package com.yadavan88


import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.jdbc.PostgresProfile

object Connection {
  import SlickTables.profile.api._
  val db: SlickTables.profile.backend.DatabaseDef = Database.forConfig("postgres")

  // val actorsF: Future[Seq[Actor]] = db.run(SlickTables.actorTable.result)

  // actorsF.map(s => println(">>>>>>>>>" + s)).recover { case ex => println("!!!!!!!!!!!!!!") }
  // Thread.sleep(10000)

}
