import org.openqa.selenium.WebDriver
import org.scalatestplus.selenium.Chrome

/**
  * Import packages necessary for working with selenium in the console.
  *
  * To get started,
  * - run `sbt test:console`
  * - :load src/test/scala/ch/renku/acceptancetests/ReplShell.scala
  *
  * Then you can execute statements like
  * ```
  * val webDriver = ReplShell.getChromeWebDriver("https://dev.renku.ch")
  * val loginButtons = webDriver.findElements(By.cssSelector("a[href='/login']"))
  * ```
  */
object ReplShell {

  def getChromeWebDriver(url: String): WebDriver = {
    val driver = Chrome.webDriver
    driver.get(url)
    driver
  }

}
