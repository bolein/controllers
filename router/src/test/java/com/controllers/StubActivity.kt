package com.controllers

import android.os.Bundle

class StubActivity : ControllerActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout_fake)
    setControllerContainer(R.id.container)
  }

  fun show(c : Controller<*>) {
    make(FragmentTransitions.Show(R.id.container, this, 0, 0, c))
  }
}
