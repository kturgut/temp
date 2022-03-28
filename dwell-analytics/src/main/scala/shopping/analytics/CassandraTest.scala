//package shopping.analytics
//
//
//import akka.projection.ProjectionId
//import akka.projection.cassandra.scaladsl.CassandraProjection
//
//object CassandraTest {
//
//
//
//  val projection =
//    CassandraProjection
//      .atLeastOnce(
//        projectionId = ProjectionId("shopping-carts", "carts-1"),
//        sourceProvider,
//        handler = () => new ShoppingCartHandler)
//      .withSaveOffset(afterEnvelopes = 100, afterDuration = 500.millis)
//
////  val projection =
////    CassandraProjection.atMostOnce(
////      projectionId = ProjectionId("shopping-carts", "carts-1"),
////      sourceProvider,
////      handler = () => new ShoppingCartHandler)
//}
//
//
//import scala.concurrent.duration._
//import scala.concurrent.Future
//
//import akka.Done
//import akka.projection.scaladsl.Handler
//import org.slf4j.LoggerFactory
//
//class ShoppingCartHandler extends Handler[EventEnvelope[ShoppingCart.Event]] {
//  private val logger = LoggerFactory.getLogger(getClass)
//
//  override def process(envelope: EventEnvelope[ShoppingCart.Event]): Future[Done] = {
//    envelope.event match {
//      case ShoppingCart.CheckedOut(cartId, time) =>
//        logger.info2("Shopping cart {} was checked out at {}", cartId, time)
//        Future.successful(Done)
//
//      case otherEvent =>
//        logger.debug2("Shopping cart {} changed by {}", otherEvent.cartId, otherEvent)
//        Future.successful(Done)
//    }
//  }
//}
//
//import scala.collection.immutable
//
//class GroupedShoppingCartHandler extends Handler[immutable.Seq[EventEnvelope[ShoppingCart.Event]]] {
//  private val logger = LoggerFactory.getLogger(getClass)
//
//  override def process(envelopes: immutable.Seq[EventEnvelope[ShoppingCart.Event]]): Future[Done] = {
//    envelopes.map(_.event).foreach {
//      case ShoppingCart.CheckedOut(cartId, time) =>
//        logger.info2("Shopping cart {} was checked out at {}", cartId, time)
//
//      case otherEvent =>
//        logger.debug2("Shopping cart {} changed by {}", otherEvent.cartId, otherEvent)
//    }
//    Future.successful(Done)
//  }
//}
//
//
//class WordCountHandler extends Handler[WordEnvelope] {
//  private val logger = LoggerFactory.getLogger(getClass)
//  private var state: Map[Word, Count] = Map.empty
//
//  override def process(envelope: WordEnvelope): Future[Done] = {
//    val word = envelope.word
//    val newCount = state.getOrElse(word, 0) + 1
//    logger.info("Word count for {} is {}", word, newCount)
//    state = state.updated(word, newCount)
//    Future.successful(Done)
//  }
//}
//
//type Word = String
//type Count = Int
//
//final case class WordEnvelope(offset: Long, word: Word)
//
//class WordSource(implicit ec: ExecutionContext) extends SourceProvider[Long, WordEnvelope] {
//
//  private val src = Source(
//    List(WordEnvelope(1L, "abc"), WordEnvelope(2L, "def"), WordEnvelope(3L, "ghi"), WordEnvelope(4L, "abc")))
//
//  override def source(offset: () => Future[Option[Long]]): Future[Source[WordEnvelope, NotUsed]] = {
//    offset().map {
//      case Some(o) => src.dropWhile(_.offset <= o)
//      case _       => src
//    }
//  }
//
//  override def extractOffset(env: WordEnvelope): Long = env.offset
//
//  override def extractCreationTime(env: WordEnvelope): Long = 0L
//}
//
//
//trait WordCountRepository {
//  def load(id: String, word: Word): Future[Count]
//  def loadAll(id: String): Future[Map[Word, Count]]
//  def save(id: String, word: Word, count: Count): Future[Done]
//}