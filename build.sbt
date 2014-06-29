enablePlugins(LibPlugin)

organization := "fh"

name := "slick-ways"

version := "0.1"

libraryDependencies ++= Seq(
  "com.h2database"      % "h2"          % "1.4.179",
  "org.xerial"          % "sqlite-jdbc" % "3.7.2",
  "com.typesafe.slick" %% "slick"       % "2.1.0",
  "org.slf4j"           % "slf4j-nop"   % "1.7.7",
  "org.scalatest"      %% "scalatest"   % "2.2.0" % "test"
)
