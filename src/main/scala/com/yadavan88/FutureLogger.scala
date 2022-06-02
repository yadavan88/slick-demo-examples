package com.yadavan88
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object FutureLogger {

  implicit class FutureLoggerXtension[A](future: Future[A]) {
    def debug: Future[A] = {
      future.failed.foreach { ex =>
        println("Future failed with exception: " + ex.getMessage())
      }
      future
    }

  }

}
