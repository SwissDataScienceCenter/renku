# Action for pushing images and chart with chartpress

This is a docker action that will generate images and render the chart using chartpress.

## Sample usage

It can simply be used as a step in a GitHub actions job:

```yaml
    steps:
    - uses: actions/checkout@v2
      with:
        fetch-depth: 0
    - uses: SwissDataScienceCenter/renku/actions/publish-chart@master
      env:
        CHART_DIR: helm-chart/mychart  # path to the chart directory
        GIT_USER: Bot User
        GIT_EMAIL: bot@example.com
        DOCKER_USERNAME: ${{ secrets.DOCKER_USERNAME }}
        DOCKER_PASSWORD: ${{ secrest.DOCKER_PASSWORD }}
        GITHUB_TOKEN: ${{ secrets.CI_TOKEN }}
```

Note that the `CI_TOKEN` needs write permissions to wherever the chart is
published to. All of the environment variables in the exampe above are required.
Additionaly, you may specify `HELM_URL` and `HELM_TGZ` for an alternate source
and version of the helm binary.
