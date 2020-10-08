/**
* Author: Elio Ventocilla
* API: https://www.scala-lang.org/api/current/scala/collection/parallel/immutable/ParVector.html
*/
package parallelism

import Utils._
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.immutable.ParVector

object ParallelCollections extends App {
  val sampleSize = 1000

  val ns = (1 to sampleSize).toVector
  val strs = "This is some string".toVector.map(_.toString)

  val nsPar = ns.par
  val strsPar = strs.par

	println("Side effects are non-deterministic")
  println("\tAddition:")
  var sum = 0
  nsPar.foreach(sum += _ ); println("\t- " + sum)
  sum = 0
  nsPar.foreach(sum += _ ); println("\t- " + sum)
  sum = 0
  nsPar.foreach(sum += _ ); println("\t- " + sum)

  println("\tConcatenation:")
  var s = ""
  strsPar.foreach(s += _ ); println("\t- " + s)
  s = ""
  strsPar.foreach(s += _ ); println("\t- " + s)
  s = ""
  strsPar.foreach(s += _ ); println("\t- " + s)


  println("Non-associative operations are non-deterministic")
  println("\tSubtraction:")
  println("\t- " + nsPar.reduce(_ - _))
  println("\t- " + nsPar.reduce(_ - _))
  println("\t- " + nsPar.reduce(_ - _))


  println("Parallel results are brought together in the same order")
  println("\t- " + strsPar.reduce(_ + _))
  println("\t- " + strsPar.reduce(_ + _))
  println("\t- " + strsPar.reduce(_ + _))


  println("Performance:")
  val t1 = time{ ns.foreach(_ => Thread.sleep(10)) }._2
  val t2 = time{ nsPar.foreach(_ => Thread.sleep(10)) }._2

  println(s"\t- Sequential time: $t1 seconds")
  println(s"\t- Parallel time: $t2 seconds")
  println(s"\t- Parallel is ${t1/t2} times faster")
}
