package com.objectfanatics.chrono5

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.bytebuddy.ByteBuddy
import net.bytebuddy.android.AndroidClassLoadingStrategy
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.matcher.ElementMatchers

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        helloWorld()
    }

    fun helloWorld() {
        val dynamicType = ByteBuddy()
            .subclass(Any::class.java)
            .method(ElementMatchers.named("toString"))
            .intercept(FixedValue.value("Hello World!"))
            .make()
            .load(
                this::class.java.classLoader,
                AndroidClassLoadingStrategy.Wrapping( application.getDir( "dexgen", Context.MODE_PRIVATE ) )
            )
            .loaded
        Toast.makeText(this, dynamicType.newInstance().toString(), Toast.LENGTH_SHORT).show()
    }
}