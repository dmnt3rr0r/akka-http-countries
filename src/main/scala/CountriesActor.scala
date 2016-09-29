import akka.actor.{ Actor, ActorLogging, Props }
import akka.event.Logging
import akka.http.scaladsl._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ ContentTypes, HttpRequest, HttpResponse, StatusCodes }
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Sink
import akka.stream.{ ActorMaterializer, ActorMaterializerSettings }
import scala.collection.immutable.List
import spray.json.{ DefaultJsonProtocol, _ }
import scala.concurrent.duration._

object CountriesActor {

  val apiAll = "https://restcountries.eu/rest/v1/all"

  // Messages
  case object LoadCountries
  case object AllCountries
  case class Filter(str: String)

  // Model
  final case class Country(name: String, alpha3Code: String, region: String)
  final case class CountriesList(countries: List[Country])

  def props: Props = Props(new CountriesActor())
}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  import CountriesActor._

  implicit val countryFormat = jsonFormat3(Country)
  implicit val counriesFormat = jsonFormat1(CountriesList)

}

class CountriesActor extends Actor with ActorLogging with JsonSupport {


  import akka.pattern.pipe

  import CountriesActor._

  // state
  var countries: List[Country] = List()

  implicit val executor = context.system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val system = context.system

  override val log = Logging(context.system, this)
  val http = Http(context.system)

  override def receive = {
    case LoadCountries => {
      log.error("request to LoadContries")
      http.singleRequest(HttpRequest(uri = apiAll)).pipeTo(self)
    }

    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      Unmarshal(entity).to[List[Country]].pipeTo(self)

    case list: List[Country] => {
      countries = list
      log.info(s"Saved ${list.length} countries");
    }

    case AllCountries => sender ! countries

    case Filter(str) => sender ! countries.filter { c => c.name.toLowerCase.startsWith(str) }

    case default => log.error(s"A request came in and I don't know what it is! ${default}")

  }


}
