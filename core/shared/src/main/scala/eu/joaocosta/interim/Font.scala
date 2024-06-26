package eu.joaocosta.interim

/** A description of a font.
  *
  * @param name font name
  * @param fontSize font height in pixels
  * @param charWidth the width of each character in pixels
  * @param lineHeight the line height in pixels
  */
final case class Font(name: String, fontSize: Int, charWidth: Char => Int, lineHeight: Int)

object Font:
  /** A description of a font.
    *
    * All chars are assumed to be square.
    * @param name font name
    * @param fontSize font width and height in pixels
    */
  def apply(name: String, fontSize: Int): Font = Font(name, fontSize, _ => fontSize, (fontSize * 1.3).toInt)

  /** A description of a font.
    *
    * All chars are assumed to have the same width.
    * @param name font name
    * @param fontSize font height in pixels
    * @param charWidth character width in pixels
    */
  def apply(name: String, fontSize: Int, charWidth: Int): Font =
    Font(name, fontSize, _ => charWidth, (fontSize * 1.3).toInt)

  /** The default font */
  val default = Font("default", 8)
