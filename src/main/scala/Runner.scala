/**
  * Created by hamednourhani on 7/14/17.
  */
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import scala.concurrent._
import scala.util._

object Runner extends App{

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val source = Source(1 to 10)
  val sink = Sink.fold[Int,Int](0)(_-_)

  val rg : RunnableGraph[Future[Int]] = source.toMat(sink)(Keep.right)

  val sum : Future[Int] = rg.run()

  sum.map({
    case a:Int =>
      a
    case _:Any =>
      0

  }).onComplete(b => {
    println(b.get)
    system.terminate
  })







}
