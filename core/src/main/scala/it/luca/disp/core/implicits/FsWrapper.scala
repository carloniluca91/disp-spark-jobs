package it.luca.disp.core.implicits

import it.luca.disp.core.Logging
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path}

import scala.util.matching.Regex

class FsWrapper(protected val fs: FileSystem)
  extends Logging {

  def getPartitionDirectories(tableLocation: String): Seq[FileStatus] = {

    val partitionDirectories: Seq[FileStatus] = fs.listStatus(new Path(tableLocation))
      .filter(p => p.isDirectory && FsWrapper.PartitionDirectoryRegex
        .findFirstMatchIn(p.getPath.getName).isDefined)

    val partitionDirectoriesDescription: String = partitionDirectories.map { p => s"  ${p.getPath.getName}" }.mkString("\n").concat("\n")
    log.info(s"Found ${partitionDirectories.size} partition directories within table location $tableLocation\n\n$partitionDirectoriesDescription")
    partitionDirectories
  }

  def getTotalSizeOfFilesInBytes(path: Path): Long = {

    fs.listStatus(path)
      .filter(_.isFile)
      .map(_.getLen).sum
  }


  def listSmallFiles(path: Path, minimumFileSize: Long): Seq[FileStatus] = {

    fs.listStatus(path)
      .filter(f => f.isFile && f.getLen < minimumFileSize)
  }

  def needsToBeMerged(path: Path, minimumFileSize: Long, maximumNumberOfSmallFiles: Int): Boolean = {

    fs.listStatus(path)
      .count(f => f.isFile && f.getLen < minimumFileSize) > maximumNumberOfSmallFiles
  }
}

object FsWrapper {

  val PartitionDirectoryRegex: Regex = "^\\w+=(.+)$".r
}
