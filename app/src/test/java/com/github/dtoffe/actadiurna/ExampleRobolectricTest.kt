package com.github.dtoffe.actadiurna

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.dtoffe.actadiurna.model.TodoParser
import com.github.dtoffe.actadiurna.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Acta Diurna", appName)
  }

  @Test
  fun `parse todo item correctly`() {
    val rawLine = "(A) 2026-08-08 Call Bob @phone +work due:2026-08-10"
    val item = TodoParser.parseLine(rawLine, 1)

    assertEquals('A', item.priority)
    assertEquals("2026-08-08", item.creationDate)
    assertEquals("2026-08-10", item.dueDate)
    assertTrue(item.contexts.contains("phone"))
    assertTrue(item.projects.contains("work"))
  }
}
