package routes

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.stream.Materializer
import com.typesafe.config.Config

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by donovan on 7/21/16.
  */
trait RootRoute {

  implicit val system: ActorSystem
  implicit val materializer: Materializer
  implicit val executor: ExecutionContextExecutor

  val logger: LoggingAdapter
  val config: Config


}
