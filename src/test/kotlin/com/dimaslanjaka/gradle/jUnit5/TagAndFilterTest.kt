package com.dimaslanjaka.gradle.jUnit5


import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

class TagAndFilterTest {
    /**
     * To enable all tests with the same filter, go to 'junit5.gradle' and take a look
     * at `junitPlatform -> filters -> tags`
     *
     * note: will not work with built-in IDE support yet.
     *
     * e.g. tag in our case 'fast'
     */
    @Test
    @Tag("fast")
    fun testThatDoesThingsFast() {
        assertTrue(true)
    }
}
