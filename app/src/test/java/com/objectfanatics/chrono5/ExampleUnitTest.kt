package com.objectfanatics.chrono5

import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.matcher.ElementMatchers
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    // @See https://github.com/raphw/byte-buddy#hello-world
    @Test
    fun helloWorld() {
        val dynamicType = ByteBuddy()
            .subclass(Any::class.java)
            .method(ElementMatchers.named("toString"))
            .intercept(FixedValue.value("Hello World!"))
            .make()
            .load(javaClass.classLoader)
            .loaded
        assertEquals("Hello World!", dynamicType.newInstance().toString())
    }
}