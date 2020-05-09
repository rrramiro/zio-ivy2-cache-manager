package fr.ramiro.ivy.repository

import fr.ramiro.ivy.model.Item
import zio._

object Repository {

  trait Service[R] {
    def create(item: Item): URIO[R, Item]
    def getAll: URIO[R, List[Item]]
    def searchInPath(pathElement: String): URIO[R, List[Item]]
    def deleteAll: URIO[R, Unit]
  }

}
