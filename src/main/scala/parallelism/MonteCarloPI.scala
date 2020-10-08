/**
* Author: Elio Ventocilla
*/
package parallelism

import Utils._
import java.util.concurrent._

object MonteCarloPI extends App {
  val pool = new ForkJoinPool

  def task[A](expr: => A): ForkJoinTask[A] = {
    val t = new RecursiveTask[A] {
      def compute = expr
    }
    pool.execute(t)
    t
  }

  def fork[A, B](a: => A, b: => B): (A, B) = {
      val f = task(b)
      val r1 = a
      val r2 = f.join
      (r1, r2)
  }

  def mcCount(iterations: Int, hits: Int = 0): Int = {
      if (iterations <= 0) hits
      else {
          val x = math.random()
          val y = math.random()
          if (x*x + y*y < 1) mcCount(iterations - 1, hits + 1)
          else mcCount(iterations - 1, hits)
      }
  }

  def monteCarloPI(hits: Int, attempts: Int): Double = {
      4.0 * hits / attempts
  }

  val trials = 50
  val iterations = 100000

  print(s"Running Monte Carlo sequentially a $trials times...")

  var sumTime = .0
  var sumPi = .0

  for( i <- 0 to trials) {
      val (pi, t) = time(monteCarloPI(mcCount(iterations), iterations))
      sumPi += pi
      sumTime += t
  }

  var avgTime = sumTime / trials
  var avgPi = sumPi / trials

  println(" Done.")
  println(s"  average PI: $avgPi\n  average time: $avgTime")

  print(s"Running Monte Carlo in parallel a $trials times...")

  sumTime = .0
  sumPi = .0

  val split = iterations / 4
  for( i <- 0 to trials) {
      val (pi, t) = time {
          val ((a, b), (c, d)) = fork(
              fork(mcCount(split), mcCount(split)),
              fork(mcCount(split), mcCount(split)))
          monteCarloPI(a + b + c + d, iterations)
      }
      sumPi += pi
      sumTime += t
  }

  avgTime = sumTime / trials
  avgPi = sumPi / trials

  println(" Done.")
  println(s"  average PI: $avgPi\n  average time: $avgTime")
}
