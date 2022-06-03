package com.yadavan88

import com.github.tminglei.slickpg.ExPostgresProfile
import com.github.tminglei.slickpg._
import com.github.tminglei.slickpg.geom.PgPostGISExtensions

trait CustomPostgresProfile
    extends ExPostgresProfile
    with PgJsonSupport
    with PgArraySupport
    with PgHStoreSupport
    with PgDate2Support {
  def pgjson = "jsonb"
  override protected def computeCapabilities: Set[slick.basic.Capability] =
    super.computeCapabilities + slick.jdbc.JdbcCapabilities.insertOrUpdate

  override val api = CustomPGAPI
  object CustomPGAPI
      extends API
      with JsonImplicits
      with HStoreImplicits
      with ArrayImplicits
      with DateTimeImplicits {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)

  }

}

object CustomPostgresProfile extends CustomPostgresProfile
