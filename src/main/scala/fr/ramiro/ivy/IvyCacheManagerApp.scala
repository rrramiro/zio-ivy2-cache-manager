package fr.ramiro.ivy

import java.nio.file.Paths
import java.nio.file.Files

import pureconfig._
import pureconfig.error.ConfigReaderException
import zio._
import fr.ramiro.ivy.filesystem.FileSystem
import fr.ramiro.ivy.Config.{ApplicationConf}


object IvyCacheManagerApp extends App {
  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    {
      for {
        cfg <- ZIO
          .fromEither(ConfigSource.default.load[ApplicationConf])
          .mapError(ConfigReaderException(_))
        _ <- runApp(cfg)
      } yield ()
    }
    .foldM(
      err => console.putStr(s"error: ${err.getMessage}").as(1),
      _ => ZIO.succeed(0)
    )
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def runApp(cfg: ApplicationConf): ZIO[ZEnv, Throwable, Unit] = {
    filesystem.listDir(Paths.get(cfg.basePath) , 1) >>= { paths =>
      ZIO.collectAllSuccesses(
      paths.map{ p =>
        if(Files.isDirectory(p))
          console.putStrLn( "D " + Paths.get(cfg.basePath).relativize(p).toString())
        else 
        console.putStrLn( "F " + p.toUri.getRawPath)
      }).as(())
    }
  }.provideLayer(ZEnv.live ++ FileSystem.live)
}
