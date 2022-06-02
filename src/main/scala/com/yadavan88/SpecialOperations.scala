package com.yadavan88

import scala.concurrent.Future
import com.vividsolutions.jts.geom.Point
import com.yadavan88.FutureLogger.FutureLoggerXtension

object SpecialOperations {
    import SpecialTables._
    import SpecialTables.api._
    import Connection._

    def saveMovieWithGeoFencing(movies: Seq[GeoFencedMovie]): Future[Option[Int]] = {
        val insertQuery = geoFencedMovieTable ++= movies
        db.run(insertQuery)
    }

    def checkGeoAccess(movieName: String, userLocation: Point) = {
        val query = geoFencedMovieTable.filter(r => r.name === movieName && ( r.geoLocation.isEmpty || r.geoLocation.contains(userLocation.bind))  )
        //filter(m => m.name === movieName && (m.geoLocation.get.contains(userLocation.bind)))
        db.run(query.result).debug
    }

}