package routes

import akka.http.scaladsl.server.Directives._

/**
  * Created by donovan on 7/21/16.
  */
trait Hello extends RootRoute {

  val helloRoutes = {
    logRequestResult("hello-routes") {
      pathPrefix("hello") {
        get {
          complete("Hello")
        }
      }
    }
  }
}
