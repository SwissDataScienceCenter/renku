# Action for updating component version

This is a docker action that will update the version of a dependency in the main
Renku chart.

## Sample usage

It can simply be used as a step in a GitHub actions job:

```yaml
    steps:
    - uses: actions/checkout@v2
      with:
        fetch-depth: 0
    - uses: SwissDataScienceCenter/renku/actions/update-component-version@master
      env:
        CHART_NAME: renku-core  # name of the dependency to update
        UPSTREAM_REPO: SwissDataScienceCenter/renku  # repository to update
        GITHUB_TOKEN: ${{ secrets.CI_TOKEN }}
```

Note that the `CI_TOKEN` needs write permissions to `UPSTREAM_REPO`. All three
environment variables in the exampe above are required. `UPSTREAM_REPO` should be
given in the format `<namespace>/<name>`.
