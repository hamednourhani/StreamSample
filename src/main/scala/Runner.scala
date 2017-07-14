/**
  * Created by hamednourhani on 7/14/17.
  */

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl._

import scala.concurrent._
//import scala.util._

object Runner extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val source = Source(1 to 10)
  val flow: Flow[Int, Int, NotUsed] = Flow.fromFunction(a => a + 10)
  val flow2: Flow[Int, Int, NotUsed] = flow.map(_ - 1)
  val sink = Sink.fold[Int, Int](0)(_ + _)

  val rg: RunnableGraph[Future[Int]] = source.via(flow2).toMat(sink)(Keep.right)

  var s1 :Sink[Int,NotUsed] = Sink.onComplete(_.get)
  var s2: Sink[Int, NotUsed] = Sink.onComplete(_.get)

  val rt = RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
    val broad = b.add(Broadcast[Int](2))
    source ~> broad.in
    broad.out(0) ~> Flow[Int].map(_ + 10) ~> s1
    broad.out(1) ~> Flow[Int].map(_ + 100) ~> s2
    ClosedShape

  })
  rt.run()


  val sum: Future[Int] = rg.run()

  sum.onComplete(b => {
    println(b.getOrElse(0))
    system.terminate
  })

}
