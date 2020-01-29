package ch.renku.acceptancetests.pages

import org.openqa.selenium.{WebDriver, WebElement}
import org.scalatestplus.selenium.WebBrowser.{cssSelector, find}

trait TopBar {
  self: RenkuPage =>

  object TopBar {

    def projects(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("ul.navbar-nav.mr-auto a[href='/projects']")) getOrElse fail(
        "Top Right 'Projects' link not found"
      )
    }

    def plusDropdown(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("li[class*='nav-item dropdown']:nth-child(1) a[class*='dropdown-toggle']"))
        .getOrElse(fail("Top Right '+' not found"))
    }

    def projectOption(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("a[href='/project_new']")) getOrElse fail("Project option not found")
    }

    def topRightDropDown(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("li[class*='nav-item dropdown']:nth-child(2) a[class*='dropdown-toggle']"))
        .getOrElse(fail("Top Right dropdown not found"))
    }

    def logoutLink(implicit webDriver: WebDriver): WebElement = eventually {
      find(cssSelector("a[href*='/auth/logout']")) getOrElse fail("Logout link not found")
    }
  }
}
