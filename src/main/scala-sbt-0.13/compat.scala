package sbt

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

    type MavenRepository = sbt.MavenRepository
    type Resolver = sbt.Resolver

    implicit class ModuleIDOps(val _m: ModuleID) extends AnyVal {
      def withConfigurations(configurations: Option[String]): ModuleID =
        _m.copy(configurations = configurations)

      def withExtraAttributes(extraAttributes: Map[String, String]): ModuleID =
        _m.copy(extraAttributes = extraAttributes)

      def withExclusions(exclusions: Seq[InclExclRule]): ModuleID =
        exclusions.foldLeft(_m)((id0, rule) => id0.exclude(rule.org, rule.name))

      def withIsTransitive(isTransitive: Boolean): ModuleID = _m.copy(isTransitive = isTransitive)
    }

    type Artifact = sbt.Artifact
    implicit class ArtifactOps(val _a: Artifact) extends AnyVal {
      def withType(`type`: String): Artifact = _a.copy(`type` = `type`)
      def withExtension(extension: String): Artifact = _a.copy(extension = extension)
      def withClassifier(classifier: Option[String]): Artifact = _a.copy(classifier = classifier)
      def withUrl(url: Option[URL]): Artifact = _a.copy(url = url)
      def withExtraAttributes(extraAttributes: Map[String, String]): Artifact =
        _a.copy(extraAttributes = extraAttributes)
    }

    type ModuleID = sbt.ModuleID
    implicit class ModuleReportOps(val _mr: ModuleReport) extends AnyVal {
      def withPublicationDate(publicationDate: Option[java.util.Calendar]): ModuleReport =
        _mr.copy(publicationDate = publicationDate.map(_.getTime))

      def withHomepage(homepage: Option[String]): ModuleReport = _mr.copy(homepage = homepage)

      def withExtraAttributes(extraAttributes: Map[String, String]): ModuleReport =
        _mr.copy(extraAttributes = extraAttributes)

      def withConfigurations(configurations: Vector[ConfigRef]): ModuleReport =
        _mr.copy(configurations = configurations.map(_.name))

      def withLicenses(licenses: Vector[(String, Option[String])]): ModuleReport =
        _mr.copy(licenses = licenses)

      def withCallers(callers: Vector[Caller]): ModuleReport = _mr.copy(callers = callers)
    }

    implicit class ConfigurationOps(val _c: Configuration) extends AnyVal {
      def withExtendsConfigs(extendsConfigs: Vector[Configuration]): Configuration =
        _c.copy(extendsConfigs = extendsConfigs.toList)
    }

    implicit class ConfigurationCompanionOps(val companion: Configuration.type) extends AnyVal {
      def of(
          id: String,
          name: String,
          description: String,
          isPublic: Boolean,
          extendsConfigs: Vector[Configuration],
          transitive: Boolean
      ): Configuration =
        Configuration(name, description, isPublic, extendsConfigs.toList, transitive)
    }

    implicit class CallerCompanionOps(val companion: Caller.type) extends AnyVal {
      def apply(
          caller: ModuleID,
          callerConfigurations: Vector[ConfigRef],
          callerExtraAttributes: Map[String, String],
          isForceDependency: Boolean,
          isChangingDependency: Boolean,
          isTransitiveDependency: Boolean,
          isDirectlyForceDependency: Boolean
      ): Caller =
        new Caller(
          caller,
          callerConfigurations.map(_.name),
          callerExtraAttributes,
          isForceDependency,
          isChangingDependency,
          isTransitiveDependency,
          isDirectlyForceDependency
        )
    }

    implicit class ConfigurationReportCompanionOps(val companion: ConfigurationReport.type)
        extends AnyVal {
      def apply(
          configuration: ConfigRef,
          modules: Seq[ModuleReport],
          details: Seq[OrganizationArtifactReport]
      ): ConfigurationReport =
        new ConfigurationReport(configuration.name, modules, details, Nil)
    }

    type UpdateReport = sbt.UpdateReport
    implicit class UpdateReportCompanionOps(val companion: UpdateReport.type) extends AnyVal {
      def apply(
          cachedDescriptor: File,
          configurations: Seq[ConfigurationReport],
          stats: UpdateStats,
          stamps: Map[File, Long]
      ): UpdateReport =
        new UpdateReport(cachedDescriptor, configurations, stats, stamps)
    }

    implicit class UpdateStatsCompanionOps(val companion: UpdateStats.type) extends AnyVal {
      def apply(
          resolveTime: Long,
          downloadTime: Long,
          downloadSize: Long,
          cached: Boolean
      ): UpdateStats =
        new UpdateStats(resolveTime, downloadTime, downloadSize, cached)
    }

    implicit class GetClassifiersModuleOps(val _m: GetClassifiersModule) extends AnyVal {
      def dependencies = _m.modules
    }
  }

  final case class InclExclRule(org: String = "*", name: String = "*") {
    def withOrganization(org: String): InclExclRule = copy(org = org)
    def withName(name: String): InclExclRule = copy(name = name)
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
    object `package` {
      type IvySbt = sbt.IvySbt
    }

    package mavenint {
      object SbtPomExtraProperties {
        final val POM_INFO_KEY_PREFIX = sbt.mavenint.SbtPomExtraProperties.POM_INFO_KEY_PREFIX
      }
    }
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

package util {
  object `package` {
    type Logger = sbt.Logger
  }
}

