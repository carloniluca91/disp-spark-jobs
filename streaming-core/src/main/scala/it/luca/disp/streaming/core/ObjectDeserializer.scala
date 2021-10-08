package it.luca.disp.streaming.core

import com.fasterxml.jackson.databind.{DeserializationFeature, JavaType, ObjectMapper}
import it.luca.disp.core.Logging
import it.luca.disp.streaming.model.MsgWrapper

import java.io.IOException
import scala.collection.JavaConversions._

object ObjectDeserializer
  extends Logging {

  protected final val mapper: ObjectMapper = new ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  @throws[IOException]
  def deserializeStringAsCollection[T](string: String, tClass: Class[T]): Seq[T] = {

    val className: String = tClass.getSimpleName
    val javaType: JavaType = mapper.getTypeFactory.constructCollectionType(classOf[java.util.List[_]], tClass)
    log.info("Deserializing given string as a collection of instances of {}", className)
    val instances: java.util.List[T] = mapper.readValue(string, javaType)
    log.info("Successfully deserialized given string as a collection of instances of {}", className)
    instances
  }

  @throws[IOException]
  def deserializeAsMsgWrapper[T](string: String, tClass: Class[T]): MsgWrapper[T] = {

    val (msgWrapperClassName, payloadClassName): (String, String) = (classOf[MsgWrapper[_]].getSimpleName, tClass.getSimpleName)
    val javaType: JavaType = mapper.getTypeFactory.constructParametricType(classOf[MsgWrapper[_]], tClass)
    log.info(s"Deserializing given string as a $msgWrapperClassName containing a $payloadClassName instance")
    val instance: MsgWrapper[T] = mapper.readValue(string, javaType)
    log.info(s"Successfully deserialized given string as a $msgWrapperClassName containing a $payloadClassName instance")
    instance
  }
}
