val sbtcompat = project.in(file(".")).settings(name := "sbt-compat")

ThisBuild / organization := "com.dwijnand"
ThisBuild /     licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild /  description := "A compatibility library; backports parts of sbt 1's public API in sbt 0.13"
ThisBuild /   developers := List(Developer("dwijnand", "Dale Wijnand", "dale wijnand gmail com", url("https://dwijnand.com")))
ThisBuild /    startYear := Some(2017)
ThisBuild /     homepage := scmInfo.value map (_.browseUrl)
ThisBuild /      scmInfo := Some(ScmInfo(url("https://github.com/dwijnand/sbt-compat"), "scm:git:git@github.com:dwijnand/sbt-compat.git"))

enablePlugins(SbtPlugin)
Global / sbtVersion  := "0.13.8" // must be Global, otherwise ^^ won't change anything
    crossSbtVersions := List("0.13.8", "1.0.0") // 0.13.8 has UpdateLogging.Default

ThisBuild / scalaVersion := (CrossVersion.partialVersion((pluginCrossBuild / sbtVersion).value) match {
  case Some((0, 13)) => "2.10.7"
  case Some((1, _))  => "2.12.12"
  case _             => sys error s"Unhandled sbt version ${(pluginCrossBuild / sbtVersion).value}"
})

ThisBuild / scalacOptions ++= Seq("-encoding", "utf8")
ThisBuild / scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")
ThisBuild / scalacOptions  += "-language:_,-reflectiveCalls"
ThisBuild / scalacOptions  += "-Xfuture"
ThisBuild / scalacOptions  += "-Yno-adapted-args"
ThisBuild / scalacOptions  += "-Ywarn-dead-code"
ThisBuild / scalacOptions  += "-Ywarn-numeric-widen"
ThisBuild / scalacOptions  += "-Ywarn-value-discard"

mimaPreviousArtifacts := Set {
  val m = organization.value %% moduleName.value % "1.0.0"
  val sbtBinV = (pluginCrossBuild / sbtBinaryVersion).value
  val scalaBinV = (update / scalaBinaryVersion).value
  if (sbtPlugin.value)
    Defaults.sbtPluginExtra(m cross CrossVersion.disabled, sbtBinV, scalaBinV)
  else
    m
}

import com.typesafe.tools.mima.core._, ProblemFilters._
mimaBinaryIssueFilters ++= Seq()

Global / cancelable := true
