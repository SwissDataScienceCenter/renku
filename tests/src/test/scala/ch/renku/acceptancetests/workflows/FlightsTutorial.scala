package ch.renku.acceptancetests.workflows

import java.nio.file.Path

import ch.renku.acceptancetests.model.datasets.DatasetName
import ch.renku.acceptancetests.model.projects.ProjectUrl
import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.tooling._
import ch.renku.acceptancetests.tooling.console._
import eu.timepit.refined.auto._
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers => ScalatestMatchers}

trait FlightsTutorial extends GivenWhenThen with Matchers with ScalatestMatchers with CLIConfiguration {
  self: FeatureSpec =>

  def followTheFlightsTutorialOnUsersMachine(
      projectUrl:             ProjectUrl
  )(implicit userCredentials: UserCredentials, renkuCliConfig: RenkuCliConfig): DatasetName = {

    implicit val projectFolder: Path = createTempFolder

    `verify renku version`

    `setup git configuration`

    When("the user clones the project locally")
    console %> c"git clone ${projectUrl.addGitCredentials} $projectFolder"

    And("enables git lfs")
    console %> c"git lfs install --local"

    And("installs virtualenv")
    console %> c"python3 -m pip install virtualenv"

    And("creates virtualenv directory")
    console %> c"virtualenv venv"


    And("adds some Python packages to the requirements.txt")
    console %> (c"echo pandas==0.25.3" >> "requirements.txt")
    console %> (c"echo seaborn==0.9.0" >> "requirements.txt")
    console %> (c"echo renku==0.10.4" >> "requirements.txt")
    console %> (c"echo papermill<1.2.0" >> "requirements.txt")
    console %> (c"echo requests>=2.20.0" >> "requirements.txt")
    console %> (c"echo jupyterhub==1.1.0" >> "requirements.txt")
    console %> (c"echo nbresuse==0.3.3" >> "requirements.txt")
    console %> (c"echo jupyterlab-git==0.20.0" >> "requirements.txt")
    console %> (c"echo renku==0.10.4" >> "requirements.txt")


    And("installs the packages")
    console %> c"./venv/bin/pip install -r requirements.txt"

    console %> c"git add requirements.txt"
    console %> c"git commit -m 'Installed pandas and seaborn'"

    And("imports a dataset from the dataverse")
    console %> c"./venv/bin/python -m renku.cli dataset import 10.7910/DVN/WTZS4K".userInput("y")

    And("downloads the tutorial step 1 notebook")
    console %> c"wget -O $projectFolder/notebooks/00-FilterFlights.ipynb https://renkulab.io/gitlab/renku-tutorial/renku-tutorial-flights/raw/master/.tutorial/meta/templates/00-FilterFlights-doi.ipynb"
    And("commits it")
    console %> c"git add notebooks"
    console %> c"git commit -m 'Created notebook to filter flights to AUS, TX.'"

    And("downloads the tutorial step 1 python file")
    console %> c"mkdir $projectFolder/src"
    console %> c"wget -O $projectFolder/src/00-FilterFlights.py https://renkulab.io/gitlab/renku-tutorial/renku-tutorial-flights/raw/master/.tutorial/meta/templates/00-FilterFlights.py"

    And("commits it")
    console %> c"git add src"
    console %> c"git commit -m 'Extracted logic from FilterFlights notebook into script.'"

    And("runs step 1")
    console %> c"mkdir $projectFolder/data/output"
    console %> c"./venv/bin/python -m renku.cli run ./venv/bin/python src/00-FilterFlights.py data/201901_us_flights_1/2019-01-flights.csv.zip data/output/2019-01-flights-filtered.csv"

    And("downloads the tutorial step 2 notebook")
    console %> c"wget -O $projectFolder/notebooks/01-CountFlights.ipynb https://renkulab.io/gitlab/renku-tutorial/renku-tutorial-flights/raw/master/.tutorial/meta/templates/01-CountFlights.ipynb"
    And("commits it")
    console %> c"git add notebooks"
    console %> c"git commit -m 'Created notebook to count flights.'"

    And("runs the notebook")
    console %>
      c"""
      |./venv/bin/python -m renku.cli run ./venv/bin/papermill
      |notebooks/01-CountFlights.ipynb
      |notebooks/01-CountFlights.ran.ipynb
      |-p input_path data/output/2019-01-flights-filtered.csv
      |-p output_path data/output/2019-01-flights-count.txt"""

    And("updates the code")
    console %> c"sed -ie 's/DFW/AUS/g' $projectFolder/src/00-FilterFlights.py"
    console %> c"rm src/00-FilterFlights.pye"
    console %> c"git add src/00-FilterFlights.py"
    console %> c"git commit -m 'Fixed filter to use AUS, not DFW.'"

    And("and updates the results")
    console %> c"./venv/bin/python -m renku.cli update"
    And("pushes all the commits to the remote")
    console %> c"git push"

    Then("the user is done with the basic workflow")
    DatasetName("2019-01 US Flights")
  }
}
