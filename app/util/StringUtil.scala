package util

object StringUtil {
  implicit class StringExtensions(val s: String) {
    import scala.util.control.Exception._
    import scala.util.control.Exception.Catch._
    
    def toIntOpt:Option[Int] = catching(classOf[NumberFormatException]) opt s.toInt 
    def toLongOpt:Option[Long] = catching(classOf[NumberFormatException]) opt s.toLong 
  }
}