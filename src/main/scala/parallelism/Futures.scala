/**
* Author: Elio Ventocilla
* More: https://docs.scala-lang.org/overviews/core/futures.html
*/
package parallelism

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.util.{Success, Failure}

object Features extends App {
	def checkValue[A](f: Future[A]): String =	f.value match {
		case None => "Not yet completed"
		case Some(Success(v)) => s"Result is $v"
		case Some(Failure(e)) => s"Something went wrong: $e"
	}

	val sampleSize = 10000

	println(s"1. Generating $sampleSize random numbers and getting the max...")
	/**
	* Here is a simple instantiation of a future.
	* The code inside Future will be extecuted in a separate thread.
	* The call to Future takes an implicit ExecutionContext parameter,
	* which we have imported in the first line.
	*/
	val f1 = Future[Double] {
		(1 to sampleSize).map(x => math.random()).max
	}

	// To check if the operation is complete we can call the isCompleted function.
	println("\t..is operation complete? " + f1.isCompleted)
	// Or we can check its current value.
	println("\t..current value: " + checkValue(f1))


	// ----------------------------------


	println(s"2. Checking when an operations fails in a Future...")
	/**
	* If the operation inside a Future fails, it will do so in another thread.
	* The value in this case will be of type Some[Failure[_]]
	*/
	val f2 = Future[Double] { 5 / 0 }
	println("\t..current value: " + checkValue(f2))


	// ----------------------------------


	println(s"3. Waiting for a Future to complete...")
	/**
	* Here's an example of a Future operation that waits two seconds to compute.
	*/
	val f3 = Future[Double] {
		Thread.sleep(2000)
		(1 to sampleSize).map(x => math.random()).sum
	}

	// checkValue should return a "Not yet completed"
	println("\t..current value: " + checkValue(f3))
	// We can wait for the Future to be completed. This blocks the main thread.
	val res1: Double = Await.result(f3, 5.seconds)
	println("\t..result (after waiting): " + res1)


	// ----------------------------------


	println(s"4. Chaining transformations...")
	/**
	* We can chain of Futures in order to calculate the variance of a random set of
	* numbers, using the map function. A Future map, as it does in Lists, will
	* return a new Future with its value transformed.
	*/
	val f4 = Future {
		(1 to sampleSize).map(_ => math.random())
	}.map { ns =>
		(ns, ns.sum / ns.size)
	}.map { case (ns, avg) =>
		ns.map(x => math.pow(x - avg, 2)).reduce(_ + _) / ns.size
	}

	println("\t..current value: " + checkValue(f4))
	val res2: Double = Await.result(f3, 5.seconds)
	println("\t..result (after waiting): " + res2)


	// ----------------------------------


	println(s"5. Combining Futures...")
	/**
	* Results from two different Futures can be brought together into a new Future
	* using for loops. In this example we check how fair is math.random().
	*/

	val fa = Future[Double] {
		Thread.sleep(2000)
		(1 to sampleSize)
			.map(_ => math.random())
			.filter(_ < .5)
			.size / sampleSize.toDouble
	}

	val fb = Future[Double] {
		Thread.sleep(2000)
		(1 to sampleSize)
			.map(_ => math.random())
			.filter(_ >= .5)
			.size / sampleSize.toDouble
	}

	val fc = for {
		a <- fa
		b <- fb
	} yield a + b

	// Since they run in parallel, they shouldn't take more than 4 seconds.
	// If they were sequential, then they would take more than 4 seconds.
	val res3 = Await.result(fc, 3.seconds)
	println("\t..result (after waiting): " + res3)


	// ----------------------------------


	println(s"6. Example of functions with side effects...")
	/**
	* There are also methods with which to perform side effects:
	* foreach, onComplete and andThen. There's an example of onComplete.
	*/
	Future[Double] {
		(1 to sampleSize).map(_ => math.random()).sum / sampleSize.toDouble
	}.onComplete {
		case Success(res) => println(s"\t..The average of random numbers is: $res")
		case Failure(e) => println(s"\t..Something went wrong: $e")
	}
}
