/**
* Author: Elio Ventocilla
* More: https://doc.akka.io/docs/akka/2.5.5/scala/actors.html
*/
package parallelism

import akka.actor.{ActorSystem, Props}
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import akka.pattern.ask

object Akka extends App {
  val system = ActorSystem("PrimeChecker")

  // Instantiating an actor
  val pc1 = system.actorOf(Props[PrimeChecker](), "PrimeChecker1")
  val pc2 = system.actorOf(Props[PrimeChecker](), "PrimeChecker2")

  // Some random numbers
  val rand = new scala.util.Random
  val trials = 200000
  val ns1 = (1 to trials).map(_ => rand.nextInt(trials)).toList
  val ns2 = (1 to trials).map(_ => rand.nextInt(trials)).toList

  // Sending a message to the actor. Returns a Future.
  println("Asking PrimeChecker 1...")
  val f1 = pc1.ask(PrimeChecker.CountPrimes(ns1))(5.seconds)
  println(s"\t..is completed? ${f1.isCompleted}")

  println("Asking PrimeChecker 2...")
  val f2 = pc2.ask(PrimeChecker.CountPrimes(ns2))(5.seconds)
  println(s"\t..is completed? ${f2.isCompleted}")

  println("Waiting for the completion of both...")
  f1.zip(f2).onComplete{
    case Success((r1, r2)) =>
      println(s"\t..PrimeChecker 1: $r1")
      println(s"\t..PrimeChecker 2: $r2")
		case _ => println("\t..Something went wrong")
  }

  system.terminate()
}
