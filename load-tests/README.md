# Load testing with k6

## To run

1. Download k6: https://k6.io/docs/get-started/installation/ - you can also get a binary from the GitHub page
2. Enter the credentials for logging in a file named `credentials.json`, you can
   use the `credentials.example.json` to start - just make a copy and rename it.
3. Run the tests with `k6 run -e BASE_URL=https://dev.renku.ch -e GIT_URL="https://gitlab.dev.renku.ch" -e CREDENTIALS_FILE=./credentials.json -e REGISTRY_DOMAIN=registry.dev.renku.ch scenarioLecture.js`

## Limitations

- This can log into Renku only and specifically in the cases where Renku has its own built-in gitlab that does
  not require a separate login OR when the gitlab deployment is part of another renku deployment
- The login flow cannot handle giving authorization when prompted in the oauth flow - do this
  for the first time manually then run the tests with the same account
