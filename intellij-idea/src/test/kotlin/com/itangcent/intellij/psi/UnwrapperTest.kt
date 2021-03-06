package com.itangcent.intellij.psi

import com.itangcent.mock.AdvancedContextTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Test case of [Unwrapper]
 */
internal class UnwrapperTest : AdvancedContextTest() {

    @Test
    fun testUnwrapped() {
        assertNull(Unwrapper().unwrapped(null))
        assertEquals(1, Unwrapper().unwrapped(1))
        assertEquals("s", Unwrapper().unwrapped("s"))
        val node = hashMapOf<String, Any?>("x" to 1, "y" to 1, "s1" to "", "s2" to "", "null" to null, "msg" to "root")
        node["sub"] = Delay(node)

        checkEqual(
            hashMapOf(
                "x" to 1, "y" to 1, "s1" to "", "s2" to "", "null" to null, "msg" to "root",
                "sub" to hashMapOf(
                    "x" to 1, "y" to 1, "s1" to "", "s2" to "", "null" to null, "msg" to "root",
                    "sub" to Any()
                )
            ), Unwrapper().unwrapped(node) as Map<*, *>
        )

        checkEqual(
            hashMapOf(
                "x" to 1, "y" to 1, "s1" to "", "s2" to "", "null" to null, "msg" to "root",
                "sub" to hashMapOf(
                    "x" to 1, "y" to 1, "s1" to "", "s2" to "", "null" to null, "msg" to "root",
                    "sub" to Any()
                )
            ), Unwrapper().unwrapped(Delay(node))
        )

        checkEqual(
            listOf(
                hashMapOf(
                    "x" to 1, "y" to 1, "s1" to "", "s2" to "", "null" to null, "msg" to "root",
                    "sub" to hashMapOf(
                        "x" to 1, "y" to 1, "s1" to "", "s2" to "", "null" to null, "msg" to "root",
                        "sub" to Any()
                    )
                )
            ), Unwrapper().unwrapped(listOf(Delay(node)))
        )
    }

    private fun checkEqual(any: Any?, any2: Any?) {
        if (any == null) {
            assertNull(any2)
            return
        }

        assertTrue(any2 != null)

        if (any::class == Any::class) {
            assertTrue(any2::class == Any::class)
            return
        }

        if (any is Map<*, *>) {
            assertTrue(any2 is Map<*, *>)
            checkMapEqual(any, any2)
            return
        }

        if (any is List<*>) {
            assertTrue(any2 is List<*>)
            checkListEqual(any, any2)
            return
        }

        if (any is Array<*>) {
            assertTrue(any2 is Array<*>)
            checkArrayEqual(any, any2)
            return
        }

        assertEquals(any, any2)
    }

    private fun checkMapEqual(map: Map<*, *>, map2: Map<*, *>) {
        assertEquals(map.keys, map2.keys)
        for (entry in map.entries) {
            checkEqual(entry.value, map2[entry.key])
        }
    }

    private fun checkListEqual(list: List<*>, list2: List<*>) {
        assertEquals(list.size, list2.size)
        for ((index, any) in list.withIndex()) {
            checkEqual(any, list2[index])
        }
    }

    private fun checkArrayEqual(arr: Array<*>, arr2: Array<*>) {
        assertEquals(arr.size, arr2.size)
        for ((index, any) in arr.withIndex()) {
            checkEqual(any, arr2[index])
        }
    }
}