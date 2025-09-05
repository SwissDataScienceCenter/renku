# Website

This website is built using [Docusaurus](https://docusaurus.io/), a modern static website generator.

## Installation

```bash
npm install
```

## Local Development

`cd` into the `docs` folder.

```bash
npm run start
```

This command starts a local development server and opens up a browser window. Most changes are reflected live without having to restart the server.

Another useful command is `npm run clear` (accompanied with a server restart) which purges the locally 
cached and built static files when significant changes are made but they do not
show up on the development server.

## Updating documentation

All the relevant files are in the `docs/docs` folder. You can add new pages or folders there to modify or add to the documentation.

## Build

```bash
npm run build
```

This command generates static content into the `build` directory and can be served using any static contents hosting service.

## Deployment

The documentation is deployed using [Read The Docs](https://about.readthedocs.com/). 
The [renku project](https://app.readthedocs.org/projects/renku/) on Read the Docs
builds and deploys when changes are made in the repo.
