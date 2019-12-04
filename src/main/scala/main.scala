import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import org.locationtech.jts.geom.Point

// Our own profile where we extend the H2 profile to add in low-level Point access
trait GeoH2Profile extends slick.jdbc.JdbcProfile with slick.jdbc.H2Profile {
  import java.sql.{PreparedStatement, ResultSet}

  override val columnTypes = new JdbcTypes

  class JdbcTypes extends super.JdbcTypes {

    class PointJdbcType extends DriverJdbcType[Point] {
      // Unsure what to use here, so I'm using O.SqlType to control this in the table
      def sqlType = java.sql.Types.OTHER 

      def setValue(v: Point, p: PreparedStatement, idx: Int) =
        p.setObject(idx, v)

      def getValue(r: ResultSet, idx: Int) =
        r.getObject(idx).asInstanceOf[Point]

      def updateValue(v: Point, r: ResultSet, idx: Int) = r.updateObject(idx, v)
    }

    val pointCol = new PointJdbcType()
  }

  override val api = GeoAPI

  object GeoAPI extends API {
    implicit def pointColumnType: BaseColumnType[Point] = columnTypes.pointCol
  }
}

object GeoH2Profile extends GeoH2Profile

import GeoH2Profile.api._

object Example extends App {

  class Telemetry(tag: Tag) extends Table[(Long, Point, Option[Point])](tag, "TELEMETRY") {
    def id    = column[Long]("ID", O.PrimaryKey)
    def pos   = column[Point]("POS", O.SqlType("GEOMETRY(POINT, 0)"))
    def other = column[Option[Point]]("OTHER", O.SqlType("GEOMETRY(POINT, 0)"))
    def * = (id, pos, other)
  }

  val telemetry = TableQuery[Telemetry]

  val aPoint: Point = {
    import org.locationtech.jts.geom.{Coordinate, GeometryFactory}
    val gf = new GeometryFactory()
    gf.createPoint(new Coordinate(42, 90))
  }

  val program = for {
    _ <- telemetry.schema.create
    _ <- telemetry += ((1L, aPoint, None))
    results <- telemetry.result
  } yield results

  val db = Database.forConfig("example")
  try println(
    Await.result(db.run(program), 2.seconds)
  )
  finally db.close
}
