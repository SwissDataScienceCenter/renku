package init

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by johann on 13/04/17.
  */
object Main {

  def main(args: Array[String]): Unit = {

    val f = CreateTables.createTables()
    Await.result(f, Duration.Inf)

  }

}
