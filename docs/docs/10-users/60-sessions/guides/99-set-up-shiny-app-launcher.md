# How to set up a Shiny app launcher

:::info

If you have a shiny app that does not need any additional R packages, this how-to guide will configure that Shiny app to run in a Renku session (using R version 4.2.0).

If your Shiny app requires additional R packages, please refer instead to [How to create a custom environment and launcher for a Shiny app](/docs/users/sessions/guides/create-custom-environment-and-launcher-for-shiny-app).

:::

1. Add a session launcher to your Renku project with the following **Custom  Environment** option as explained in [How to use your own docker image for a Renku session](/docs/users/sessions/guides/use-your-own-docker-image-for-renku-session) considering the following parameters:
    1. **Container Image:** [`http://registry.renkulab.io/bethcg/shiny-launcher-project:acb9b74`](http://registry.renkulab.io/bethcg/shiny-launcher:b710b82)
    2. **Default URL:** `/shiny/work/<*path-to-app>*/`
        
        where  `*<path-to-app>*` should be the path to the folder of the file starting the Shiny app.
        
        Make sure to include `/shiny/work/`at the beginning and the slash at the end!
        

You can now start a session with your new shiny launcher to access the shiny app directly in your browser.
