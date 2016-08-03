package routes

import akka.http.scaladsl.server.Directives._

/**
  * Created by donovan on 7/21/16.
  */
trait Goodbye extends RootRoute {

  val goodbyeRoutes = {
    logRequestResult("goodbye-service") {
      pathPrefix("goodbye") {
        get {
          complete("Goodbye, Love you!")
        }
      }
    }
  }
}
