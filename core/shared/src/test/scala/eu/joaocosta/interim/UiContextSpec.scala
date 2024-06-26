package eu.joaocosta.interim

class UiContextSpec extends munit.FunSuite:

  test("registerItem should not mark an item not under the cursor"):
    given uiContext: UiContext   = new UiContext()
    given inputState: InputState = InputState(0, 0, false, "")

    UiContext.registerItem(1, Rect(1, 1, 10, 10))
    assertEquals(uiContext.scratchItemState.hotItem, None)
    assertEquals(uiContext.scratchItemState.activeItem, None)
    assertEquals(uiContext.scratchItemState.selectedItem, None)

    val itemStatus = UiContext.getScratchItemStatus(1)
    assertEquals(itemStatus.hot, false)
    assertEquals(itemStatus.active, false)
    assertEquals(itemStatus.selected, false)
    assertEquals(itemStatus.clicked, false)

  test("registerItem should mark an item under the cursor as hot"):
    given uiContext: UiContext   = new UiContext()
    given inputState: InputState = InputState(5, 5, false, "")

    UiContext.registerItem(1, Rect(1, 1, 10, 10))
    assertEquals(uiContext.scratchItemState.hotItem, Some(0 -> 1))
    assertEquals(uiContext.scratchItemState.activeItem, None)
    assertEquals(uiContext.scratchItemState.selectedItem, None)

    val itemStatus = UiContext.getScratchItemStatus(1)
    assertEquals(itemStatus.hot, true)
    assertEquals(itemStatus.active, false)
    assertEquals(itemStatus.selected, false)
    assertEquals(itemStatus.clicked, false)

  test("registerItem should mark a clicked item as active and focused"):
    given uiContext: UiContext   = new UiContext()
    given inputState: InputState = InputState(5, 5, true, "")

    UiContext.registerItem(1, Rect(1, 1, 10, 10))
    assertEquals(uiContext.scratchItemState.hotItem, Some(0 -> 1))
    assertEquals(uiContext.scratchItemState.activeItem, Some(1))
    assertEquals(uiContext.scratchItemState.selectedItem, Some(1))

    val itemStatus = UiContext.getScratchItemStatus(1)
    assertEquals(itemStatus.hot, true)
    assertEquals(itemStatus.active, true)
    assertEquals(itemStatus.selected, true)
    assertEquals(itemStatus.clicked, false)

  test("registerItem should mark a clicked item as clicked once the mouse is released"):
    val uiContext: UiContext    = new UiContext()
    val inputState1: InputState = InputState(5, 5, true, "")
    UiContext.registerItem(1, Rect(1, 1, 10, 10))(using uiContext, inputState1)
    uiContext.commit()

    val inputState2: InputState = InputState(5, 5, false, "")
    UiContext.registerItem(1, Rect(1, 1, 10, 10))(using uiContext, inputState2)
    uiContext.commit()

    val itemStatus = UiContext.getItemStatus(1)(using uiContext, inputState2)
    assertEquals(itemStatus.hot, true)
    assertEquals(itemStatus.active, true)
    assertEquals(itemStatus.selected, true)
    assertEquals(itemStatus.clicked, true)

  test("registerItem should not override an active item with another one"):
    val uiContext   = new UiContext()
    val inputState1 = InputState(5, 5, true, "")
    UiContext.registerItem(1, Rect(1, 1, 10, 10))(using uiContext, inputState1)
    uiContext.commit()

    val inputState2 = InputState(20, 20, true, "")
    UiContext.registerItem(1, Rect(1, 1, 10, 10))(using uiContext, inputState2)
    UiContext.registerItem(2, Rect(15, 15, 10, 10))(using uiContext, inputState2)
    assertEquals(uiContext.scratchItemState.hotItem, Some(0 -> 2))
    assertEquals(uiContext.scratchItemState.activeItem, Some(1))
    assertEquals(uiContext.scratchItemState.selectedItem, Some(1))
    uiContext.commit()

    val itemStatus = UiContext.getItemStatus(2)(using uiContext, inputState2)
    assertEquals(itemStatus.hot, true)
    assertEquals(itemStatus.active, false)
    assertEquals(itemStatus.selected, false)
    assertEquals(itemStatus.clicked, false)

  test("fork should create a new UiContext with no ops, and merge them back with ++="):
    val uiContext: UiContext = new UiContext()
    api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(0, 0, 0))(using uiContext)
    assertEquals(uiContext.getOrderedOps(), List(RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(0, 0, 0))))
    val forked = uiContext.fork()
    assertEquals(forked.getOrderedOps(), Nil)
    api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(1, 2, 3))(using forked)
    assertEquals(uiContext.getOrderedOps(), List(RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(0, 0, 0))))
    assertEquals(forked.getOrderedOps(), List(RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(1, 2, 3))))
    uiContext ++= forked
    assertEquals(
      uiContext.getOrderedOps(),
      List(
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(0, 0, 0)),
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(1, 2, 3))
      )
    )

  test("operations with a higher z-index should be returned last"):
    given uiContext: UiContext = new UiContext()
    UiContext.withZIndex(1):
      api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(3, 3, 3))
      api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(4, 4, 4))
    UiContext.withZIndex(-1):
      api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(0, 0, 0))
      api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(1, 1, 1))
    api.Primitives.rectangle(Rect(0, 0, 1, 1), Color(2, 2, 2))

    assertEquals(
      uiContext.getOrderedOps(),
      List(
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(0, 0, 0)),
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(1, 1, 1)),
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(2, 2, 2)),
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(3, 3, 3)),
        RenderOp.DrawRect(Rect(0, 0, 1, 1), Color(4, 4, 4))
      )
    )

  test("pushInputState should update the historical state"):
    val uiContext: UiContext = new UiContext()
    val inputState1          = uiContext.pushInputState(InputState(5, 5, false, ""))
    assertEquals(inputState1.deltaX, 0)
    assertEquals(inputState1.deltaY, 0)
    val inputState2 = uiContext.pushInputState(InputState(6, 7, false, ""))
    assertEquals(inputState2.deltaX, 1)
    assertEquals(inputState2.deltaY, 2)
    val inputState3 = uiContext.pushInputState(InputState(false, ""))
    assertEquals(inputState3.deltaX, 0)
    assertEquals(inputState3.deltaY, 0)
