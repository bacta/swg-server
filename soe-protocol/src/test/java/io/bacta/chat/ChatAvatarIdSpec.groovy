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

package io.bacta.chat

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by crush on 1/7/2015.
 */
class ChatAvatarIdSpec extends Specification {
    @Unroll
    def "shouldConstructChatAvatarId"() {
        /*when:
        final ChatAvatarId avatarId = new ChatAvatarId(originalString)
        def gameCode = avatarId.getGameCode()
        def cluster = avatarId.getCluster()
        def name = avatarId.getName()

        then:
        noExceptionThrown()
        gameCode == expectedGameCode
        cluster == expectedCluster
        name == expectedName

        where:
        originalString        | expectedGameCode | expectedCluster | expectedName
        "com.swg.bacta.crush" | "com.swg"        | "bacta"         | "crush"
        "swg.bacta.crush"     | "swg"            | "bacta"         | "crush"
        "bacta.crush"         | ""               | "bacta"         | "crush"
        "crush"               | ""               | ""              | "crush"
        ""                    | ""               | ""              | ""*/
    }
}
