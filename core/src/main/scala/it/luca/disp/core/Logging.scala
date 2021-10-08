package it.luca.disp.core

import org.slf4j.{Logger, LoggerFactory}

trait Logging {

  protected final val log: Logger = LoggerFactory.getLogger(this.getClass)

}
