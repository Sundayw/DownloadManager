package models.download

import scala.annotation.tailrec

import org.joda.time.LocalDate

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 1/10/13
 * Time: 2:30 PM
 */

case class SimpleDate(day: Int, month: Int, year: Int) extends Quantum[SimpleDate] {

  def this(jodaDate: LocalDate) =
    this(jodaDate.getDayOfMonth, jodaDate.getMonthOfYear, jodaDate.getYear)

  def this(dateString: String)(implicit dmy: (Int, Int, Int) = SimpleDate.retrieveDMY(dateString)) =
    this(dmy._1, dmy._2, dmy._3)

  def toSimpleMonth = SimpleMonth(month, year)

  override def asDateString = "%d/%d/%d".format(this.month, this.day, this.year)
  override def asJodaDate   = new LocalDate(this.year, this.month, this.day)
  override def <=(that: SimpleDate) : Boolean = {
    (this.year < that.year) || {
      (this.year == that.year) && ((this.month < that.month) || {
        (this.month == that.month) && (this.day <= that.day)
      })
    }
  }

  override protected def cons(jodaDate: LocalDate)        = SimpleDate(jodaDate)
  override protected def iterateDate(jodaDate: LocalDate) = jodaDate.plusDays(1)

}

object SimpleDate extends QuantumCompanion[SimpleDate] {

  override def apply(jodaDate: LocalDate) = new SimpleDate(jodaDate)
  override def apply(dateString: String)  = new SimpleDate(dateString)

  override val DateRegex = """(\d{1,2})[/-](\d{1,2})[/-](\d{2}|\d{4})""".r

  private def retrieveDMY(str: String) : (Int, Int, Int) = {
    str match {
      case DateRegex(d, m, y) =>
        val day   = parseDayStr(d)
        val month = parseMonthStr(m)
        val year  = parseYearStr(y)
        (day, month, year)
    }
  }

}



case class SimpleMonth(month: Int, year: Int) extends Quantum[SimpleMonth] {

  def this(jodaDate: LocalDate) =
    this(jodaDate.getMonthOfYear, jodaDate.getYear)

  def this(dateString: String)(implicit my: (Int, Int) = SimpleMonth.retrieveMY(dateString)) =
    this(my._1, my._2)

  def toSimpleYear = SimpleYear(year)

  override def asDateString = "%d/%d".format(this.month, this.year)
  override def asJodaDate   = new LocalDate(this.year, this.month, 1)
  override def <=(that: SimpleMonth) : Boolean = {
    (this.year < that.year) || {
      (this.year == that.year) && (this.month <= that.month)
    }
  }

  override protected def cons(jodaDate: LocalDate)        = SimpleMonth(jodaDate)
  override protected def iterateDate(jodaDate: LocalDate) = jodaDate.plusMonths(1)

}

object SimpleMonth extends QuantumCompanion[SimpleMonth] {

  override def apply(jodaDate: LocalDate) = new SimpleMonth(jodaDate)
  override def apply(dateString: String)  = new SimpleMonth(dateString)

  override val DateRegex = """(\d{1,2})[/-](\d{2}|\d{4})""".r

  private def retrieveMY(str: String) : (Int, Int) = {
    str match {
      case DateRegex(m, y) =>
        val month = parseMonthStr(m)
        val year  = parseYearStr(y)
        (month, year)
    }
  }

}



case class SimpleYear(year: Int) extends Quantum[SimpleYear] {

  def this(jodaDate: LocalDate) =
    this(jodaDate.getYear)

  def this(dateString: String)(implicit y: Int = SimpleYear.retrieveY(dateString)) =
    this(y)

  override def asDateString        = "%d".format(this.year)
  override def asJodaDate          = new LocalDate(this.year)
  override def <=(that: SimpleYear) = this.year <= that.year

  override protected def cons(jodaDate: LocalDate)        = SimpleYear(jodaDate)
  override protected def iterateDate(jodaDate: LocalDate) = jodaDate.plusYears(1)

}

object SimpleYear extends QuantumCompanion[SimpleYear] {

  override def apply(jodaDate: LocalDate) = new SimpleYear(jodaDate)
  override def apply(dateString: String)  = new SimpleYear(dateString)

  override val DateRegex = """(\d{2}|\d{4})""".r

  private def retrieveY(str: String) : Int = {
    str match { case DateRegex(y) => parseYearStr(y) }
  }

}


protected trait Quantum[T <: Quantum[T]] {

  protected def cons(jodaDate: LocalDate) : T
  protected def iterateDate(jodaDate: LocalDate) : LocalDate

  def <=(that: T) : Boolean

  def asDateString : String
  def asJodaDate   : LocalDate

  def to(that: T) : Seq[T] = {

    @tailrec
    def helper(startDate: LocalDate, endDate: LocalDate, acc: Seq[T] = Seq()) : Seq[T] = {
      if (startDate isBefore endDate)
        helper(iterateDate(startDate), endDate, cons(startDate) +: acc)
      else
        (cons(startDate) +: acc).reverse
    }

    val (start, end) = if (this <= that) (this, that) else (that, this)
    helper(start.asJodaDate, end.asJodaDate)

  }

}

protected trait QuantumCompanion[T <: Quantum[T]] {

  def apply(jodaDate: LocalDate) : T
  def apply(dateString: String)  : T

  val DateRegex: util.matching.Regex

  protected def parseDayStr(str: String)   = str.toInt
  protected def parseMonthStr(str: String) = str.toInt
  protected def parseYearStr(str: String)  = (if (str.length == 2) "20" + str else str).toInt

}