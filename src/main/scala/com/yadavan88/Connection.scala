package com.yadavan88


import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future
import slick.jdbc.PostgresProfile

object Connection {
  import SlickTables.profile.api._
  val db: SlickTables.profile.backend.DatabaseDef = Database.forConfig("postgres")
  // val db = Database.forConfig("databaseUrl")
}