package fr.ramiro.ivy.repository

import cats.effect.Blocker
import cats.implicits._
import io.getquill.{idiom => _, _}
import doobie._
import doobie.implicits._
import doobie.hikari._
import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import fr.ramiro.ivy.Config._
import fr.ramiro.ivy.model.Item
import org.flywaydb.core.Flyway
import zio._
import zio.blocking.Blocking
import zio.interop.catz._

object DoobieTodoRepository {

  def withDoobieTodoRepository(
      cfg: DbConfig
  ): ZLayer[Blocking, Throwable, Has[Repository.Service[Any]]] = {
    Task(
      Flyway
        .configure()
        .dataSource(cfg.url, cfg.user, cfg.password)
        .load()
        .migrate()
    ).unit.toManaged_ *> (ZIO.runtime[Blocking].toManaged_ >>= { implicit rt =>
      HikariTransactor
        .newHikariTransactor[Task](
          cfg.driver,
          cfg.url,
          cfg.user,
          cfg.password,
          rt.platform.executor.asEC,
          Blocker.liftExecutionContext(rt.environment.get.blockingExecutor.asEC)
        )
        .toManaged
    }).map(transactor => new DoobieTodoRepository.Service[Any](transactor))
  }.toLayer

  object SqlContext extends DoobieContext.SQLite(LowerCase) {
    implicit val itemInsertMeta = insertMeta[Item](_.id)

    implicit class StringQuotes(underlying: String) {
      def like(s: String) = quote(infix"$underlying like $s".as[Boolean])
    }

    val itemTable = quote {
      querySchema[Item]("item")
    }

    @SuppressWarnings(Array("org.wartremover.warts.StringPlusAny"))
    def create(item: Item): ConnectionIO[Long] = run(quote {
      itemTable.insert(lift(item)).returningGenerated(_.id)
    })

    def searchInPath(pathElement: String): ConnectionIO[List[Item]] =
      run(quote {
        itemTable.filter(_.path like lift(pathElement))
      })

    val getAll: ConnectionIO[List[Item]] = run(itemTable)

    val deleteAll: ConnectionIO[Long] = run(quote {
      itemTable.delete
    })

  }

  class Service[R](xa: Transactor[Task]) extends Repository.Service[R] {

    override def getAll: URIO[R, List[Item]] =
      SqlContext.getAll
        .transact(xa)
        .orDie

    def searchInPath(pathElement: String): URIO[R, List[Item]] =
      SqlContext.searchInPath(pathElement).transact(xa).orDie

    override def deleteAll: URIO[R, Unit] =
      SqlContext.deleteAll
        .transact(xa)
        .unit
        .orDie

    override def create(
        item: Item
    ): URIO[R, Item] =
      SqlContext
        .create(item)
        .map(id => item.copy(id = id))
        .transact(xa)
        .orDie
  }
}
