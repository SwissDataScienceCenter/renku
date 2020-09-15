package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.pages.JupyterLabPage
import ch.renku.acceptancetests.tooling.AcceptanceSpec
import ch.renku.acceptancetests.model.datasets.DatasetName

import scala.concurrent.duration._
import scala.language.postfixOps
import eu.timepit.refined.auto._

trait FlightsTutorialJupyterLab {
  self: AcceptanceSpec =>

  def followFlightsTutorialInJupyterLab(jupyterLabPage: JupyterLabPage): DatasetName = {
    import jupyterLabPage.terminal

    // Create or import the dataset
    val createDataSet = false
    if (createDataSet) {
      And("creates a dataset")
      terminal %> "renku dataset create flights" sleep (2 seconds)
      And("adds data to the dataset")
      terminal %> "renku dataset add flights https://renkulab.io/gitlab/cramakri/renku-tutorial-flights-data-raw/raw/master/data/v1/2019-01-flights.csv.zip" sleep (13 seconds)
    } else {
      And("imports a dataset from the dataverse")
      terminal %> "yes 2>/dev/null | renku dataset import 10.7910/DVN/WTZS4K" sleep (15 seconds)
    }

    And("pushes the changes to the remote")
    terminal %> "git push" sleep (10 seconds)
    And("adds some Python packages to the requirements.txt")
    terminal %> "echo pandas==0.25.3 >> requirements.txt" sleep (1 second)
    terminal %> "echo seaborn==0.9.0 >> requirements.txt" sleep (1 second)
    And("installs the packages")
    terminal %> "pip install -r requirements.txt" sleep (1 minute)
    terminal %> "git add requirements.txt" sleep (1 second)
    terminal %> "git commit -m 'Installed pandas and seaborn'" sleep (1 second)
    terminal %> "git push" sleep (5 seconds)
    And("downloads the tutorial step 1 notebook")
    terminal %> "wget -O notebooks/00-FilterFlights.ipynb https://renkulab.io/gitlab/renku-tutorial/renku-tutorial-flights/raw/master/.tutorial/meta/templates/00-FilterFlights-doi.ipynb" sleep (8 seconds)
    And("commits it")
    terminal %> "git add notebooks" sleep (1 second)
    terminal %> "git commit -m 'Created notebook to filter flights to AUS, TX.'" sleep (1 second)
    terminal %> "git push" sleep (5 seconds)
    And("downloads the tutorial step 1 python file")
    terminal %> "mkdir src" sleep (1 second)
    terminal %> "wget -O src/00-FilterFlights.py https://renkulab.io/gitlab/renku-tutorial/renku-tutorial-flights/raw/master/.tutorial/meta/templates/00-FilterFlights.py" sleep (8 seconds)
    And("commits it")
    terminal %> "git add src" sleep (1 second)
    terminal %> "git commit -m 'Extracted logic from FilterFlights notebook into script.'" sleep (1 second)
    terminal %> "git push" sleep (5 seconds)
    And("runs step 1")
    terminal %> "mkdir data/output" sleep (1 second)
    terminal %> "renku run python src/00-FilterFlights.py data/201901_us_flights_1/2019-01-flights.csv.zip data/output/2019-01-flights-filtered.csv" sleep (17 seconds)
    And("downloads the tutorial step 2 notebook")
    terminal %> "wget -O notebooks/01-CountFlights.ipynb https://renkulab.io/gitlab/renku-tutorial/renku-tutorial-flights/raw/master/.tutorial/meta/templates/01-CountFlights.ipynb" sleep (8 seconds)
    And("commits it")
    terminal %> "git add notebooks" sleep (1 second)
    terminal %> "git commit -m 'Created notebook to count flights.'" sleep (1 second)
    terminal %> "git push" sleep (5 seconds)
    And("runs the notebook")
    terminal %> """renku run papermill \
notebooks/01-CountFlights.ipynb \
notebooks/01-CountFlights.ran.ipynb \
-p input_path data/output/2019-01-flights-filtered.csv  \
-p output_path data/output/2019-01-flights-count.txt""" sleep (17 seconds)
    And("pushes the results to the remote")
    terminal %> "git push" sleep (10 seconds)
    And("updates the code")
    terminal %> "sed -ie 's/DFW/AUS/g' src/00-FilterFlights.py"
    terminal %> "rm src/00-FilterFlights.pye"
    terminal %> "git add src/00-FilterFlights.py"
    terminal %> "git commit -m 'Fixed filter to use AUS, not DFW.'"
    And("and updates the results")
    terminal %> "renku update" sleep (10 seconds)
    And("pushes the results to the remote")
    terminal %> "git push" sleep (10 seconds)

    Then("the user is done with the basic workflow")
    DatasetName("2019-01 US Flights")
  }
}
