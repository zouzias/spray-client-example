package org.zouzias.spray.examples.client

import akka.actor.ActorSystem
import akka.event.Logging
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * A simple example using spary-client
 */
object Main extends App {

  val hostname : String = "www.spray.io"

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("simple-spray-client")

  import system.dispatcher // execution context for futures

  // Timeout on futures
  implicit val timeout = Timeout(5 seconds) // needed for `?` below

  // Logging
  val log = Logging(system, getClass)

  // Create a pipeline for the hostname/port
  val pipeline = createPipeline(hostname)

  // Request the main site
  val request = Get("/")
  val responseFuture: Future[HttpResponse] = pipeline.flatMap(_(request))

  // Callback for request
  responseFuture onComplete {
    case Success(HttpResponse(status, entity, headers, protocol)) => {
      log.info("Http response is {} ", status.toString())
      log.info("Http protocol is {} ", protocol.toString)
      headers.foreach(header => {
        log.info("Header " + header.name + " = " + header.value)
      })
      shutdown()
    }
    case Success(somethingUnexpected) => {
      log.warning("The spray.io website didn't response properly: '{}'.", somethingUnexpected)
      shutdown()
    }

    case Failure(error) => {
      log.error(error, "Failure on response of www.spray.io")
      shutdown()
    }
  }

  // Wait for termination of actor system
  system.awaitTermination()

  def createPipeline(hostname : String, port : Int = 80): Future[SendReceive] = {
    // Quoting from http://spray.io/documentation/1.2.2/spray-client/#usage
    /**
     * The central element of a spray-client pipeline is sendReceive, which produces a function HttpRequest => Future[HttpResponse]
     * (this function type is also aliased to SendReceive). When called without parameters sendReceive will automatically
     * use the IO(Http) extension of an implicitly available ActorSystem to access the spray-can Request-level API.
     * All requests must therefore either carry an absolute URI or an explicit Host header.
     */
    val pipeline: Future[SendReceive] =
      for (
        Http.HostConnectorInfo(connector, _) <-
        IO(Http) ? Http.HostConnectorSetup(hostname, port = port)
      ) yield sendReceive(connector)
    pipeline
  }

  /**
   * Shutdown the actor system
   */
  def shutdown(): Unit = {
    log.info("Shutting down the akka system...")
    system.shutdown()
  }
}
