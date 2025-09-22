# How to set up a Shiny app launcher

<aside>
<img src="https://www.notion.so/icons/info-alternate_gray.svg" alt="https://www.notion.so/icons/info-alternate_gray.svg" width="40px" />

If you have a shiny app that does not need any additional R packages, this how-to guide will configure that Shiny app to run in a Renku session (using R version 4.2.0).

If your Shiny app requires additional R packages, please refer instead to [How to create a custom environment and launcher for a Shiny app](How%20to%20create%20a%20custom%20environment%20and%20launcher%20fo%200a4783d76b8646ce83c97481f28f0381.md).

</aside>

1. Add a session launcher to your Renku 2.0 project with the following **Custom  Environment** option as explained in [How to use your own docker image for a Renku session](How%20to%20use%20your%20own%20docker%20image%20for%20a%20Renku%20sessi%2011f0df2efafc80af848ffcaf9ccff31c.md) considering the following parameters:
    1. **Container Image:** [`registry.renkulab.io/bethcg/shiny-launcher-project:acb9b74`](registry.renkulab.io/bethcg/shiny-launcher:b710b82)
    2. **Default URL:** `/shiny/work/<*path-to-app>*/`
        
        where  `*<path-to-app>*` should be the path to the folder of the file starting the Shiny app.
        
        Make sure to include `/shiny/work/`at the beginning and the slash at the end!
        

You can now start a session with your new shiny launcher to access the shiny app directly in your browser.