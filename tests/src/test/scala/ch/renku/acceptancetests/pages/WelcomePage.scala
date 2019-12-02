package ch.renku.acceptancetests.pages

import ch.renku.acceptancetests.pages.Page._
import eu.timepit.refined.auto._

case object WelcomePage extends RenkuPage with TopBar {
  override val path:  Path  = "/"
  override val title: Title = "Renku"
}
