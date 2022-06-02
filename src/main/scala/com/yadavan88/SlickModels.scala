package com.yadavan88

import java.time.LocalDate
import slick.lifted.Tag
import slick.jdbc.PostgresProfile
import com.vividsolutions.jts.geom.Geometry
//import slick.jdbc.PostgresProfile.api._

final case class Movie(
    id: Long,
    name: String,
    releaseDate: LocalDate,
    lengthInMin: Int
)
final case class Actor(id: Long, name: String)
final case class MovieActorMapping(id: Long, movieId: Long, actorId: Long)
final case class GeoLocation(id: Long, geoLocation: Geometry)

final case class GeoFencedMovie(
    id: Long,
    name: String,
    releaseDate: LocalDate,
    lengthInMin: Int,
    allowedGeo: Option[Geometry]
)

class SlickTablesGeneric(val profile: PostgresProfile) {
  import profile.api._

  class MovieTable(tag: Tag) extends Table[Movie](tag, "Movie") {
    def id = column[Long]("movie_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def releaseDate = column[LocalDate]("release_date")
    def lengthInMin = column[Int]("length_in_min")
    override def * = (id, name, releaseDate, lengthInMin) <> (Movie.tupled, Movie.unapply)
  }
  lazy val movieTable = TableQuery[MovieTable]

  class ActorTable(tag: Tag) extends Table[Actor](tag, "Actor") {
    def id = column[Long]("actor_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    override def * = (id, name) <> (Actor.tupled, Actor.unapply)
  }

  lazy val actorTable = TableQuery[ActorTable]

  class MovieActorMappingTable(tag: Tag)
      extends Table[MovieActorMapping](tag, "MovieActorMapping") {
    def id = column[Long]("movie_actor_id", O.PrimaryKey, O.AutoInc)
    def movieId = column[Long]("movie_id")
    def actorId = column[Long]("actor_id")
    override def * = (id, movieId, actorId) <> (MovieActorMapping.tupled, MovieActorMapping.unapply)
  }

  lazy val movieActorMappingTable = TableQuery[MovieActorMappingTable]

  val ddl: profile.DDL =
    Seq(movieTable, actorTable, movieActorMappingTable).map(_.schema).reduce(_ ++ _)

}

object SlickTables extends SlickTablesGeneric(PostgresProfile)

object SpecialTables {
  val api = CustomPostgresProfile.api
  import api._
  class GeoFencedMovieTable(tag: Tag) extends Table[GeoFencedMovie](tag, "GeoFencedMovie") {
    def id = column[Long]("geo_movie_id", O.PrimaryKey, O.SqlType("BIGSERIAL"))
    def name = column[String]("name")
    def releaseDate = column[LocalDate]("release_date")
    def lengthInMin = column[Int]("length_in_min")
    def geoLocation = column[Option[Geometry]]("geo_location")
    // format: off
    override def * = (id, name, releaseDate, lengthInMin, geoLocation) <> (GeoFencedMovie.tupled, GeoFencedMovie.unapply)
    // format: on
  }
  lazy val geoFencedMovieTable = TableQuery[GeoFencedMovieTable]

  val ddl = Seq(geoFencedMovieTable).map(_.schema).reduce(_ ++ _)

  // class GeoLocationTable(tag: Tag) extends Table[GeoLocation](tag, "GeoLocation") {
  //   def id = column[Long]("movie_actor_id", O.PrimaryKey, O.AutoInc)
  //   def geoLocation = column[Geometry]("geo_location")
  //   override def * = (id, geoLocation) <> (GeoLocation.tupled, GeoLocation.unapply)
  // }
}

//docker run --name postgresql -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=admin -p 5432:5432 -d postgres
//docker run --name postgresql -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=admin -p 5432:5432 -d postgis/postgis

object Main extends App {
  val ddlQueries = SlickTables.ddl.createIfNotExistsStatements.mkString(";\n")
  println(ddlQueries)
}
