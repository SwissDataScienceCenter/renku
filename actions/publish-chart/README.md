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
        CHART_PATH: helm-chart/mychart  # path to the chart directory
        DOCKER_USERNAME: ${{ secrets.DOCKER_USERNAME }}
        DOCKER_PASSWORD: ${{ secrest.DOCKER_PASSWORD }}
        GITHUB_TOKEN: ${{ secrets.CI_TOKEN }}
```

Note that the `CI_TOKEN` needs write permissions to wherever the chart is
published to.

## Configuration

You can set these environment variables:

| Variable name | Default | Required |
| --------------| --------| ---------|
| CHART_PATH    | helm-chart/ | No |
| DOCKER_PASSWORD | None | Yes |
| DOCKER_USERNAME | None | Yes |
| GIT_EMAIL     | None | Yes |
| GIT_USER      | None | Yes |
| GITHUB_TOKEN  | None | Yes |
| IMAGE_PREFIX  | None | No  |
