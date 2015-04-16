
organization := "com.mongoline"

name := "mongoline"

version := "0.0.3-SNAPSHOT"

resolvers += "Sepage Release" at "http://vps95041.ovh.net:8081/content/repositories/sepage-release/"

resolvers += "Sepage Snapshot" at "http://vps95041.ovh.net:8081/content/repositories/sepage-snapshot/"

resolvers += Resolver.mavenLocal

scalaVersion := "2.10.5"

scalacOptions in(Compile, compile) ++= List("-deprecation", "-unchecked", "-feature")

javacOptions in(Compile, compile) ++= List("-Xlint", "-source", "1.7", "-target", "1.7")

libraryDependencies ++= Seq(
  "com.github.jmkgreen.morphia" % "morphia" % "1.2.3",
  "com.github.jmkgreen.morphia" % "morphia-logging-slf4j" % "1.2.3",
  "org.mongodb" % "mongo-java-driver" % "2.11.0"
)

publishTo := {
  val nexus = "http://vps95041.ovh.net:8081/content/repositories/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "sepage-snapshot/") 
  else
    Some("releases"  at nexus + "sepage-release/")
}

credentials += Credentials(new File("/Users/lpereira/dev/fedora-20/.ivy2/.credentials"))

