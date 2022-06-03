package com.yadavan88

import java.time.LocalDate
import slick.jdbc.SQLActionBuilder
import slick.jdbc.SetParameter
import scala.concurrent.Future
import com.yadavan88.FutureLogger.FutureLoggerXtension
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors

object MyExecContext {
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(8))
}

object DataSetup {
  import MyExecContext._
  import SlickTables.profile.api._
  def deleteData: Future[Seq[Int]] = {
    // val mappingDelQuery = SlickTables.movieActorMappingTable.delete
    // val actorDelQuery = SlickTables.actorTable.delete
    // val movieDelQuery = SlickTables.movieTable.delete
    // val streamingProviderQuery = SlickTables.streamingProviderMappingTable.delete
    val combinedDelQueries = DBIO.sequence(SlickTables.tables.map(_.delete))
    Connection.db.run(combinedDelQueries.transactionally).logError
  }

  def advancedDataDelete = {
    Connection.db.run(DBIO.sequence(SpecialTables.specialtables.map(_.delete)))
  }

  def initSetup = {

    val shawshank = Movie(1L, "Shawshank Redemptions", LocalDate.of(1994, 4, 2), 162)
    val starTrekWrath = Movie(2L, "Star Trek Wrath Of Khan", LocalDate.of(1986, 6, 1), 131)
    val movies = Seq(shawshank, starTrekWrath)

    val shatner = Actor(1L, "Willian Shatner")
    val nemoy = Actor(2L, "Leonard Nemoy")
    val nichols = Actor(3L, "Nichelle Nichols")
    val freeman = Actor(4L, "Morgan Freeman")
    val actors = Seq(shatner, nemoy, nichols, freeman)

    val mappings = Seq(
      MovieActorMapping(0L, shawshank.id, freeman.id),
      MovieActorMapping(0L, starTrekWrath.id, shatner.id),
      MovieActorMapping(0L, starTrekWrath.id, nemoy.id),
      MovieActorMapping(0L, starTrekWrath.id, nichols.id)
    )

    val streamingProviders = Seq(
      StreamingProviderMapping(0L, 1L, StreamingProvider.Disney),
      StreamingProviderMapping(0L, 1L, StreamingProvider.Prime),
      StreamingProviderMapping(0L, 2L, StreamingProvider.Hulu),
      StreamingProviderMapping(0L, 2L, StreamingProvider.Netflix)
    )

    // an empty database "movies" should exist before these queries are run

    println("Make sure that the database and tables are already created1")
    for {
      _ <- deleteData
      _ <- QueryOperations.forceInsertMovies(movies)
      _ <- QueryOperations.forceInsertActors(actors)
      _ <- QueryOperations.insertMappings(mappings)
      _ <- QueryOperations.insertStreamingProviders(streamingProviders)
    } yield ()

  }

  def advancedSetUp: Future[Unit] = {
    println(SpecialTables.ddl.createIfNotExistsStatements.mkString(";\n"))
    Future.unit
  }

  val shawshankLocation = MovieLocations(0L, 1L, List("Ashland", "Virginia Island"))
  val starTrekLocation = MovieLocations(0L, 2L, List("Golden Gate Park", "Paramount Studios"))

}
