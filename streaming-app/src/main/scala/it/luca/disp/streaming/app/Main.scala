package it.luca.disp.streaming.app

import it.luca.disp.core.Logging
import it.luca.disp.streaming.app.option.{StreamingAppArguments, StreamingOptionParser}

object Main
  extends App
    with Logging {

  log.info("Started streaming main class")
  StreamingOptionParser.parse(args, StreamingAppArguments()) match {
    case Some(arguments) =>
      log.info(s"Successfully parsed streaming app arguments\n\n$arguments")

    case None => log.error("Error while parsing streaming app arguments")
  }
}