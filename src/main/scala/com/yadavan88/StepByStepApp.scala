package com.yadavan88

import java.time.LocalDate
import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure
import slick.jdbc.SQLActionBuilder
import slick.jdbc.SetParameter
import FutureLogger._
import MyExecContext._
import scala.concurrent.Await
import scala.concurrent.duration._
object StepByStepApp extends App {

  import SlickTables.profile.api._

  val res = for {
    _ <- DataSetup.initSetup
    _ <- QueryOperations.getAllMoviesByPlainQuery.debug
    _ <- DataSetup.advancedSetUp.logError
    _ <- DataSetup.advancedDataDelete.logError
    _ <- QueryOperations.getStreamingProviders(2).debug
    _ <- SpecialOperations.saveMovieLocations(DataSetup.shawshankLocation).debug
    _ <- SpecialOperations.saveMovieLocations(DataSetup.starTrekLocation).debug
    _ <- SpecialOperations.getMoviesByLocation("Ashland").debug
    _ <- SpecialOperations.getMoviesFilmedInLocations(List("Ashland", "Paramount Studios")).debug
    _ <- SpecialOperations.saveMovieProperties(DataSetup.starTrekProperties)
    _ <- SpecialOperations.saveMovieProperties(DataSetup.shawshankProperties)
    _ <- SpecialOperations.getMoviesByDistributor("Columbia Pictures").debug
    _ <- SpecialOperations.saveActorDetails(DataSetup.actorDetailsShatner)
    _ <- SpecialOperations.saveActorDetails(DataSetup.actorDetailsNemoy)
    _ <- SpecialOperations.getActorsBornOn("1931").debug
    _ = println("all done.... ")
  } yield ()

  Await.result(res, 5.seconds)
}
