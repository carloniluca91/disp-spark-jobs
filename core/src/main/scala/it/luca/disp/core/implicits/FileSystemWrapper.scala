package it.luca.disp.core.implicits

import it.luca.disp.core.Logging
import org.apache.hadoop.fs.{FileSystem, Path}

import scala.io.Source

class FileSystemWrapper(protected val fs: FileSystem)
  extends Logging {

  /**
   * Read content of file at given path as a single string
   * @param path [[Path]] to be read
   * @return string representing content of given file
   */

  def readFileAsString(path: String): String = {

    Source.fromInputStream(fs.open(new Path(path)))
      .getLines()
      .mkString(" ")
  }
}
