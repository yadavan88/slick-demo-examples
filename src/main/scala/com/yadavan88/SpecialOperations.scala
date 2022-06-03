package com.yadavan88

import scala.concurrent.Future
import com.yadavan88.FutureLogger.FutureLoggerXtension

object SpecialOperations {

    import SpecialTables.api._
    def saveMovieLocations(movieLocation: MovieLocations): Future[Int] = {
        val insertLocation = SpecialTables.movieLocationsTable += movieLocation
        Connection.db.run(insertLocation)
    }

    def getMoviesByLocation(location: String): Future[Seq[MovieLocations]] = {
        val q = SpecialTables.movieLocationsTable.filter(_.locations.@>(List(location)))
        Connection.db.run(q.result)
    }
}
