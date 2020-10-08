/**
* Author: Elio Ventocilla
* More: https://doc.akka.io/docs/akka/2.5.5/scala/actors.html
*/
package parallelism

import akka.actor.{Actor, Props}
import akka.event.Logging
import Utils._

class PrimeChecker extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case PrimeChecker.CountPrimes(ns) =>
      sender() ! ns.count(isPrime)
    case _ => log.info(s"received unknown message")
  }
}

object PrimeChecker {
  case class CountPrimes(ns: List[Int])
}
