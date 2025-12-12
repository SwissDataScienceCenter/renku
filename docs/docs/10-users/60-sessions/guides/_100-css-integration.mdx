import { Columns, Column } from '@theme/MDXComponents';

# RenkuLab: CSCS integration

:::info
The RenkuLab: CSCS integration allows users to start interactive Renku sessions running as HPC jobs at CSCS.

RenkuLab accesses compute and storage resources on the behalf of the user, using existing permissions and resource allocation. Running Renku sessions at CSCS will consume compute credits from your CSCS account(s) and may affect other resources you have at CSCS (e.g. storage).

:::

# Instructions

### Prerequisites

1. You need to have an account at [CSCS](https://cscs.ch) which can launch jobs on the [HPC platform](https://docs.cscs.ch/platforms/hpcp/) or the [ML platform](https://docs.cscs.ch/platforms/mlp/).
2. You need to have a [RenkuLab.io](https://renkulab.io) account.

### Step 1: Connect your CSCS from RenkuLab

Go to the [integrations page](https://renkulab.io/integrations?targetProvider=cscs.ch) and connect with the CSCS integration. 

### Step 2: Complete the  [Request access to the Renku:CSCS Integration](https://www.notion.so/2810df2efafc803a8175ccd2d472b23d?pvs=21)

Fill in the form so that a RenkuLab admin can let you launch sessions with the CSCS integration.

### Step 3: Launch a session

**Option 1: Try out a template**

1. Go to [this Renku project](https://renkulab.io/p/renku-team/cscs-starter-project) and click on the **Copy project** green button.
2. 🚀 Launch a session on the **Python Basic - VSCodium - Eiger** launcher!

**Option 2: Create your own CSCS launcher in your project**

1. Create a **new Renku project**
2. Add a new **session launcher** 
3. For the launcher’s environment:
    1. If you want to **Create from Code**, please note that only Python environments work on CSCS. If you’re unfamiliar with creating a code-based environment on Renku, see [How to create an environment with custom packages installed](/docs/users/sessions/guides/create-environment-with-custom-packages-installed)
    2. If you want to use the Renku **Global Environments**, select **Python Basic** or **Python Datascience**
        
        
        :::warning
        Do not pick Python/Jupyter, Virtual desktop or RStudio.
        :::

        
    3. Please note that **External Environments** do not work with the CSCS integration. 

    
4. When selecting Session launcher compute resources, open the drop down and select a **CSCS resource class**, such as the `Eiger - Debug` resource class inside the `CSCS - Eiger - Debug` resource pool!

5. Note: By default, the job is submitted against your default CSCS account
        
        <details>
        <summary>*If you’d like the job go to a different account…*</summary>

                <p>
                1. Open your session launcher side panel
                2. Scroll down to the Environment variables section
                3. Add an environment variable with key `SLURM_ACCOUNT` and set the value to your CSCS organisation (e.g. `g159`). 
                </p>

                :::note
                [See the example Project "Demo HPC"](https://staging.dev.renku.ch/p/flora.thiebaut/demo-hpc#launcher-01K58XQSDV5HFF0T9A6D1G16ZF)

                 <p class="image-container-l">
                 ![image.png](./example-demo-hpc.png)     
                </p>

                :::

                :::note

                Note: If you see the following screen, wait a minute and try to refresh the page. The session is still starting at CSCS. 

                <p class="image-container-l">
                ![image.png](./session-starting.png)
                </p>

                :::
                
                :::warning

                After you shut down your sessions, **make sure that there is no running job leftover at CSCS**. Session shut down is not 100% reliable right now.

                - Go to https://my.hpcp.cscs.ch/compute (or https://my.mlp.cscs.ch/compute) and check that no job is running

                :::

        </details>

------

<div style={{ display: "flex", gap: "30px", paddingLeft: "20px", }}>
## What works & What doesn’t work yet
</div>

<div style={{ display: "flex", gap: "30px", paddingLeft: "20px" }}>
  <div style={{ flex: 1, padding: "20px", border: '1px solid #ccc' }}>

    **Currently working**
    
    :white_check_mark: Launch interactive Renku sessions at CSCS on: `Eiger` and `Bristen`
        - Sessions on `Daint` , `Clariden` work, but only with special images created by the Renku team. Simplified integration with ARM clusters is coming soon. Please [Contact us](/docs/users/community) for more info.

    :white_check_mark: Access data from scratch, store and home

   :white_check_mark: By default, the job is submitted against your default CSCS account

                    <details>
                    <summary>*If you’d like the job go to a different account...*</summary>
                    <p>
                    1. Open your session launcher side panel
                    2. Scroll down to the Environment variables section
                    3. Add an environment variable with key `SLURM_ACCOUNT` and set the value to your CSCS organisation (e.g. `g159`).
                    </p>
                    [See the example Project "Demo HPC"](https://staging.dev.renku.ch/p/flora.thiebaut/demo-hpc#launcher-01K58XQSDV5HFF0T9A6D1G16ZF)

                    ![image.png](./env-var.png)     
                    </details>

    :white_check_mark: “Create from code” environments and Global environments work (on Eiger only)

                    <details>
                    <summary>*If you’d like to test Daint outside of the pre-prepared project...*</summary>
                    <p>
                    At the moment, only special images work on Daint. The [testing project](https://staging.dev.renku.ch/p/flora.thiebaut/demo-hpc-clean#launcher-01K604HWZBNTTVD9G8VKATVJ8Z) comes with a launcher suitable for Daint. Here’s how to test on Daint in your own project (we will integrate this into RenkuLab more properly later!):      
                    1. Create a session launcher with an **External Environment**
                    2. For the configuration, copy configuration from [this session launcher](https://staging.dev.renku.ch/p/flora.thiebaut/demo-hpc#launcher-01K5XTKBQK4XCCB6WTAQ7AVZJZ).
                    3. Ask to be added to a Daint resource pool
                    </p> 
                    Tested: has torch and reports cuda access to 4 GPUs
                    </details>

    - Code repositories added to the Renku project are added to your session

  </div>

  <div style={{ flex: 1, padding: "20px", border: '1px solid #ccc' }}>
    **Not working at the moment**

    <p>:x: Renku data connectors</p>
    <p>:x: External environments (bringing your own docker image)</p>
    <p>:x: Renku session secrets</p>
    <p>:x: Configuring the max run time of the job</p>
    <p>:x: Submitting slurm jobs from within the session</p>

  </div>
</div>

------ 

# How does this work? Where can I see my things at CSCS?

In the Renku session logs, lookout for the following logs:

```json
{"time":"2025-09-25T09:30:12.370016834Z","level":"INFO","msg":"determined session path","sessionPath":"/capstor/scratch/cscs/fthiebau/renku/sessions/flora.thiebaut/demo-hpc/flora-thieba-ff7d0b715ab8"}
...
{"time":"2025-09-25T09:30:19.100336894Z","level":"INFO","msg":"submitted job","jobID":"5692630"}
```

Then you can go to the Renku session folder:

```json
$ ssh eiger
[eiger][fthiebau@eiger-ln004 ~]$ cd /capstor/scratch/cscs/fthiebau/renku/sessions/flora.thiebaut/demo-hpc/flora-thieba-ff7d0b715ab8
```

- The `slurm-<JOB_ID>.out` file contains the logs of the session.
- The `session_script.sh` file contains the script submitted to SLURM
    - you should see `#SBATCH --account=<ACCOUNT>` at the top if you set it
- The `environment.toml` file is used to start the container, see https://docs.cscs.ch/software/container-engine/
- The `work` folder is the working directory inside the session. If you saved any files during the session (e.g. notebooks), they will be found here.