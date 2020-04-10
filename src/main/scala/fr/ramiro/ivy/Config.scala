package fr.ramiro.ivy
import pureconfig.ConfigConvert
import pureconfig.generic.semiauto._

object Config {
  final case class ApplicationConf(basePath: String)

  object ApplicationConf {
    implicit val convert: ConfigConvert[ApplicationConf] = deriveConvert
  }
}
