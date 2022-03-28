//package shopping.analytics
//
//import akka.actor.typed.ActorSystem
//import akka.kafka.ConsumerSettings
//import akka.projection.MergeableOffset
//import akka.projection.kafka.scaladsl.KafkaSourceProvider
//import akka.projection.scaladsl.SourceProvider
//import org.apache.kafka.clients.consumer.ConsumerConfig
//import org.apache.kafka.clients.consumer.ConsumerRecord
//import org.apache.kafka.common.serialization.StringDeserializer
//
//
//object DwellKafkaSource {
//
//  val bootstrapServers = "localhost:9092"
//  val groupId = "group-wordcount"
//  val topicName = "words"
//
//  def init(system: ActorSystem[_]) = {
//    val consumerSettings =
//      ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
//        .withBootstrapServers(bootstrapServers)
//        .withGroupId(groupId)
//        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
//
//    val sourceProvider: SourceProvider[MergeableOffset[Long], ConsumerRecord[String, String]] =
//      KafkaSourceProvider(system, consumerSettings, Set(topicName))
//  }
//
//}
//
//
//object DwellSourceProjection {
//  val sessionProvider = new HibernateSessionFactory
//
//  val projectionId = ProjectionId("WordCount", "wordcount-1")
//  val projection =
//    JdbcProjection.exactlyOnce(
//      projectionId,
//      sourceProvider,
//      () => sessionProvider.newInstance(),
//      handler = () => new WordCountJdbcHandler(wordRepository))
//
//}