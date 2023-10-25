addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.4")

//addSbtPlugin("com.github.cb372" % "sbt-explicit-dependencies" % "0.2.11")

//addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.2")

//addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.12")

//addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.8.1")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.1.5")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")

//addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

//addSbtPlugin("io.get-coursier" % "sbt-coursier" % "2.0.8")

//addSbtPlugin("com.dwijnand" % "sbt-reloadquick" % "1.0.0")

//addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.5.0")
addSbtPlugin("org.jmotor.sbt" % "sbt-dependency-updates" % "1.2.7")

addSbtPlugin("net.vonbuchholtz" % "sbt-dependency-check" % "5.1.0")

ThisBuild / libraryDependencySchemes ++= Seq("org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always)
