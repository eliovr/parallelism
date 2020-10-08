/**
* Author: Elio Ventocilla
*/

package parallelism

object Utils {
  /**
  * A function for meassuring the performance of an expression.
  * @param expr the expresion whose performance will be meassured.
  * @return a tuple where the first element contains the result of the expresion,
  *   and the second the performance time in seconds.
  */
  def time[A](expr: => A): (A, Double) = {
      val t = System.nanoTime()
      (expr, (System.nanoTime - t) / 1e9)
  }

  def isPrime(x: Int): Boolean= {
    def check(div: Int): Boolean = {
      if (div > x/2) true
      else(x % div != 0) && check(div+ 1)
    }
    check(2)
  }
}
