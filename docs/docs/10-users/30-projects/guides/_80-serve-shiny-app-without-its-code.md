# How to serve a Shiny app without its code

This guide shows you how to set up a Renkulab project and session launcher that serves a Shiny app. In this method, it is not necessary to include a repository with the code as part of the project.

## Clone the template repo

Clone the repo located at [https://github.com/SwissDataScienceCenter/demo-shiny-serve](https://github.com/SwissDataScienceCenter/demo-shiny-serve) and replace the contents of the `src/r/app` folder with your app.

## Build/test locally [optional]

If you have Docker installed on your machine, you can build the Dockerfile and make sure everything works. This shortens the feedback loop and helps you identify any problems quickly. The README in the linked repo explains how to do this.

## Push your changes to GitHub

Create a repository on GitHub and push your local repo to it. The repo includes actions to build a Docker image for serving your app.

In the web page for your repo, you should see a section called **Packages** in the right-hand side of the page for your repo. Click there to get the URL for the image (you will probably want to most recent one).

It will look something like: `ghcr.io/swissdatasciencecenter/demo-shiny-serve:sha-71a15ca`

## Create a Session Launcher

With the image URL, you can create a Session Launcher that serves the Shiny app your Renkulab project

Go to your Renkulab project (or create a new one, if necessary) and add a session launcher for a _Custom Environment_ with the following values:

- **Container Image**: _[the image URL from above]_
- **Port**: 3838
- **UID**: 997 (the `shiny` user in the Rocker image)
- **GID**: 997

The other values in the Session Launcher configuration can be left to their defaults/empty

## Test

You should now have a Session Launcher for your Shiny app — start the session and try it out!
