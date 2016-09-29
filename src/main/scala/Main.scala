import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import routes._

import scala.io.StdIn

/**
 * Created by donovan on 7/21/16.
 */
object Main extends Hello with Goodbye with JsonSupport {

  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  implicit val timeout = Timeout(5 seconds)

  override val config = ConfigFactory.load()

  override val logger = Logging(system, getClass)

  import CountriesActor._

  lazy val countriesActor = system.actorOf(CountriesActor.props, "countriesService")

  // schedule the loading of countries
  val cancellableLoadSchedule = system.scheduler.schedule(0 milliseconds, 4 hours, countriesActor, LoadCountries)

  // Top level Entry Points
  val route = helloRoutes ~ goodbyeRoutes ~ {
    get {
      pathPrefix("ping") {
        complete {
          "pong!"
        }
      } ~
      pathPrefix("all") {
        onSuccess((countriesActor ? AllCountries).mapTo[List[Country]]) { c => complete(c) }
      } ~
      pathPrefix("load") {
        complete {
          countriesActor ! LoadCountries
          "okay"
        }
      } ~
      path("filter" / Remaining) { str =>
        onSuccess((countriesActor ? Filter(str)).mapTo[List[Country]]) { c => complete(c) }
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
