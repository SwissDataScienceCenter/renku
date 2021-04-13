# Check PR description

This action looks for a specific string in the target PR description and an
optional command following it.

## Command

The command is made by multiple sections separated by a space, and it terminates
with the end of the line.

A section is made by one of the following:

- A renku component name followed by `=<ref>`, where `<ref>` is any valid reference
  like a tag or a branch name (E.G. `renku-ui=0.11.9` or
  `renku-notebooks=debug-with-vscode-k8s`).
- The string `#notest`.

The supported components are:
- `renku`
- `renku-core`
- `renku-gateway`
- `renku-graph`
- `renku-notebooks`
- `renku-ui` (that includes the `renku-ui-server` component, since the version is
  aligned between the 2 components)

The reference will be stored in an output variable with the same name.

The `#notest` string will falsify the otherwise truthy variable named `test-enabled`.

Keep in mind that:
- Unknown sections are skipped.
- The order plays no role unless you define multiple references for the same
  component. In that case, only the first is considered. It would be best if you
  tried to avoid any repetition.

## How to use that

This action is meant to be used in combination with the other actions from this
repository to simplify deploying a testing environment for pull requests.

The string should look like the following:

```
/deploy #notest renku-ui=0.11.9
```

Further information can be found here:
https://renku.readthedocs.io/en/latest/developer/contributing/pull-requests.html
