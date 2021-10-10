package it.luca.disp.core.option

import it.luca.disp.core.BaseTestSuite

abstract class CustomOptionParserTest
  extends BaseTestSuite {

  protected final def getArgs(s: Map[CliOption[_, _], String]): Seq[String] = {

    s.flatMap { case (cliOption, value) =>
      s"-${cliOption.shortOption.toString}" :: value :: Nil
    }.toSeq
  }

  protected final def testOptionValidation[T](optionWithValidation: WithValidation[T, _],
                                              validValue: T,
                                              invalidValue: T): Unit = {

    val validation: T => Either[String, Unit] = optionWithValidation.validation
    validation(validValue) shouldBe Right(_: Unit)
    validation(invalidValue) shouldBe Left(_: String)
  }

  protected final def testOptionValidation[T](optionWithValidation: WithValidation[T, _],
                                              validValues: Seq[T],
                                              invalidValues: Seq[T]): Unit = {

    val validation: T => Either[String, Unit] = optionWithValidation.validation
    validValues.foreach { x => validation(x) shouldBe Right(_: Unit) }
    invalidValues.foreach { x => validation(x) shouldBe Left(_: String) }
  }
}
