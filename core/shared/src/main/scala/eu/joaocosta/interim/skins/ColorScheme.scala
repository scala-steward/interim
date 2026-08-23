package eu.joaocosta.interim.skins

import eu.joaocosta.interim.Color

final case class ColorScheme(
    background: Color,         // white / black
    text: Color,               // black / white
    icon: Color,               // black / lightGray
    iconHighlight: Color,      // pureGray / white
    primary: Color,            // lightPrimary / darkPrimary
    primaryShadow: Color,      // lightPrimaryShadow / darkPrimaryShadow
    primaryHighlight: Color,   // lightPrimaryHighlight / darkPrimaryHighlight
    secondary: Color,          // lightGray / darkGray
    secondaryHighlight: Color, // pureGray / pureGray
    borderColor: Color         // pureGray / pureGray
)

/** Internal default color scheme used by InterIm's default skins
  * Inspired by:
  *   - https://github.com/raysan5/raygui/tree/master/styles/default
  *   - https://github.com/raysan5/raygui/tree/master/styles/genesis
  */
object ColorScheme:
  val white     = Color(245, 245, 245)
  val lightGray = Color(201, 201, 201)
  val pureGray  = Color(127, 127, 127)
  val darkGray  = Color(46, 53, 51)
  val black     = Color(24, 27, 30)

  val lightPrimary          = Color(151, 232, 255)
  val lightPrimaryShadow    = Color(91, 178, 217)
  val lightPrimaryHighlight = Color(201, 239, 255)

  val darkPrimary          = Color(172, 60, 60)
  val darkPrimaryShadow    = Color(95, 36, 36)
  val darkPrimaryHighlight = Color(204, 118, 118)

  val lightScheme = ColorScheme(
    background = white,
    text = black,
    icon = black,
    iconHighlight = pureGray,
    primary = lightPrimary,
    primaryShadow = lightPrimaryShadow,
    primaryHighlight = lightPrimaryHighlight,
    secondary = lightGray,
    secondaryHighlight = pureGray,
    borderColor = pureGray
  )
  val darkScheme = ColorScheme(
    background = black,
    text = white,
    icon = lightGray,
    iconHighlight = white,
    primary = darkPrimary,
    primaryShadow = darkPrimaryShadow,
    primaryHighlight = darkPrimaryHighlight,
    secondary = darkGray,
    secondaryHighlight = pureGray,
    borderColor = pureGray
  )

  private var darkMode = false

  /** Forces default skins to use the dark mode */
  def useDarkMode() = darkMode = true

  /** Forces default skins to use the light mode */
  def useLightMode() = darkMode = false

  /** Checks if dark mode is enabed for default skins */
  def darkModeEnabled() = darkMode

  /** Checks if light mode is enabed for default skins */
  def lightModeEnabled() = !darkMode
