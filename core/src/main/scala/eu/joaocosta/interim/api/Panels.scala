package eu.joaocosta.interim.api

import eu.joaocosta.interim.ItemId._
import eu.joaocosta.interim.*
import eu.joaocosta.interim.skins.*

/** Objects containing all default panels.
  *
  * Panels are a mix of a component and a layout. They perform rendering operations, but also provide a draw area.
  *
  * By convention, all panels are of the form `def panel(id, area, params..., skin)(body): (Value, Rect)`.
  * The returned value is the value returned by the body. Panels also return a rect, which is the area
  * the panel must be called with in the next frame (e.g. for movable panels).
  *
  * As such, panels should be called like:
  *
  * ```
  *  val (value, nextRect) = panel(id, panelRect, ...) {area => ...}
  *  panelRect = nextRect
  * ```
  */
object Panels extends Panels

trait Panels:

  /**  Window with a title.
    *
    * @param title of this window
    * @param movable if true, the window will include a move handle in the title bar
    */
  final def window[T](
      id: ItemId,
      area: Rect,
      title: String,
      movable: Boolean = false,
      skin: WindowSkin = WindowSkin.Default(),
      handleSkin: HandleSkin = HandleSkin.Default()
  )(
      body: Rect => T
  ): Components.Component[(T, Rect)] =
    skin.renderWindow(area, title)
    val nextArea =
      if (movable)
        Components.moveHandle(
          id |> "internal_move_handle",
          skin.titleTextArea(area),
          handleSkin
        )(area)
      else area
    (body(skin.panelArea(area)), nextArea)