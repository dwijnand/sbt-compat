package sbt

object compat {
  implicit class ConfigTypeRef(private val CR: ConfigRef.type) extends AnyVal {
    def wrap(cr: ConfigRef): ConfigRef = cr
  }
}
