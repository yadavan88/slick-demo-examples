package com.yadavan88

import scala.concurrent.Future

object QueryOperations {
  val db = Connection.db
  import SlickTables.profile.api._

  def getActors: Future[Seq[Actor]] = {
    db.run(SlickTables.actorTable.result)
  }
  def findMovieByName(name: String): Future[Option[Movie]] = {
    db.run(SlickTables.movieTable.filter(_.name === name).result.headOption)
  }

  // todo: check the return id
  def insertMovie(movie: Movie): Future[Movie] = {
    //val insertQuery = SlickTables.movieTable += movie
    val insertQueryWithReturn = SlickTables.movieTable.returning(SlickTables.movieTable) += movie
    db.run(insertQueryWithReturn)
  }

  def forceInsertActors(actors: Seq[Actor]): Future[Option[Int]] = {
      db.run(SlickTables.actorTable.forceInsertAll(actors))
  }

  def forceInsertMovies(movies: Seq[Movie]): Future[Option[Int]] = {
      db.run(SlickTables.movieTable.forceInsertAll(movies))
  }

  def insertMappings(mappings: Seq[MovieActorMapping]): Future[Option[Int]] = {
      db.run(SlickTables.movieActorMappingTable ++= mappings)
  }

  def updateMovie(movieId: Long, movie: Movie): Future[Int] = {
    val updateQuery = SlickTables.movieTable.filter(_.id === movieId).update(movie)
    db.run(updateQuery)
  }

  def deleteMovie(movieId: Long): Future[Int] = {
    db.run(SlickTables.movieTable.filter(_.id === movieId).delete)
  }

  import SlickTables._
  def getActorsByMovie(movieId: Long): Future[Seq[Actor]] = {
    val joinQuery = for {
      res <- movieActorMappingTable
        .filter(_.movieId === movieId)
        .join(actorTable)
        .on(_.actorId === _.id)
    } yield res._2
    db.run(joinQuery.result)
  }

}
