package com.yadavan88

import java.time.LocalDate
import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure
import slick.jdbc.SQLActionBuilder
import slick.jdbc.SetParameter
import FutureLogger._
import MyExecContext._
object StepByStepApp extends App {

  import SlickTables.profile.api._

  for {
    _ <- DataSetup.initSetup
    _ <- QueryOperations.getAllMoviesByPlainQuery.debug
    _ <- DataSetup.advancedSetUp.logError
    _ <- DataSetup.advancedDataDelete.logError
    _ <- QueryOperations.getStreamingProviders(2).debug
    _ <- SpecialOperations.saveMovieLocations(DataSetup.shawshankLocation).debug
    _ <- SpecialOperations.saveMovieLocations(DataSetup.starTrekLocation).debug
    _ <- SpecialOperations.getMoviesByLocation("Ashland").debug

  } yield ()

  Thread.sleep(10000)
}
