package com.di7ak.openspaces

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.di7ak.openspaces.utils.mapJsonTo
import com.di7ak.openspaces.utils.test
import org.json.JSONObject

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val json = """
            {"lol":{"kek":15}}
        """.trimIndent()
        val mapper = """
            {"id": ["$.lol.kek.k", "$.lol.kek"]}
        """.trimIndent()
        val mapperObj = JSONObject(mapper)
        val result = json.mapJsonTo(test::class.java, mapperObj)
        println(result)

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.di7ak.openspaces", appContext.packageName)
    }
}