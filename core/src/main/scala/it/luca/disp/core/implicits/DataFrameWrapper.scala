package it.luca.disp.core.implicits

import org.apache.spark.sql.DataFrame

class DataFrameWrapper(protected val dataFrame: DataFrame) {

  /**
   * Rename all dataFrame [[DataFrame]] columns according to SQL naming convention
   * @return original [[DataFrame]] with columns renamed according to SQL naming convention
   *         (e.g, column 'applicationStartTime' becomes 'application_start_time)
   */

  def withSqlNamingConvention(): DataFrame = {

    dataFrame.columns.foldLeft(dataFrame) {
      case (df, columnName) =>
        df.withColumnRenamed(columnName, columnName.replaceAll("[A-Z]", "_$0").toLowerCase)}
  }
}