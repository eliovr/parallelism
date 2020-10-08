/**
* Author: Elio Ventocilla
* Inspired from: https://github.com/axel22/parprog-snippets
*/
package parallelism

import java.util.concurrent._
import Utils._

object Task extends App {
  val pool = new ForkJoinPool

  /**
  * This function creates a task which runs the given expression
  * on a different thread.
  */
  def task[A](expr: => A): ForkJoinTask[A] = {
    val t = new RecursiveTask[A] {
      def compute = expr
    }
    pool.execute(t)
    t
  }

  val elems = 150000
  val rand = new scala.util.Random()
  val ns = (1 to elems).map(_ => rand.nextInt(elems)).toVector
  val (ns1, ns2) = ns.splitAt(ns.size / 2)
  val (ns1a, ns1b) = ns1.splitAt(ns1.size / 2)
  val (ns2a, ns2b) = ns2.splitAt(ns2.size / 2)

  var t = time(ns.count(isPrime) / elems.toDouble)
  println(s"Sequential time: ${t._2}, probability of primes: ${t._1}")

  t = time {
    val t = task(ns1.count(isPrime))
    val a = ns2.count(isPrime)
    val b = t.join
    (a + b) / elems.toDouble
  }
  println(s"2 Parallel time: ${t._2}, probability of primes: ${t._1}")

  t = time {
    val t1 = task(ns1a.count(isPrime))
    val t2 = task(ns1b.count(isPrime))
    val t3 = task(ns2a.count(isPrime))
    val a = ns2b.count(isPrime)
    val (b, c, d) = (t1.join, t2.join, t3.join)
    (a + b + c + d) / elems.toDouble
  }

  println(s"4 Parallel time: ${t._2}, result: ${t._1}")

}
