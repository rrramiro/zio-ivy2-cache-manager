package fr.ramiro.ivy

import java.nio.file.Paths
import java.nio.file.Files

import pureconfig._
import pureconfig.error.ConfigReaderException
import zio._
import fr.ramiro.ivy.filesystem.FileSystem
import fr.ramiro.ivy.Config.ApplicationConf
import fr.ramiro.ivy.model.Item
import fr.ramiro.ivy.repository.DoobieTodoRepository

object IvyCacheManagerApp extends App {
  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    {
      for {
        cfg <- ZIO
                .fromEither(ConfigSource.default.load[ApplicationConf])
                .mapError(ConfigReaderException(_))
        _ <- runApp(cfg)
      } yield ()
    }.foldM(
        err => console.putStr(s"error: ${err.getMessage}").as(1),
        _   => ZIO.succeed(0)
      )
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def runApp(cfg: ApplicationConf): ZIO[ZEnv, Throwable, Unit] = {
    filesystem.listDir(Paths.get(cfg.basePath)) >>= { paths =>
      ZIO
        .collectAllSuccesses(paths.map { p =>
          if (!Files.isDirectory(p)) {
            val filename = p.getFileName.toString
            if (filename.startsWith("ivy-") && filename.endsWith(".xml")) {
              val group    = p.getParent.getParent.getFileName.toString
              val artifact = p.getParent.getFileName.toString
              val version = filename.stripPrefix("ivy-").stripSuffix(".xml")
              val item = Item(0, p.toString, group, artifact, version)
              repository.create(item) *>
                console.putStrLn("=> " + group + " - " + artifact + " - " + filename)
            } else ZIO.unit
          } else ZIO.unit
        })
        .as(())
    }
  }.provideLayer(ZEnv.live ++ FileSystem.live ++ DoobieTodoRepository.withDoobieTodoRepository(cfg.db) )
}
