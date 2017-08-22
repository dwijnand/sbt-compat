val sbtcompat = project in file(".")

organization in ThisBuild := "com.dwijnand"
        name              := "sbt-compat"
    licenses in ThisBuild := Seq(("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0")))
 description in ThisBuild := "A compatibility library; backports parts of sbt 1's public API in sbt 0.13"
  developers in ThisBuild := List(Developer("dwijnand", "Dale Wijnand", "dale wijnand gmail com", url("https://dwijnand.com")))
   startYear in ThisBuild := Some(2017)
    homepage in ThisBuild := scmInfo.value map (_.browseUrl)
     scmInfo in ThisBuild := Some(ScmInfo(url("https://github.com/dwijnand/sbt-compat"), "scm:git:git@github.com:dwijnand/sbt-compat.git"))

       sbtPlugin           := true
      sbtVersion in Global := "0.13.8" // must be Global, otherwise ^^ won't change anything
crossSbtVersions           := List("0.13.8", "1.0.0") // 0.13.8 has UpdateLogging.Default

scalaVersion in ThisBuild := (CrossVersion partialVersion (sbtVersion in pluginCrossBuild).value match {
  case Some((0, 13)) => "2.10.6"
  case Some((1, _))  => "2.12.3"
  case _             => sys error s"Unhandled sbt version ${(sbtVersion in pluginCrossBuild).value}"
})

scalacOptions in ThisBuild ++= Seq("-encoding", "utf8")
scalacOptions in ThisBuild ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")
scalacOptions in ThisBuild  += "-Xfuture"
scalacOptions in ThisBuild  += "-Yno-adapted-args"
scalacOptions in ThisBuild  += "-Ywarn-dead-code"
scalacOptions in ThisBuild  += "-Ywarn-numeric-widen"
scalacOptions in ThisBuild  += "-Ywarn-value-discard"


import com.typesafe.tools.mima.core._, ProblemFilters._
mimaPreviousArtifacts := Set {
  val m = organization.value %% moduleName.value % "1.0.0"
  val sbtBinV = (sbtBinaryVersion in pluginCrossBuild).value
  val scalaBinV = (scalaBinaryVersion in update).value
  if (sbtPlugin.value)
    Defaults.sbtPluginExtra(m cross CrossVersion.Disabled, sbtBinV, scalaBinV)
  else
    m
}
mimaBinaryIssueFilters ++= Seq()

cancelable in Global := true
