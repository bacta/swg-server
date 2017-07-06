/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.utils

import io.bacta.buffer.BufferUtil
import spock.lang.Specification

import java.nio.ByteBuffer

/**
 * Created by kyle on 5/29/2016.
 */
class BufferUtilTest extends Specification {
    def "CombineBuffers"() {


        when:
        initialBuffer = BufferUtil.combineBuffers(initialBuffer, secondBuffer)

        then:
        noExceptionThrown()
        initialBuffer.position() == expectedPosition

        where:
        initialBuffer                             |   secondBuffer             |   expectedPosition
        ByteBuffer.allocate(100).position(100)    |   ByteBuffer.allocate(100) |   200
        ByteBuffer.allocate(100).position(100)   |   ByteBuffer.allocate(200) |   300
        ByteBuffer.allocate(50).position(50)    |   ByteBuffer.allocate(500) |   550
    }
}
