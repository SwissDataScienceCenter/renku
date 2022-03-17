How-to: Create a GitLab pipeline to send Renku metadata to an external triplestore 

Some users are interested in being able to write their own queries for the metadata in the triplestore. As `Renku<>`’s triplestore contains private data, an alternative solution is to simply export the data to an external triplestore. One way to accomplish this is to use a GitLab pipeline. The following GitLab pipeline configuration can be copied to any project in Renku and used to export triples to a JENA instance.

Once you have a triplestore set up, find your project in your Renku's GitLab and Click on `CI/CD` -> `Editor` and paste the following:

.. code-block:: yaml

    variables:
      GIT_STRATEGY: fetch
      GIT_SSL_NO_VERIFY: "true"
      GIT_LFS_SKIP_SMUDGE: 1
      
    image:
      name: travissdsc/pythonbase:0.0.1  # an image based on renku/renku-python which sets user to root
      entrypoint: [""]
    
    job:
      script: 
        - 'python3 -m pip install --upgrade "pip==21.3.1"'
        - 'python3 -m pip install jinja2'
        - 'python3 -m pip install --upgrade "renku==1.0.6" "sentry-sdk==0.18.0"'
        - 'git config --global user.email "yourname@example.com"' # populate this and the following line (name) with a real user. This and the following git steps are only needed if you want to use `renku migrate` 
        - 'git config --global user.name "Example User"'
        - 'git config --global filter.lfs.smudge "git-lfs smudge --skip %f"'
        - 'git config --global filter.lfs.process "git-lfs filter-process --skip"'
        - 'renku migrate --preserve-identifiers' # (optional) ensure that the data going in to jena is using the latest schema 
        - 'renku graph export --full --strict > graph.json' # Only need --full if starting from 0 in jena
        - 'curl -i -u admin:$JENA_ADMIN_PASSWORD -X  POST "https://jenatest.dev.renku.ch/omni-batch-harmony-ds/data"  -H "Content-Type:application/ld+json"     -d "@graph.json"' # send data to your triplestore
        
        
        
The above script will do the following: 

    - Installing dependencies

    - Configure git for the migration of the schema. It might be safer to do a migration manually in order to ensure that the data in the triplestore is using the same schema for all projects.

    - Export the triples and writing them to a file. 

    - Send the triples to a JENA instance. In this case the instance is `jenatest.dev.renku.ch` and the JENA dataset is `omni-batch-harmony-ds`. 
    
Modify the values
*****************
- Supply the latest versions of the above packages and the URL of your triplestore/dataset. 
- Set the JENA_ADMIN_PASSWORD variable in GitLab by going to the project page, click on `Settings` -> `CI/CD` -> `Variables` -> `Add Variable`. Be sure to select `Mask variable` so the value doesn't show up in the job logs. 
