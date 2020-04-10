val http4sVersion   = "0.21.1"
val circeVersion    = "0.13.0"
val doobieVersion   = "0.8.8"
val zioVersion      = "1.0.0-RC18-2"
val silencerVersion = "1.6.0"
val acyclicVersion  = "0.2.0"
val sttpVersion     = "2.0.5"

val wartremoverCompileExclusions = Seq(
  Wart.Overloading,
  Wart.PublicInference,
  Wart.Equals,
  Wart.ImplicitParameter,
  Wart.Serializable,
  Wart.JavaSerializable,
  Wart.DefaultArguments,
  Wart.Var,
  Wart.Product,
  Wart.Any,
  Wart.ExplicitImplicitTypes,
  Wart.ImplicitConversion,
  Wart.Nothing,
  Wart.MutableDataStructures
)

val wartremoverTestCompileExclusions = wartremoverCompileExclusions ++ Seq(
  Wart.DefaultArguments,
  Wart.Var,
  Wart.AsInstanceOf,
  Wart.IsInstanceOf,
  Wart.TraversableOps,
  Wart.Option2Iterable,
  Wart.JavaSerializable,
  Wart.FinalCaseClass,
  Wart.NonUnitStatements
)

val filterConsoleScalacOptions = { options: Seq[String] =>
  options.filterNot(
    Set(
      "-Xfatal-warnings",
      "-Ywarn-unused:_",
      //"-Ywarn-dead-code",
      "-Ywarn-value-discard",
      "-Ywarn-numeric-widen",
      "-Ywarn-extra-implicit"
    )
  )
}


lazy val root = (project in file("."))
  .settings(
    name := "zio-ivy2-cache-manager",
    version := "0.1",
    scalaVersion := "2.13.1",
    licenses := Seq(
      "MIT" -> url(
        s"https://github.com/mschuwalow/${name.value}/blob/v${version.value}/LICENSE"
      )
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    scalacOptions := Seq(
      "-feature",
      "-deprecation",
      "-explaintypes",
      "-unchecked",
      "-encoding",
      "UTF-8",
      "-language:higherKinds",
      "-language:existentials",
      "-Xfatal-warnings",
      "-Xlint:-infer-any,_",
      "-Xlint:constant",
      //"-Xlog-implicits",
      "-Ywarn-value-discard",
      "-Ywarn-numeric-widen",
      "-Ywarn-extra-implicit",
      "-Ywarn-unused:_",
      "-Ymacro-annotations"
    ) ++ (if (isSnapshot.value) Seq.empty
    else
      Seq(
        "-opt:l:inline"
      )) ++ Seq(
      "-P:acyclic:force",
      //"-P:splain:all", //TODO comment to have the macros "zio.macros.annotation.accessible" working
      "-P:silencer:checkUnused"
    ),
    dependencyCheckCveUrlModified := Some(
      new URL("http://nvdmirror.sml.io/")
    ),
    dependencyCheckCveUrlBase := Some("http://nvdmirror.sml.io/"),
    dependencyCheckAssemblyAnalyzerEnabled := Some(false),
    dependencyCheckFormat := "All",
    wartremoverWarnings in (Compile, compile) := Warts.all
      .diff(wartremoverCompileExclusions),
    wartremoverWarnings in (Test, compile) := Warts.all
      .diff(wartremoverTestCompileExclusions),
    scalacOptions in (Compile, console) ~= filterConsoleScalacOptions,
    scalacOptions in (Test, console) ~= filterConsoleScalacOptions,
    scalacOptions in console ~= filterConsoleScalacOptions,

    libraryDependencies ++= Seq(
      "org.http4s"                   %% "http4s-blaze-server"         % http4sVersion,
      "org.http4s"                   %% "http4s-dsl"                  % http4sVersion,
      "io.circe"                     %% "circe-core"                  % circeVersion,
      "io.circe"                     %% "circe-generic"               % circeVersion,
      "io.circe"                     %% "circe-optics"                % circeVersion,
      "io.circe"                     %% "circe-literal"               % circeVersion % Test,
      "org.tpolecat"                 %% "doobie-core"                 % doobieVersion,
      "org.tpolecat"                 %% "doobie-h2"                   % doobieVersion,
      "org.tpolecat"                 %% "doobie-hikari"               % doobieVersion,
      "org.tpolecat"                 %% "doobie-quill"                % doobieVersion,
      "com.softwaremill.sttp.client" %% "core"                        % sttpVersion,
      "com.softwaremill.sttp.client" %% "http4s-backend"              % sttpVersion % Test,
      "dev.zio"                      %% "zio"                         % zioVersion,
      "dev.zio"                      %% "zio-test"                    % zioVersion % Test,
      "dev.zio"                      %% "zio-test-sbt"                % zioVersion % Test,
      "dev.zio"                      %% "zio-interop-cats"            % "2.0.0.0-RC12",
      "dev.zio"                      %% "zio-interop-reactivestreams" % "1.0.3.5-RC6",
      "co.fs2"                       %% "fs2-reactive-streams"        % "2.2.2",
      "org.flywaydb"                 % "flyway-core"                  % "6.3.0",
      "com.h2database"               % "h2"                           % "1.4.200",
      "org.slf4j"                    % "slf4j-log4j12"                % "1.7.30",
      "com.github.pureconfig"        %% "pureconfig"                  % "0.12.3",
      "com.lihaoyi"                  %% "sourcecode"                  % "0.2.1",
      "com.lihaoyi"                  %% "acyclic"                     % acyclicVersion % "provided",
      ("com.github.ghik" % "silencer-lib" % silencerVersion % "provided")
        .cross(CrossVersion.full),
      // plugins
      compilerPlugin("com.lihaoyi" %% "acyclic" % acyclicVersion),
      compilerPlugin(
        ("io.tryp" % "splain" % "0.5.1").cross(CrossVersion.patch)
      ), //TODO comment to have the macros "zio.macros.annotation.accessible" working
      compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
      compilerPlugin(
        ("org.typelevel" % "kind-projector" % "0.11.0").cross(CrossVersion.full)
      ),
      compilerPlugin(
        ("com.github.ghik" % "silencer-plugin" % silencerVersion)
          .cross(CrossVersion.full)
      )
    )
  )

//release
import ReleaseTransformations._
import ReleasePlugin.autoImport._
import sbtrelease.{ Git, Utilities }
import Utilities._

releaseProcess := Seq(
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  pushChanges,
  tagRelease,
  mergeReleaseVersion,
  ReleaseStep(releaseStepTask(publish in Docker)),
  setNextVersion,
  commitNextVersion,
  pushChanges
)

val mergeBranch = "master"

val mergeReleaseVersion = ReleaseStep(action = st => {
  val git       = st.extract.get(releaseVcs).get.asInstanceOf[Git]
  val curBranch = (git.cmd("rev-parse", "--abbrev-ref", "HEAD") !!).trim
  st.log.info(s"####### current branch: $curBranch")
  git.cmd("checkout", mergeBranch) ! st.log
  st.log.info(s"####### pull $mergeBranch")
  git.cmd("pull") ! st.log
  st.log.info(s"####### merge")
  git.cmd("merge", curBranch, "--no-ff", "--no-edit") ! st.log
  st.log.info(s"####### push")
  git.cmd("push", "origin", s"$mergeBranch:$mergeBranch") ! st.log
  st.log.info(s"####### checkout $curBranch")
  git.cmd("checkout", curBranch) ! st.log
  st
})

Global / onChangedBuildSource := ReloadOnSourceChanges
