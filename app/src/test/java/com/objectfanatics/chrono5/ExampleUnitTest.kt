package com.objectfanatics.chrono5

import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
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

    interface TestInterface1 {
        fun testFunction1(arg1: String)
    }

    // Create the same class as TestInterface1 expect for the name.
    @Test
    fun copyTestInterface1ExpectForName() {
        val dynamicType = ByteBuddy()
            .rebase(TestInterface1::class.java)
            .name("${TestInterface1::class.java.`package`!!.name}.${TestInterface1::class.java.simpleName}_")
            .make()
            .load(javaClass.classLoader)
            .loaded
        with(dynamicType) {
            println("name = $name")
            methods.forEach { println(it) }
        }
    }

    // Create new interface.
    @Test
    fun newInterface() {
        val dynamicType = ByteBuddy()
            .makeInterface()
            .name("${TestInterface1::class.java.`package`!!.name}.NewInterface")
            // A method from TestInterface1
            .define(TestInterface1::class.java.methods[0]).withoutCode()
            // New method
            .defineMethod("newMethod", String::class.java, Visibility.PUBLIC).withoutCode()
            .make()
            .load(javaClass.classLoader)
            .loaded
        with(dynamicType) {
            println("name = $name")
            methods.forEach { println(it) }
        }
    }
}