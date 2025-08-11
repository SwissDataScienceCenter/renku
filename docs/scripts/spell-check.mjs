// From https://chris-m-whong.medium.com/spell-check-your-docusaurus-site-with-unified-engine-and-retext-bea7373bc380

import { engine } from 'unified-engine';
import { unified } from 'unified';
import remarkParse from 'remark-parse';
import remarkRetext from 'remark-retext';
import retextEnglish from 'retext-english';
import retextSpell from 'retext-spell';
import dictionary from 'dictionary-en';

engine(
  {
    processor: unified(),
    files: ['./docs/**/*.md', './docs/**/*.mdx', './docs/**/*.yml'],
    color: true,
    defaultConfig: {
      plugins: [                       // define the process
        remarkParse,                   // parse markdown/mdx
        [
          remarkRetext,                // extract text from the parsed markdown
          unified().use({              // pass the text into a retext process          
            plugins: [
              retextEnglish,         // parse english text
              [
                retextSpell,         // spell check the text against the dictionary
                {
                  dictionary,
                  personal: [        // define a personal dictionary
                    'Alertmanager',
                    'Authzed',
                    'autoscaling',
                    'backend',
                    'CLI',
                    'Cronjob',
                    'Cronjobs',
                    'docusaurus',
                    'dropdown',
                    'filesystem',
                    'frontend',
                    'Github',
                    'Gitops',
                    'Grafana',
                    'ingress-nginx',
                    'Init',
                    'integrations',
                    'k8s',
                    'k9s',
                    'Keycloak',
                    'kubectl',
                    'kubectx',
                    'kubens',
                    'Kubernetes',
                    'namespace',
                    'OKD',
                    'Openshift',
                    'Postgres',
                    'Prometheus',
                    'pre-installed',
                    'Renku',
                    'Solr',
                    'starship',
                    'StatefulSet',
                    'templated',
                    'TLS',
                    'UI',
                    'yaml',
                  ].join('\n')
                }
              ]
            ]
          })
        ]
      ]
    }
  },
  (error, code) => {
    if (error) console.error(error);
    process.exit(code);
  }
);
