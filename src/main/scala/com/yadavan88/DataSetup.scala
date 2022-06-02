package com.yadavan88

import java.time.LocalDate
import slick.jdbc.SQLActionBuilder
import slick.jdbc.SetParameter
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.yadavan88.FutureLogger.FutureLoggerXtension
import scala.util.Success
import scala.util.Failure
import com.vividsolutions.jts.io.WKTReader

object DataSetup {
  import SlickTables.profile.api._
  def deleteData: Future[Int] = {
    val mappingDelQuery = SlickTables.movieActorMappingTable.delete
    val actorDelQuery = SlickTables.actorTable.delete
    val movieDelQuery = SlickTables.movieTable.delete
    Connection.db.run((mappingDelQuery >> actorDelQuery >> movieDelQuery).transactionally).debug
  }

  def advancedDataDelete = {
    Connection.db.run(SpecialTables.geoFencedMovieTable.delete).debug
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

    val ddlScripts = SlickTables.ddl.createIfNotExistsStatements
    println(ddlScripts.toList)
    val ddlAction = SQLActionBuilder(ddlScripts.toList.mkString(";"), SetParameter.SetUnit)

    // an empty database "movies" should exist before these queries are run
    for {
      _ <- Connection.db.run(ddlAction.asUpdate)
      _ <- deleteData
      _ <- QueryOperations.forceInsertMovies(movies)
      _ <- QueryOperations.forceInsertActors(actors)
      _ <- QueryOperations.insertMappings(mappings)
    } yield ()

  }

  def advancedSetUp: Future[Int] = {
    val plain = "create extension if not exists postgis ;"
    val qurey = SpecialTables.ddl.createIfNotExistsStatements.mkString(";")
    println(qurey)
    val action = SQLActionBuilder(plain + qurey, SetParameter.SetUnit)
    val rs = Connection.db.run(action.asUpdate).debug
    rs.onComplete {
      case Success(value)     => println("special tables created.")
      case Failure(exception) => println("exception... " + exception)
    }
    rs
  }

  val reader = new WKTReader()
  val USA_Canada = reader.read(
    "POLYGON((-156.064213217353 70.904147237984,-165.556400717353 68.72252478395494,-164.853275717353 61.692595752289385,-158.173588217353 56.65938316955106,-149.384525717353 60.15530044125013,-125.126713217353 49.04163280830964,-121.962650717353 36.74348640510432,-77.665775717353 34.1665702577458,-57.97827571735299 52.379105154021765,-69.228275717353 59.97987895789853,-91.728275717353 70.32067262057865,-156.064213217353 70.904147237984))"
  )
  val GERMANY = reader.read(
    "POLYGON((13.058138443232611 47.75768357416473,9.674349380732611 47.668979059342334,7.477083755732612 47.491116934282246,8.312044693232611 48.92600360672974,6.158724380732612 49.642630977992255,6.378450943232612 51.42993983832047,7.389193130732612 53.149969009698864,8.663607193232611 53.33405793280008,8.883333755732611 54.72770015242716,10.992708755732611 54.24266723973982,13.277865005732611 54.21697964895226,14.684115005732611 53.67383971660745,15.035677505732611 51.59403602791627,14.728060318232611 50.961786025176025,12.311068130732611 50.348863833289684,12.574740005732611 49.414443139936225,14.024935318232611 48.57831858770065,12.926302505732611 48.19894248861258,13.058138443232611 47.75768357416473))"
  )
  val HAMBURG = reader.read("POINT(10.012433928806729 53.54306716892628)")
  val geoFencedMovies = Seq(
    GeoFencedMovie(1L, "Star Trek Generations", LocalDate.of(1994, 5, 24), 118, Some(USA_Canada)),
    GeoFencedMovie(2L, "The Prestige", LocalDate.of(2006, 11, 9), 130, Some(GERMANY)),
    GeoFencedMovie(3L, "Guardians of the Galaxy", LocalDate.of(2014, 11, 9), 122, None)
  )

}

/*

CREATE TABLE IF NOT EXISTS public."GeoTest"
(
    id bigserial NOT NULL,
    geo geometry,
    CONSTRAINT "GeoTest_pkey" PRIMARY KEY (id)
)
insert into public."GeoTest"("geo") values( ST_MakePolygon( ST_GeomFromText('LINESTRING(75 29,77 29,77 29, 75 29)')));
insert into public."GeoTest"("geo") values (ST_GeomFromText('POLYGON((13.058138443232611 47.75768357416473,9.674349380732611 47.668979059342334,7.477083755732612 47.491116934282246,8.312044693232611 48.92600360672974,6.158724380732612 49.642630977992255,6.378450943232612 51.42993983832047,7.389193130732612 53.149969009698864,8.663607193232611 53.33405793280008,8.883333755732611 54.72770015242716,10.992708755732611 54.24266723973982,13.277865005732611 54.21697964895226,14.684115005732611 53.67383971660745,15.035677505732611 51.59403602791627,14.728060318232611 50.961786025176025,12.311068130732611 50.348863833289684,12.574740005732611 49.414443139936225,14.024935318232611 48.57831858770065,12.926302505732611 48.19894248861258,13.058138443232611 47.75768357416473))'))
POLYGON((13.058138443232611 47.75768357416473,9.674349380732611 47.668979059342334,7.477083755732612 47.491116934282246,8.312044693232611 48.92600360672974,6.158724380732612 49.642630977992255,6.378450943232612 51.42993983832047,7.389193130732612 53.149969009698864,8.663607193232611 53.33405793280008,8.883333755732611 54.72770015242716,10.992708755732611 54.24266723973982,13.277865005732611 54.21697964895226,14.684115005732611 53.67383971660745,15.035677505732611 51.59403602791627,14.728060318232611 50.961786025176025,12.311068130732611 50.348863833289684,12.574740005732611 49.414443139936225,14.024935318232611 48.57831858770065,12.926302505732611 48.19894248861258,13.058138443232611 47.75768357416473))

select * from public."GeoTest" where ST_contains("geo", ST_GeomFromText('POINT(10.465365005732611 51.347669985966945)'))


 */
