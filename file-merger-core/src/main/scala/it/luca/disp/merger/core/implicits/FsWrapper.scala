package it.luca.disp.merger.core.implicits

import it.luca.disp.core.Logging
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path}

import scala.util.matching.Regex

class FsWrapper(protected val fs: FileSystem)
  extends Logging {

  /**
   * Check whether given path contains more than n files whose size is smaller than given fileSize
   * @param path [[Path]] to check
   * @param n number of maximum small files
   * @param fileSize minimum file size (in bytes)
   * @return true if, within given path, more than n small files are found
   */

  def containsTooManySmallFiles(path: Path, n: Int, fileSize: Long): Boolean = {

    fs.listStatus(path)
      .count(f => f.isFile && f.getLen < fileSize) > n
  }

  /**
   * Retrieve list of partition directories within a given HDFS path
   * @param tableLocation HDFS location to check
   * @return collection of [[FileStatus]] representing directories named like 'partition_column=partition_value'
   */

  def getPartitionDirectories(tableLocation: String): Seq[FileStatus] = {

    val partitionDirectories: Seq[FileStatus] = fs.listStatus(new Path(tableLocation))
      .filter(p => p.isDirectory && FsWrapper.PartitionDirectoryRegex
        .findFirstMatchIn(p.getPath.getName).isDefined)

    if (partitionDirectories.nonEmpty) {
      val partitionDirectoriesDescription: String = partitionDirectories.map { p => s"  ${p.getPath.getName}" }.mkString("\n").concat("\n")
      log.info(s"Found ${partitionDirectories.size} partition directories within table location $tableLocation\n\n$partitionDirectoriesDescription")
    } else {
      log.warn(s"No partitions found at table location $tableLocation")
    }

    partitionDirectories
  }

  /**
   * Compute aggregated size of files within given path
   * @param path input [[Path]]
   * @return sum of size of all files within given path
   */

  def getTotalSizeOfFilesInBytes(path: Path): Long = {

    fs.listStatus(path)
      .collect { case f: FileStatus if f.isFile => f.getLen }
      .sum
  }
}

object FsWrapper {

  val PartitionDirectoryRegex: Regex = "^\\w+=(.+)$".r
}
