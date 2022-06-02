package com.yadavan88

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure
import slick.jdbc.SQLActionBuilder
import slick.jdbc.SetParameter
import com.vividsolutions.jts.io.WKTReader
import FutureLogger._
object StepByStepApp extends App {

  import SlickTables.profile.api._
  val wktReader = new WKTReader

  for {
    _ <- DataSetup.initSetup
    _ <- DataSetup.advancedSetUp
    _ <- DataSetup.advancedDataDelete
    _ = println("geo tables created")
    _ <- SpecialOperations.saveMovieWithGeoFencing(DataSetup.geoFencedMovies).debug
    _ = println("data saved to geo tables")
    _ = println("checking if German user has access to `The Prestige`")
    berlin = wktReader.read("POINT(10.465365005732611 51.347669985966945)").getCentroid()
    _ = println("berlin location. " + berlin)
    moviesList <- SpecialOperations.checkGeoAccess("The Prestige", berlin).debug
    _ = println("movies = "+moviesList)
  } yield ()

  Thread.sleep(10000)
}
