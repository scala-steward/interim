package eu.joaocosta.interim.api

import eu.joaocosta.interim._

/** Object containing the default primitives.
  *
  * By convention, all components are functions in the form `def primitive(area, color, params...): Unit`.
  *
  * The area parameter can be either a `Rect` or a `LayoutAllocator`.
  */
object Primitives extends Primitives

trait Primitives:

  /** Draws a rectangle filling the specified area with a color.
    */
  final def rectangle(area: Rect | LayoutAllocator.CellAllocator, color: Color)(using uiContext: UiContext): Unit =
    val reservedArea = area match {
      case rect: Rect                           => rect
      case alloc: LayoutAllocator.CellAllocator => alloc.nextCell()
    }
    uiContext.pushRenderOp(RenderOp.DrawRect(reservedArea, color))

  /** Draws the outline a rectangle inside the specified area with a color.
    */
  final def rectangleOutline(area: Rect | LayoutAllocator.CellAllocator, color: Color, strokeSize: Int)(using
      uiContext: UiContext
  ): Unit =
    val reservedArea = area match {
      case rect: Rect                           => rect
      case alloc: LayoutAllocator.CellAllocator => alloc.nextCell()
    }
    val top    = reservedArea.copy(h = strokeSize)
    val bottom = top.move(dx = 0, dy = reservedArea.h - strokeSize)
    val left   = reservedArea.copy(w = strokeSize)
    val right  = left.move(dx = reservedArea.w - strokeSize, dy = 0)
    rectangle(top, color)
    rectangle(bottom, color)
    rectangle(left, color)
    rectangle(right, color)

  /** Draws a block of text in the specified area with a color.
    *
    * @param text text to write
    * @param font font definition
    * @param horizontalAlignment how the text should be aligned horizontally
    * @param verticalAlignment how the text should be aligned vertically
    */
  final def text(
      area: Rect | LayoutAllocator.AreaAllocator,
      color: Color,
      message: String,
      font: Font = Font.default,
      horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Left,
      verticalAlignment: VerticalAlignment = VerticalAlignment.Top
  )(using
      uiContext: UiContext
  ): Unit =
    if (message.nonEmpty)
      val reservedArea = area match {
        case rect: Rect                           => rect
        case alloc: LayoutAllocator.AreaAllocator => alloc.allocate(message, font)
      }
      uiContext.pushRenderOp(
        RenderOp.DrawText(reservedArea, color, message, font, reservedArea, horizontalAlignment, verticalAlignment)
      )

  /** Advanced operation to add a custom primitive to the list of render operations.
    *
    * Supports an arbitrary data value. It's up to the backend to interpret it as it sees fit.
    * If the backend does not know how to interpret it, it can just render a colored rect.
    *
    * @param data custom value to be interpreted by the backend.
    */
  final def custom[T](area: Rect, color: Color, data: T)(using uiContext: UiContext): Unit =
    uiContext.pushRenderOp(RenderOp.Custom(area, color, data))

  /** Applies the operations in a code block at the next z-index. */
  def onTop[T](body: (UiContext) ?=> T)(using uiContext: UiContext): T =
    UiContext.withZIndex(uiContext.currentZ + 1)(body)

  /** Applies the operations in a code block at the previous z-index. */
  def onBottom[T](body: (UiContext) ?=> T)(using uiContext: UiContext): T =
    UiContext.withZIndex(uiContext.currentZ - 1)(body)
