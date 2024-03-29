val http4sVersion   = "0.21.4"
val circeVersion    = "0.13.0"
val doobieVersion   = "0.9.0"
val zioVersion      = "1.0.0-RC18-2"
val zioCatsVersion  = "2.0.0.0-RC13"
val zioReactVersion = "1.0.3.5-RC7"
val fs2Version      = "2.3.0"
//val silencerVersion = "1.6.0"
val acyclicVersion  = "0.2.0"
val calibanVersion  = "0.7.6"
val sttpVersion     = "2.1.1"

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
  //Wart.TraversableOps,
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
    scalaVersion := "2.13.12",
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
      //"-P:silencer:checkUnused"
    ),
    dependencyCheckCveUrlModified := Some(
      new URL("http://nvdmirror.sml.io/")
    ),
    dependencyCheckCveUrlBase := Some("http://nvdmirror.sml.io/"),
    dependencyCheckAssemblyAnalyzerEnabled := Some(false),
    dependencyCheckFormat := "All",
    Compile / compile / wartremoverWarnings  := Warts.all
      .diff(wartremoverCompileExclusions),
    Test / compile / wartremoverWarnings := Warts.all
      .diff(wartremoverTestCompileExclusions),
    Compile / console / scalacOptions ~= filterConsoleScalacOptions,
    Test / console / scalacOptions ~= filterConsoleScalacOptions,
    console / scalacOptions ~= filterConsoleScalacOptions,

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
      "dev.zio"                      %% "zio-interop-cats"            % zioCatsVersion,
      "dev.zio"                      %% "zio-interop-reactivestreams" % zioReactVersion,
      "co.fs2"                       %% "fs2-reactive-streams"        % fs2Version,
      "org.flywaydb"                 % "flyway-core"                  % "6.4.1",
      "com.h2database"               % "h2"                           % "1.4.200",
      "org.xerial"                   % "sqlite-jdbc"                  % "3.31.1",
      "ch.qos.logback"               % "logback-classic"              % "1.2.3",
      "com.github.pureconfig"        %% "pureconfig"                  % "0.12.3",
      "com.lihaoyi"                  %% "sourcecode"                  % "0.2.1",
      "com.lihaoyi"                  %% "acyclic"                     % acyclicVersion % "provided",
      //("com.github.ghik" % "silencer-lib" % silencerVersion % "provided").cross(CrossVersion.full),
      // plugins
      compilerPlugin("com.lihaoyi" %% "acyclic" % acyclicVersion),
      compilerPlugin(
        ("io.tryp" % "splain" % "1.0.3").cross(CrossVersion.patch)
      ), //TODO comment to have the macros "zio.macros.annotation.accessible" working
      compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
      compilerPlugin(
        ("org.typelevel" % "kind-projector" % "0.13.2").cross(CrossVersion.full)
      ),
      //compilerPlugin(("com.github.ghik" % "silencer-plugin" % silencerVersion).cross(CrossVersion.full))
    )
  )

//release
/*
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
*/
Global / onChangedBuildSource := ReloadOnSourceChanges

