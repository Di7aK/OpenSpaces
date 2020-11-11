package com.di7ak.openspaces

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.di7ak.openspaces.utils.mapJsonTo
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
        val jsonString = """
            {
                "id": 87,
                "val1": ["kjhj"],
                "val2": {
                    "val1": ["123", "456"],
                    "val2": [{
                        "kkk":"123"
                    },
                    {
                        "kkk":"456"
                    }]
                }
            }
        """.trimIndent()
        val jsonPath = """
            {
                "id": {
                    "type": "value",
                    "paths": ["id"]
                },
                "val2": {
                    "type": "array",
                    "paths": ["val2.val2"],
                    "map": {
                        "kkk": {
                            "type": "value",
                            "paths": ["kkk"]
                        }
                    }
                }
            }
        """.trimIndent()
        val mapperData = JSONObject(jsonPath)
        val json = JSONObject(jsonString)
        val result = json.mapJsonTo(test::class.java, mapperData)
        System.out.println(result)

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.di7ak.openspaces", appContext.packageName)
    }


    data class test(
        var id: Int = 0,
        var val1: List<String> = listOf(),
        var val2: List<test2> = listOf()
    )

    data class test2(
        var val1: List<String> = listOf(),
        var kkk: String = ""
    )
}