name := "spray-client-example"

version := "1.0"

resolvers += "spray repo" at "http://repo.spray.io/"

libraryDependencies ++= {
  val sprayVersion = "1.3.3"
  val akkaVersion = "2.3.9"
  Seq(
    "io.spray" %% "spray-http"   % sprayVersion,
    "io.spray" %% "spray-httpx"   % sprayVersion,
    "io.spray" %% "spray-util"   % sprayVersion,
    "io.spray" %% "spray-can"   % sprayVersion,
    "io.spray" %% "spray-json"   % "1.3.2",
    "io.spray" %% "spray-client"   % sprayVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.2"
  )
}



scalaVersion := "2.11.6"
    