package fr.ramiro.ivy

import java.nio.file.Path

import zio._

package object filesystem extends FileSystem.Service[Has[FileSystem.Service[Any]]] {
  type FileSystem = Has[FileSystem.Service[Any]]

  override def readFile(path: Path): RIO[FileSystem, List[Byte]] =
    ZIO.accessM(_.get.readFile(path))

  override def listDir(start: Path, maxDepth: Int): RIO[FileSystem, List[Path]] =
    ZIO.accessM(_.get.listDir(start, maxDepth))
}
