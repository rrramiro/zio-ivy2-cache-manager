package fr.ramiro.ivy

import fr.ramiro.ivy.model.Item
import zio._

package object repository extends Repository.Service[Has[Repository.Service[Any]]] {
  type Repository = Has[Repository.Service[Any]]

  def create(
      item: Item
  ): URIO[Repository, Item] =
    ZIO.accessM(_.get.create(item))

  def getAll: URIO[Repository, List[Item]] =
    ZIO.accessM(_.get.getAll)

  def searchInPath(pathElement: String): URIO[Repository, List[Item]] =
    ZIO.accessM(_.get.searchInPath(pathElement))

  def deleteAll: URIO[Repository, Unit] =
    ZIO.accessM(_.get.deleteAll)

}
