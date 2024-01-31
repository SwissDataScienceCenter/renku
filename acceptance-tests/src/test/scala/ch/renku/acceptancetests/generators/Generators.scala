/*
 * Copyright 2024 Swiss Data Science Center (SDSC)
 * A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
 * Eidgenössische Technische Hochschule Zürich (ETHZ).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.renku.acceptancetests.generators

import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric.{NonNegative, Positive}
import eu.timepit.refined.string.Url
import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen}

import java.time._
import java.time.temporal.ChronoUnit.{DAYS, MINUTES => MINS}
import scala.concurrent.duration._
import scala.language.implicitConversions

object Generators {

  def nonEmptyStrings(minLength: Int = 3, maxLength: Int = 10, charsGenerator: Gen[Char] = alphaChar): Gen[String] = {
    require(minLength > 0, "minLength of generated string cannot be < 1")
    require(maxLength > 0, "maxLength of generated string cannot be < 1")
    require(minLength <= maxLength, s"Cannot generate string with minLength $minLength > maxLength $maxLength")

    for {
      length <- choose(minLength, maxLength)
      chars  <- listOfN(length, charsGenerator)
    } yield chars.mkString("")
  }

  def sentenceContaining(phrase: String): Gen[String] = for {
    prefix <- nonEmptyStrings(minLength = 1)
    suffix <- nonEmptyStrings(minLength = 1)
  } yield s"$prefix $phrase $suffix"

  def paragraph(minWords: Int Refined Positive = 2,
                maxWords: Int Refined Positive = 10
  ): Gen[String Refined NonEmpty] = {
    require(minWords.value <= maxWords.value)
    (
      for {
        words     <- Gen.choose(minWords.value, maxWords.value)
        paragraph <- listOfN(words, nonEmptyStrings(minLength = 1))
      } yield paragraph.mkString(" ")
    ) map Refined.unsafeApply
  }

  def prefixParagraph(prefix: String, minWords: Int = 2, maxWords: Int = 10): Gen[String] = {
    require(minWords <= maxWords)
    for {
      words     <- Gen.choose(minWords, maxWords)
      paragraph <- listOfN(words, nonEmptyStrings(minLength = 1))
    } yield prefix + paragraph.mkString(" ")
  }

  def emails: Gen[String Refined NonEmpty] = {
    for {
      name <- nonEmptyStrings()
      host <- nonEmptyStrings()
    } yield s"$name@$host"
  } map Refined.unsafeApply

  def stringsOfLength(length: Int Refined Positive = 10, charsGenerator: Gen[Char] = alphaChar): Gen[String] =
    listOfN(length, charsGenerator).map(_.mkString(""))

  def blankStrings(maxLength: Int Refined NonNegative = 10): Gen[String] =
    for {
      length <- choose(0, maxLength.value)
      chars  <- listOfN(length, const(" "))
    } yield chars.mkString("")

  def nonEmptyStringsList(minElements: Int Refined Positive = 1,
                          maxElements: Int Refined Positive = 5
  ): Gen[List[String]] =
    for {
      size  <- choose(minElements.value, maxElements.value)
      lines <- Gen.listOfN(size, nonEmptyStrings())
    } yield lines

  def listOf[T](generator: Gen[T], min: Int = 0, max: Int = 5): Gen[List[T]] =
    for {
      size <- choose(min, max)
      list <- Gen.listOfN(size, generator)
    } yield list

  def setOf[T](generator: Gen[T], maxElements: Int Refined Positive = 5): Gen[Set[T]] =
    for {
      size <- choose(0, maxElements.value)
      set  <- Gen.containerOfN[Set, T](size, generator)
    } yield set

  def positiveInts(max: Int = 1000): Gen[Int Refined Positive] =
    choose(1, max) map Refined.unsafeApply

  def positiveLongs(max: Long = 1000): Gen[Long Refined Positive] =
    choose(1L, max) map Refined.unsafeApply

  def nonNegativeInts(max: Int = 1000): Gen[Int] = choose(0, max)

  def negativeInts(min: Int = -1000): Gen[Int] = choose(min, 0)

  def durations(max: FiniteDuration = 5 seconds): Gen[FiniteDuration] =
    choose(1, max.toMillis)
      .map(FiniteDuration(_, MILLISECONDS))

  def relativePaths(minSegments: Int = 1, maxSegments: Int = 10): Gen[String] = {
    require(minSegments <= maxSegments,
            s"Generate relative paths with minSegments=$minSegments and maxSegments=$maxSegments makes no sense"
    )

    for {
      partsNumber <- Gen.choose(minSegments, maxSegments)
      partsGenerator = nonEmptyStrings(
                         charsGenerator = frequency(9 -> alphaChar, 1 -> oneOf('-', '_'))
                       )
      parts <- Gen.listOfN(partsNumber, partsGenerator)
    } yield parts.mkString("/")
  }

  val httpPorts: Gen[Int Refined Positive] = choose(1000, 10000) map Refined.unsafeApply

  val httpUrls: Gen[String] = for {
    protocol <- Arbitrary.arbBool.arbitrary map {
                  case true  => "http"
                  case false => "https"
                }
    port <- httpPorts
    host <- nonEmptyStrings()
  } yield s"$protocol://$host:$port"

  val validatedUrls: Gen[String Refined Url] = httpUrls map Refined.unsafeApply

  val shas: Gen[String] = for {
    length <- Gen.choose(40, 40)
    chars  <- Gen.listOfN(length, Gen.oneOf((0 to 9).map(_.toString) ++ ('a' to 'f').map(_.toString)))
  } yield chars.mkString("")

  val timestamps: Gen[Instant] =
    Gen
      .choose(Instant.EPOCH.toEpochMilli, Instant.now().plus(2000, DAYS).toEpochMilli)
      .map(Instant.ofEpochMilli)

  val timestampsNotInTheFuture: Gen[Instant] =
    Gen
      .choose(Instant.EPOCH.toEpochMilli, Instant.now().toEpochMilli)
      .map(Instant.ofEpochMilli)

  val timestampsInTheFuture: Gen[Instant] =
    Gen
      .choose(Instant.now().plus(10, MINS).toEpochMilli, Instant.now().plus(2000, DAYS).toEpochMilli)
      .map(Instant.ofEpochMilli)

  val zonedDateTimes: Gen[ZonedDateTime] =
    timestamps
      .map(ZonedDateTime.ofInstant(_, ZoneId.systemDefault))

  val localDates: Gen[LocalDate] =
    timestamps
      .map(LocalDateTime.ofInstant(_, ZoneOffset.UTC))
      .map(_.toLocalDate)

  val localDatesNotInTheFuture: Gen[LocalDate] =
    timestampsNotInTheFuture
      .map(LocalDateTime.ofInstant(_, ZoneOffset.UTC))
      .map(_.toLocalDate)

  object Implicits {

    implicit class GenOps[T](generator: Gen[T]) {

      def generateOne: T = generateExample(generator)

      def generateList(min: Int = 0, max: Int = 5): List[T] =
        generateExample(listOf(generator, min, max))

      def generateDifferentThan(value: T): T = {
        val generated = generator.sample.getOrElse(generateDifferentThan(value))
        if (generated == value) generateDifferentThan(value)
        else generated
      }

      protected def generateExample[O](generator: Gen[O]): O = {
        @annotation.tailrec
        def loop(tries: Int): O =
          generator.sample match {
            case Some(o)               => o
            case None if tries >= 5000 => sys.error(s"Failed to generate example value after $tries tries")
            case None                  => loop(tries + 1)
          }

        loop(0)
      }
    }

    implicit def asArbitrary[T](implicit generator: Gen[T]): Arbitrary[T] = Arbitrary(generator)
  }
}
