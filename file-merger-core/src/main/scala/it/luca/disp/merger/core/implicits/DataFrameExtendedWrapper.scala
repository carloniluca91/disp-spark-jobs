package it.luca.disp.merger.core.implicits

import it.luca.disp.core.implicits.DataFrameWrapper
import org.apache.spark.sql.DataFrame

class DataFrameExtendedWrapper(override protected val dataFrame: DataFrame)
  extends DataFrameWrapper(dataFrame) {
}
