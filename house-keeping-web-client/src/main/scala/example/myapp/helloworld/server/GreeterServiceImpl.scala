//package example.myapp.helloworld.server
//
//
//import akka.NotUsed
//import akka.actor.typed.ActorSystem
//import akka.stream.Materializer
//import akka.stream.scaladsl.{Sink, Source}
//import com.google.protobuf.timestamp.Timestamp
//import example.myapp.helloworld.grpc._
//
//class GreeterServiceImpl(system: ActorSystem[_]) extends GreeterService {
//
//  val greeterActor = system.actorOf(GreeterActor.props("Hello"), "greeter")
//
//  def sayHello(in: HelloRequest): Future[HelloReply] = {
//    // timeout and execution context for ask
//    implicit val timeout: Timeout = 3.seconds
//    import system.dispatcher
//
//    (greeterActor ? GreeterActor.GetGreeting)
//      .mapTo[GreeterActor.Greeting]
//      .map(message => HelloReply(s"${message.greeting}, ${in.name}"))
//  }
//
//  def changeGreeting(in: ChangeRequest): Future[ChangeResponse] = {
//    greeterActor ! GreeterActor.ChangeGreeting(in.newGreeting)
//    Future.successful(ChangeResponse())
//  }
//}