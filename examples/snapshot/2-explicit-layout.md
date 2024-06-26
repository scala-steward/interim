# 2. Explicit Layout

Welcome to the InterIm tutorial!

## Running the examples

You can run the code in this file (and other tutorials) with:

```bash
scala-cli 2-explicit-layout.md example-minart-backend.scala
```

Other examples can be run in a similar fashion

## Component layout in InterIm applications

In InterIm, every component receives a `Rect` with the area where it can be rendered, so its size must be known up front.

This is very different from other systems like HTML, where elements can infer their size from their contents.

This is a typical problem of immediate mode GUIs. While there are some techniques to address that, InterIm
currently opts for the simpler option.

However, explicit does not mean manual! InterIm comes with multiple helpers to automatically generate areas according
to a specified layout:
- `grid`: a grid layout with n*m equally sized cells
- `rows`: a row layout with n equally sized rows
- `columns`: a column layout with n equally sized columns
- `dynamicRows`: a row layout with rows of different sizes
- `dynamicColumns`: a column layout with columns of different sizes

## Using layouts in the counter application

Previously, in out counter application, we had to manually set the areas for all components.
This can be quite a chore, especially since changing one area might force us to manually change them all!

Everything was layed out in 3 equally sized columns, so let's use the `columns` layout.

This layout returns a `IndexedSeq[Rect]`, with the 3 areas we want to use.

Our application now looks like:

```scala
import eu.joaocosta.interim.*

val uiContext = new UiContext()
var counter = 0

def application(inputState: InputState) =
  import eu.joaocosta.interim.InterIm.*
  ui(inputState, uiContext):
    columns(area = Rect(x = 10, y = 10, w = 110, h = 30), numColumns = 3, padding = 10): column ?=>
      button(id = "minus", label = "-")(column(0)):
        counter = counter - 1
      text(
        area = column(1),
        color = Color(0, 0, 0),
        message = counter.toString,
        font = Font.default,
        horizontalAlignment = centerHorizontally,
        verticalAlignment = centerVertically
      )
      button(id = "plus", label = "+")(column(2)):
        counter = counter + 1
```

Now let's run it:

```scala
MinartBackend.run(application)
```

## Note about dynamic layouts

While `rows`, `columns` and `cells` provide a `IndexedSeq[Rect]` (or a `IndexedSeq[IndexedSeq[Rect]]`) to our body, the dynamic
versions work a bit differently.

In those cases, a `Int => Rect` function is provided where, given a desired size, a `Rect` is returned.
If the size is positive, the `Rect` will be allocated from the top/left, while negative sizes will allocate a `Rect`
from the bottom/right.

For example, this is how our application would look like with a dynamic layout:

```scala
def dynamicApp(inputState: InputState) =
  import eu.joaocosta.interim.InterIm.*
  ui(inputState, uiContext):
    dynamicColumns(area = Rect(x = 10, y = 10, w = 110, h = 30), padding = 10): column ?=>
      button(id = "minus", label = "-")(area = column(30)): // 30px from the left
        counter = counter - 1
      button(id = "plus", label = "+")(area = column(-30)): // 30px from the right
        counter = counter + 1
      text(
        area = column(maxSize), // Fill the remaining area
        color = Color(0, 0, 0),
        message = counter.toString,
        font = Font.default,
        horizontalAlignment = centerHorizontally,
        verticalAlignment = centerVertically
      )
```
