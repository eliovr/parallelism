/**
* Author: Elio Ventocilla
*/
package parallelism

import Utils._
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Fork extends App {
  /**
  * Fork is one way of doing two operations in parallel.
  */
  def fork[A, B](a: => A, b: => B): (A, B) = {
      val f = Future { b }
      val r1 = a
      val r2 = Await.result(f, 5.seconds)
      (r1, r2)
  }

  val elems = 150000
  val rand = new scala.util.Random()
  val ns = (1 to elems).map(_ => rand.nextInt(elems)).toVector
  val (ns1, ns2) = ns.splitAt(ns.size / 2)
  val (ns1a, ns1b) = ns1.splitAt(ns1.size / 2)
  val (ns2a, ns2b) = ns2.splitAt(ns2.size / 2)


  println("Running sequential operations:")
  var t = time(ns.count(isPrime) / elems.toDouble)
  println(s"\t- time: ${t._2}, probability of prime: ${t._1}")


  println("Running 2 parallel operations:")
  t = time{
    val (a, b) = fork(ns1.count(isPrime), ns2.count(isPrime))
    (a + b) / elems.toDouble
  }
  println(s"\t- total time: ${t._2}, probability of prime: ${t._1}")


  println("Running 4 parallel operations:")
  t = time{
    val ((a, b), (c, d)) = fork(
      fork(ns1a.count(isPrime), ns1b.count(isPrime)),
      fork(ns2a.count(isPrime), ns2b.count(isPrime))
    )
    (a + b + c + d) / elems.toDouble
  }
  println(s"\t- total time: ${t._2}, probability of prime: ${t._1}")
}
