package fr.ramiro.ivy
import pureconfig.ConfigConvert
import pureconfig.generic.semiauto._

object Config {
  final case class DbConfig(
      url: String,
      driver: String,
      user: String,
      password: String
  )

  object DbConfig {
    implicit val convert: ConfigConvert[DbConfig] = deriveConvert
  }

  final case class ApplicationConf(
      basePath: String,
      db: DbConfig
  )

  object ApplicationConf {
    implicit val convert: ConfigConvert[ApplicationConf] = deriveConvert
  }
}
