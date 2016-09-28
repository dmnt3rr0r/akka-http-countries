import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import routes._

import scala.io.StdIn

/**
 * Created by donovan on 7/21/16.
 */
object Main extends Hello with Goodbye {

  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()

  override val logger = Logging(system, getClass)

  // Top level Entry Points
  val route = helloRoutes ~ goodbyeRoutes ~ {
    get {
      pathPrefix("ping") {
        complete {
          "pong!"
        }
      }
    }
  }

  def main(ags: Array[String]): Unit = {
    println("Starting countries-ms...")
    val bindingFuture = Http().bindAndHandle(route, config.getString("http.interface"), config.getInt("http.port"))

    println(s"Server online at http://" + config.getString("http.interface") + ":" + config.getInt("http.port") + "\nPress RETURN to stop...")
    StdIn.readLine()

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
