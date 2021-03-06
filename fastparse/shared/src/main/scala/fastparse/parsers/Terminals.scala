package fastparse.parsers

import fastparse.Parser
import fastparse.Utils._
import fastparse.core.ParseCtx

import scala.annotation.tailrec

/**
 * Created by haoyi on 5/10/15.
 */
object Terminals {

  /**
   * A parser that always succeeds, consuming no input
   */
  case object Pass extends Parser[Unit]{
    def parseRec(cfg: ParseCtx, index: Int) = success(cfg.success, (), index, false)
  }

  /**
   * A parser that always fails immediately
   */
  case object Fail extends Parser[Nothing]{
    def parseRec(cfg: ParseCtx, index: Int) = fail(cfg.failure, index)
  }
  /**
   * Succeeds, consuming a single character
   */
  case object AnyChar extends Parser[Unit]{
    def parseRec(cfg: ParseCtx, index: Int) = {
      val input = cfg.input
      if (index >= input.length) fail(cfg.failure, index)
      else success(cfg.success, input(index), index+1, false)
    }
  }

  /**
   * Succeeds if at the start of the input, consuming no input
   */
  case object Start extends Parser[Unit]{
    def parseRec(cfg: ParseCtx, index: Int) = {
      if (index == 0) success(cfg.success, (), index, false)
      else fail(cfg.failure, index)
    }
  }
  /**
   * Succeeds if at the end of the input, consuming no input
   */
  case object End extends Parser[Unit]{
    def parseRec(cfg: ParseCtx, index: Int) = {
      if (index == cfg.input.length) success(cfg.success, (), index, false)
      else fail(cfg.failure, index)
    }
  }

  /**
   * Workaround https://github.com/scala-js/scala-js/issues/1603
   * by implementing startsWith myself
   */
  def startsWith(src: String, prefix: String, offset: Int) = {
    val max = prefix.length
    @tailrec def rec(i: Int): Boolean = {
      if (i >= prefix.length) true
      else if (i + offset >= src.length) false
      else if (src.charAt(i + offset) != prefix.charAt(i)) false
      else rec(i + 1)
    }
    rec(0)
  }
  /**
   * Parses a literal `String`
   */
  case class Literal(s: String) extends Parser[Unit]{
    def parseRec(cfg: ParseCtx, index: Int) = {

      if (startsWith(cfg.input, s, index)) success(cfg.success, (), index + s.length, false)
      else fail(cfg.failure, index)
    }
    override def toString = literalize(s).toString
  }

  /**
   * Parses a single character
   */
  case class CharLiteral(c: Char) extends Parser[Unit]{
    def parseRec(cfg: ParseCtx, index: Int) = {
      val input = cfg.input
      if (index >= input.length) fail(cfg.failure, index)
      else if (input(index) == c) success(cfg.success, c.toString, index + 1, false)
      else fail(cfg.failure, index)
    }
    override def toString = literalize(c.toString).toString
  }

  /**
   * Always succeeds, and provides the current index of the
   * parse into the input string. e.g. useful for providing
   * source locations for AST nodes. Consumes no input.
   */
  case object Index extends fastparse.Parser[Int]{
    def parseRec(cfg: ParseCtx, index: Int) = {
      success(cfg.success, index, index, false)
    }
  }
}
