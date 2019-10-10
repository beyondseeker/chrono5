package com.objectfanatics.chrono5

import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.matcher.ElementMatchers
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.reflect.KClass

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

    @Test(expected = IllegalArgumentException::class)
    fun parameterizedType0() {
        parameterizedType()
    }

    @Test(expected = IllegalArgumentException::class)
    fun parameterizedType1() {
        parameterizedType(List::class)
    }

    @Test
    fun parameterizedType2() {
        val rawTypes: Array<KClass<out Any>> =
            arrayOf(List::class, Set::class)
        val parameterizedType = parameterizedType(*rawTypes)
        assertEquals(
            parameterizedType.toString(),
            "java.util.List<java.util.Set>"
        )
    }

    @Test
    fun parameterizedType3() {
        val rawTypes: Array<KClass<out Any>> =
            arrayOf(List::class, Set::class, Class::class)
        val parameterizedType = parameterizedType(*rawTypes)
        assertEquals(
            parameterizedType.toString(),
            "java.util.List<java.util.Set<java.lang.Class>>"
        )
    }

    @Test
    fun parameterizedType4() {
        val rawTypes: Array<KClass<out Any>> =
            arrayOf(List::class, Set::class, Class::class, String::class)
        val parameterizedType = parameterizedType(*rawTypes)
        assertEquals(
            parameterizedType.toString(),
            "java.util.List<java.util.Set<java.lang.Class<java.lang.String>>>"
        )
    }

    companion object {
        /**
         * Creates a linearly nested parameterized type.
         * ex: parameterizedType(List::class, Set::class, Class::class, String::class) -> List<Set<Class<String>>>
         */
        fun parameterizedType(vararg t: KClass<out Any>): TypeDescription.Generic {
            require(t.size >= 2)

            val r = t.reversed().map { it.java }

            var parameterizedType: TypeDescription.Generic =
                TypeDescription.Generic.Builder.parameterizedType(r[1], r[0]).build()

            r.drop(2).forEach { jClass ->
                parameterizedType = TypeDescription.Generic.Builder.parameterizedType(
                    TypeDescription.Generic.Builder.rawType(jClass).build().asErasure(),
                    parameterizedType
                ).build()
            }

            return parameterizedType
        }
    }
}