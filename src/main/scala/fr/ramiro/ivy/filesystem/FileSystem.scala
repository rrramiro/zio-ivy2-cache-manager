package fr.ramiro.ivy.filesystem

import java.nio.file.Path

import cats.effect.Blocker
import fs2.io
import zio._
import zio.interop.catz._

object FileSystem {
  trait Service[R] {
    def readFile(file: Path): RIO[R, List[Byte]]
    def listDir(start: Path): RIO[R, List[Path]]
  }

  object Service {
    def live[R](blocker: blocking.Blocking): Service[R] = new Service[R] {

      def readFile(path: Path): RIO[R, List[Byte]] =
        io.file
          .readAll[Task](path, Blocker.liftExecutionContext(blocker.get.blockingExecutor.asEC), 4096)
          .compile
          .toList

      def listDir(start: Path): RIO[R, List[Path]] =
        io.file
          .walk[Task](
            blocker = Blocker.liftExecutionContext(blocker.get.blockingExecutor.asEC),
            start: Path,
            Int.MaxValue
          )
          .compile
          .toList
    }
  }

  val live = blocking.Blocking.live >>> ZLayer.fromFunction(Service.live[Any])
}
