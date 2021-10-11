package it.luca.disp.merger.app.dto

case class MergeOperationInfo(tableName: String,
                              partitionColumn: Option[String],
                              partitionValue: Option[String])

object MergeOperationInfo {

  def apply(tableName: String): MergeOperationInfo =
    MergeOperationInfo(tableName, None, None)

  def apply(tableName: String, partitionColumn: String, partitionValue: String): MergeOperationInfo =
    MergeOperationInfo(tableName, Some(partitionColumn), Some(partitionValue))
}
