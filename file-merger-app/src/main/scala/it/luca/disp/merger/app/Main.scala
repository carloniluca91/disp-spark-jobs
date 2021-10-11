package it.luca.disp.merger.app

import it.luca.disp.core.Logging
import it.luca.disp.merger.app.option.{MergerAppArguments, MergerAppOptionParser}

object Main
  extends App
    with Logging {

  log.info("Started file merger main class")
  MergerAppOptionParser.parse(args, MergerAppArguments()) match {

    case Some(arguments) =>
      log.info(s"Successfully parsed file merger app arguments\n\n$arguments")

    case None => log.error("Error while parsing file merger app arguments")
  }
}
