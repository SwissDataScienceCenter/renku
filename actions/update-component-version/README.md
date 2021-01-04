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
        GITHUB_TOKEN: ${{ secrets.CI_TOKEN }}
```

Note that the `CI_TOKEN` needs write permissions to your upstream repository.

## Configuration

You can set these environment variables:

| Variable name | Default |
| --------------| --------|
| CHART_NAME    | name of the repository |
| UPSTREAM_REPO | SwissDataScienceCenter/renku |
| GIT_EMAIL     | renku@datascience.ch |
| GIT_USER      | Renku Bot |
