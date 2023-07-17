package com.zrh.notes

import com.zrh.notes.annotation.Model
import com.zrh.notes.annotation.ModelHandler
import com.zrh.notes.annotation.UserModel
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun annotation(){
        val model = UserModel()
        ModelHandler.handle(model)
    }
}