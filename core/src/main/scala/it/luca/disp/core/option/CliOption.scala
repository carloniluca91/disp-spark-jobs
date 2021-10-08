package it.luca.disp.core.option

trait CliOption[A] {

  def shortOption: Char

  def longOption: String

  def optionDescription: String

  def required: Boolean = true

  def validation: Option[A => Either[String, Unit]]
}
