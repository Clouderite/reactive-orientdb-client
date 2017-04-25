organization := "com.devesion.orientdb"
name := "reactive-client"
version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaV = "2.5.0"
  val akkaHttpV = "10.0.5"

  Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "com.typesafe.akka"   %% "akka-actor"    % akkaV,
    "com.orientechnologies" % "orientdb-core" % "2.2.14",
    "com.orientechnologies" % "orientdb-object" % "2.2.14",
    "com.orientechnologies" % "orientdb-client" % "2.2.14",
    "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % "2.4.11",
    "org.scalatest"       %% "scalatest" % "3.0.1" % "test",
    "org.mockito"         % "mockito-core" % "1.10.19"
  )
}

fork := true
mappings in (Compile, packageBin) ++= (mappings in (Compile, packageSrc)).value
scalacOptions ++= Seq("-Xmax-classfile-name", "110")