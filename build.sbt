ThisBuild / scalaVersion := "2.13.3"
ThisBuild / organization := "se.his"

lazy val hello = (project in file("."))
  .settings(
    organization := "se.his",
    name := "parallelism",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % Test,
    libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "0.2.0",
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.9",
    scalacOptions += "-deprecation"
  )
