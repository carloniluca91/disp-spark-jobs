package it.luca.disp.core.option

sealed trait CliOption[A] {

  def shortOption: Char

  def longOption: String

  def description: String

  def required: Boolean

  protected[option] def optionalValidation: Option[A => Either[String, Unit]]
}

sealed trait Required[A] extends CliOption[A] {

  override def required: Boolean = true
}

sealed trait WithValidation[A] extends CliOption[A] {

  def validation: A => Either[String, Unit]
}

sealed trait WithoutValidation[A] extends CliOption[A] {

  override protected[option] def optionalValidation: Option[A => Either[String, Unit]] = None
}

trait RequiredWithoutValidation[A] extends Required[A] with WithoutValidation[A]

trait RequiredWithValidation[A] extends Required[A] with WithValidation[A] {

  override protected[option] def optionalValidation: Option[A => Either[String, Unit]] = Some(validation)
}