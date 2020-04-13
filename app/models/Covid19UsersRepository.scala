package models

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape
import slick.lifted.ProvenShape.proveShapeOf

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class CitizenInfo(id: Long,
                       country: String,
                       name: String,
                       age: Int,
                       isAffected: Boolean)

class Covid19UsersRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends Covid19UsersBaseRepository with
  Covid19UsersBaseRepositoryImpl with Covid19UsersTable with Covid19UsersReadRepositoryImpl

// =====================================================================================================================
// Write Repository
// =====================================================================================================================
trait Covid19UsersBaseRepository {
  def store(userInfo: CitizenInfo): Future[Boolean]

  def flipStatus(userId: Long, isAffected: Boolean): Future[Boolean]
}

trait Covid19UsersBaseRepositoryImpl extends Covid19UsersBaseRepository {
  self: Covid19UsersTable =>

  import profile.api._

  def store(userInfo: CitizenInfo): Future[Boolean] =
    db.run(covid19CitizenQuery returning covid19CitizenQuery.map(_.id) += userInfo) map (_ > 0)

  def flipStatus(userId: Long, isAffected: Boolean): Future[Boolean] = {
    db.run(covid19CitizenQuery.filter(_.id === userId)
      .map(_.isAffected).update(!isAffected)).map(_ > 0)
  }
}

trait Covid19UsersReadRepository {
  def getCitizenById(id: Long): Future[Option[CitizenInfo]]

  def getCountries: Future[List[String]]

  def getByCountry(country: String): Future[List[CitizenInfo]]
}

trait Covid19UsersReadRepositoryImpl extends Covid19UsersReadRepository {
  self: Covid19UsersTable =>

  import profile.api._

  def getCitizenById(id: Long): Future[Option[CitizenInfo]] =
    db.run(covid19CitizenQuery.filter(_.id === id).result.headOption)

  def getCountries: Future[List[String]] =
    db.run(covid19CitizenQuery.map(_.country).result).map(_.toList)

  def getByCountry(country: String): Future[List[CitizenInfo]] = {
    if (country == "all") {
      db.run(covid19CitizenQuery.result).map(_.toList)
    } else {
      db.run(covid19CitizenQuery.filter(_.country.toLowerCase === country.toLowerCase).result).map(_.toList)
    }
  }
}

trait Covid19UsersTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val covid19CitizenQuery: TableQuery[Covid19Users] = TableQuery[Covid19Users]

  private[models] class Covid19Users(tag: Tag) extends Table[CitizenInfo](tag, "covid19_users") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def country: Rep[String] = column[String]("country")

    def name: Rep[String] = column[String]("name")

    def age: Rep[Int] = column[Int]("age")

    def isAffected: Rep[Boolean] = column[Boolean]("isAffected")

    def * : ProvenShape[CitizenInfo] = (id, country, name, age, isAffected) <> (CitizenInfo.tupled, CitizenInfo.unapply) // scalastyle:ignore
  }

}
