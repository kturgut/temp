//package example.myapp.helloworld.server
//
//import akka.actor.ActorSystem
//import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
//import akka.http.scaladsl.Http
//import com.typesafe.config.ConfigFactory
//import example.myapp.helloworld.grpc._
//
//import scala.concurrent.{ExecutionContext, Future}
//import akka.grpc.scaladsl.ServiceHandler
//import akka.http.javadsl.server.Directives.reject
//import akka.http.scaladsl.server.Directives.{complete, handle, headerValueByName, pass, path}
//import akka.http.scaladsl.server.{Directive0, Directives, Route}
//
//object GreeterServer {
//  def main(args: Array[String]): Unit = {
//    // Important: enable HTTP/2 in ActorSystem's config
//    // We do it here programmatically, but you can also set it in the application.conf
//    val conf = ConfigFactory
//      .parseString("akka.http.server.preview.enable-http2 = on")
//      .withFallback(ConfigFactory.defaultApplication())
//    val system = ActorSystem("HelloWorld", conf)
//    new GreeterServer(system).run()
//    // ActorSystem threads will keep the app alive until `system.terminate()` is called
//  }
//}
//
//class GreeterServer(system: ActorSystem) {
//  def run(): Future[Http.ServerBinding] = {
//    // Akka boot up code
//    implicit val sys: ActorSystem = system
//    implicit val ec: ExecutionContext = sys.dispatcher
//
//    // A Route to authenticate with
//    val authenticationRoute: Route = path("login") {
//      Directives.get {
//        complete("Psst, please use token XYZ!")
//      }
//    }
//
//    // Create service handlers
//    val handler: HttpRequest => Future[HttpResponse] =
//      GreeterServiceHandler(new GreeterServiceImpl())
//
//    // As a Route
//    val handlerRoute: Route = handle(handler)
//
//    // A directive to authorize calls
//    val authorizationDirective: Directive0 =
//      headerValueByName("token").flatMap { token =>
//        if (token == "XYZ") pass
//        else reject
//      }
//
//    val route = Directives.concat(
//      authenticationRoute,
//      authorizationDirective {
//        handlerRoute
//      })
//
//    // Create service handlers
//    val service: HttpRequest => Future[HttpResponse] =
//      GreeterServiceHandler(new GreeterServiceStatelessImpl())
//
//    // Bind service handler servers to localhost:8082
//    val binding = Http().newServerAt("127.0.0.1", 8082).bind(route)
//
//
////    // Bind service handler servers to localhost:8080/8081
////    val binding = Http().newServerAt("127.0.0.1", 8080).bind(service)
////
//
//    // report successful binding
//    binding.foreach { binding => println(s"gRPC server bound to: ${binding.localAddress}") }
//
//    binding
//  }
//
////  def runServingMultipleServices(): Future[Http.ServerBinding] = {
////    // explicit types not needed but included in example for clarity
////    val greeterService: PartialFunction[HttpRequest, Future[HttpResponse]] =
////      example.myapp.helloworld.grpc.GreeterServiceHandler.partial(new GreeterServiceImpl())
////    val echoService: PartialFunction[HttpRequest, Future[HttpResponse]] =
////      EchoServiceHandler.partial(new EchoServiceImpl)
////    val reflectionService = ServerReflection.partial(List(GreeterService, EchoService))
////    val serviceHandlers: HttpRequest => Future[HttpResponse] =
////      ServiceHandler.concatOrNotFound(greeterService, echoService, reflectionService)
////
////    Http()
////      .newServerAt("127.0.0.1", 8080)
////      .bind(serviceHandlers)
////  }
//}