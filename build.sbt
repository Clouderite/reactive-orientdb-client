import java.util.{Date, TimeZone}

def timestamp(): String = {
  val sdf = new java.text.SimpleDateFormat("yyyyMMddHHmmss")
  sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
  sdf.format(new Date(System.currentTimeMillis()))
}

lazy val commonSettings = Seq(
  scalaVersion := "2.11.8",

  organization := "io.clouderite.orientdb",
  name := "reactive-client",
  version := "1.0.1." + timestamp(),

  ivyLoggingLevel := UpdateLogging.Full,
  publishArtifact := true,
  publishArtifact in Test := false,
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false },
  publishTo := Some("Sonatype Releases Nexus" at "https://maven.clouderite.io/nexus/content/repositories/releases/"),
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),

  resolvers += "Sonatype Releases Nexus" at "https://maven.clouderite.io/nexus/content/repositories/releases/",

  libraryDependencies ++= {
    val akkaV = "2.5.0"

    Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "com.typesafe.akka" %% "akka-actor" % akkaV,
      "com.orientechnologies" % "orientdb-core" % "2.2.20",
      "com.orientechnologies" % "orientdb-object" % "2.2.20",
      "com.orientechnologies" % "orientdb-client" % "2.2.20",
      "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % "2.4.11",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "org.mockito" % "mockito-core" % "1.10.19"
    )
  },

  fork := true,
  mappings in(Compile, packageBin) ++= (mappings in(Compile, packageSrc)).value,
  scalacOptions ++= Seq("-Xmax-classfile-name", "110")
)

lazy val app = project
  .in(file("."))
  .settings(commonSettings)

