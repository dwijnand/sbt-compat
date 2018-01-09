package sbt
import scala.language.implicitConversions

// sbt 1 -> sbt 0.13 compatibility
// Exposes (a minimal part) of the sbt 1 API using sbt 0.13 API
// Inspired by macro-compat

object compat {
  val scalaModuleInfo = SettingKey[Option[librarymanagement.ScalaModuleInfo]]("ivyScala")

  implicit class ModuleIdOps(val _m: ModuleID) extends AnyVal {
    def withName(n: String): ModuleID = _m copy (name = n)
  }
}

package io {
  object `package` {
    type Using[Source, T] = sbt.Using[Source, T]
    val Using = sbt.Using
  }
}

package librarymanagement {
  object `package` {
    final val UpdateLogging = sbt.UpdateLogging
    type ModuleDescriptor = IvySbt#Module
    type UpdateLogging = UpdateLogging.Value
    type UpdateConfiguration = sbt.UpdateConfiguration
    type ScalaModuleInfo = IvyScala

    implicit class UpdateConfigurationOps(val _uc: UpdateConfiguration) extends AnyVal {
      def withLogging(ul: UpdateLogging): UpdateConfiguration = _uc copy (logging = ul)
    }

    implicit class RichUpdateReport(val _ur: UpdateReport) extends AnyVal {
      def configuration(c: ConfigRef) = _ur configuration c.name
    }

    val GetClassifiersModule = sbt.GetClassifiersModule
    type GetClassifiersModule = sbt.GetClassifiersModule

    object SbtPomExtraProperties {
      def POM_INFO_KEY_PREFIX =
        sbt.mavenint.SbtPomExtraProperties.POM_INFO_KEY_PREFIX
    }

    type MavenRepository = sbt.MavenRepository

    type IvySbt = sbt.IvySbt

    implicit class ModuleIDOps(val id: sbt.ModuleID) extends AnyVal {
      def withConfigurations(configurations: Option[String]): sbt.ModuleID =
        id.copy(configurations = configurations)
      def withExtraAttributes(
          extraAttributes: Map[String, String]): sbt.ModuleID =
        id.copy(extraAttributes = extraAttributes)
      def withExclusions(
          exclusions: Seq[sbt.librarymanagement.InclExclRule]): sbt.ModuleID =
        exclusions.foldLeft(id)((id0, rule) => id0.exclude(rule.org, rule.name))
      def withIsTransitive(isTransitive: Boolean): sbt.ModuleID =
        id.copy(isTransitive = isTransitive)
    }

    implicit class ArtifactOps(val artifact: sbt.Artifact) extends AnyVal {
      def withType(`type`: String): sbt.Artifact =
        artifact.copy(`type` = `type`)
      def withExtension(extension: String): sbt.Artifact =
        artifact.copy(extension = extension)
      def withClassifier(classifier: Option[String]): sbt.Artifact =
        artifact.copy(classifier = classifier)
      def withUrl(url: Option[sbt.URL]): sbt.Artifact =
        artifact.copy(url = url)
      def withExtraAttributes(
          extraAttributes: Map[String, String]): sbt.Artifact =
        artifact.copy(extraAttributes = extraAttributes)
    }

    implicit class ModuleReportOps(val report: sbt.ModuleReport) extends AnyVal {
      def withPublicationDate(publicationDate: Option[java.util.Calendar]): sbt.ModuleReport =
        report.copy(publicationDate = publicationDate.map(_.getTime))
      def withHomepage(homepage: Option[String]): sbt.ModuleReport =
        report.copy(homepage = homepage)
      def withExtraAttributes(extraAttributes: Map[String, String]): sbt.ModuleReport =
        report.copy(extraAttributes = extraAttributes)
      def withConfigurations(configurations: Vector[sbt.librarymanagement.ConfigRef]): sbt.ModuleReport =
        report.copy(configurations = configurations.map(_.name))
      def withLicenses(licenses: Vector[(String, Option[String])]): sbt.ModuleReport =
        report.copy(licenses = licenses)
      def withCallers(callers: Vector[sbt.Caller]): sbt.ModuleReport =
        report.copy(callers = callers)
    }

    implicit def toModuleReportOps(
        report: sbt.ModuleReport): ModuleReportOps =
      new ModuleReportOps(report)

    implicit class ConfigurationOps(val config: sbt.Configuration)
        extends AnyVal {
      def withExtendsConfigs(
          extendsConfigs: Vector[sbt.Configuration]): sbt.Configuration =
        config.copy(extendsConfigs = extendsConfigs.toList)
      def toConfigRef: ConfigRef =
        ConfigRef(config.name)
    }

    implicit def configurationToConfigRef(
        config: sbt.Configuration): ConfigRef =
      config.toConfigRef

    implicit class ConfigurationCompanionOps(
        val companion: sbt.Configuration.type)
        extends AnyVal {
      def of(
          id: String,
          name: String,
          description: String,
          isPublic: Boolean,
          extendsConfigs: Vector[sbt.Configuration],
          transitive: Boolean
      ): sbt.Configuration =
        sbt.Configuration(name,
                          description,
                          isPublic,
                          extendsConfigs.toList,
                          transitive)
    }

    implicit class CallerCompanionOps(val companion: sbt.Caller.type)
        extends AnyVal {
      def apply(
          caller: sbt.ModuleID,
          callerConfigurations: Vector[ConfigRef],
          callerExtraAttributes: Map[String, String],
          isForceDependency: Boolean,
          isChangingDependency: Boolean,
          isTransitiveDependency: Boolean,
          isDirectlyForceDependency: Boolean
      ): sbt.Caller =
        new sbt.Caller(
          caller,
          callerConfigurations.map(_.name),
          callerExtraAttributes,
          isForceDependency,
          isChangingDependency,
          isTransitiveDependency,
          isDirectlyForceDependency
        )
    }

    implicit class ConfigurationReportCompanionOps(
        val companion: sbt.ConfigurationReport.type)
        extends AnyVal {
      def apply(
          configuration: String,
          modules: Seq[sbt.ModuleReport],
          details: Seq[sbt.OrganizationArtifactReport]
      ): sbt.ConfigurationReport =
        new sbt.ConfigurationReport(
          configuration,
          modules,
          details,
          Nil
        )
    }

    implicit class UpdateReportCompanionOps(
        val companion: sbt.UpdateReport.type)
        extends AnyVal {
      def apply(
          cachedDescriptor: java.io.File,
          configurations: Seq[sbt.ConfigurationReport],
          stats: sbt.UpdateStats,
          stamps: Map[java.io.File, Long]
      ): sbt.UpdateReport =
        new sbt.UpdateReport(
          cachedDescriptor,
          configurations,
          stats,
          stamps
        )
    }

    implicit class UpdateStatsCompanionOps(val companion: sbt.UpdateStats.type)
        extends AnyVal {
      def apply(
          resolveTime: Long,
          downloadTime: Long,
          downloadSize: Long,
          cached: Boolean
      ): sbt.UpdateStats =
        new sbt.UpdateStats(
          resolveTime,
          downloadTime,
          downloadSize,
          cached
        )
    }

    implicit def configVectorToList(
        configs: Vector[sbt.Configuration]): List[sbt.Configuration] =
      configs.toList
    implicit def configListToVector(
        configs: List[sbt.Configuration]): Vector[sbt.Configuration] =
      configs.toVector

    implicit class GetClassifiersModuleOps(val module: GetClassifiersModule)
        extends AnyVal {
      def dependencies = module.modules
    }

    def needsIvyXmlLocal = List(sbt.Keys.deliverLocalConfiguration)
    def needsIvyXml = List(sbt.Keys.deliverConfiguration)
  }

  final case class InclExclRule(org: String = "*", name: String = "*") {
    def withOrganization(org: String): InclExclRule =
      copy(org = org)
    def withName(name: String): InclExclRule =
      copy(name = name)
  }

  final case class ConfigRef(name: String)

  object ConfigRef {
    def wrap(s: String): ConfigRef = ConfigRef(s)
  }

  object UpdateConfiguration {
    def apply() = new UpdateConfiguration(None, false, UpdateLogging.Default)
  }

  class DependencyResolution(ivy: IvySbt) {
    def wrapDependencyInModule(m: ModuleID): ModuleDescriptor = {
      val moduleSettings = InlineConfiguration(
        "dummy" % "test" % "version",
        ModuleInfo("dummy-test-project-for-resolving"),
        dependencies = Seq(m)
      )
      new ivy.Module(moduleSettings)
    }

    def update(
        module: ModuleDescriptor,
        configuration: UpdateConfiguration,
        uwconfig: UnresolvedWarningConfiguration,
        log: Logger
    ): Either[UnresolvedWarning, UpdateReport] =
      IvyActions.updateEither(
        module,
        new UpdateConfiguration(
          retrieve = None,
          missingOk = false,
          logging = UpdateLogging.DownloadOnly
        ),
        UnresolvedWarningConfiguration(),
        LogicalClock.unknown,
        None,
        log
      )
  }

  package ivy {
    object IvyDependencyResolution {
      def apply(configuration: IvyConfiguration): DependencyResolution =
        new DependencyResolution(new IvySbt(configuration))
    }
  }
}

package internal {
  package librarymanagement {
    object `package`
  }

  package util {
    object JLine {
      def usingTerminal[T](f: jline.Terminal => T): T =
        sbt.JLine.usingTerminal(f)
    }
  }

  object `package` {
    type BuildStructure = sbt.BuildStructure
    type LoadedBuildUnit = sbt.LoadedBuildUnit
  }
}
