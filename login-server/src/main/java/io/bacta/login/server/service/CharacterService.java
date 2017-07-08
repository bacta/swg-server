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

package io.bacta.login.server.service;

import io.bacta.soe.network.connection.SoeUdpConnection;

/**
 * Created by crush on 7/2/2017.
 */
public interface CharacterService {
    void deleteCharacter(int clusterId, long networkId, int bactaId);

    void deleteAllCharacters(int bactaId);

    void createCharacter(int clusterId, int bactaId, String characterName, long characterId, int templateId, boolean jedi);

    void renameCharacter(int clusterId, long characterId, String name);

    void restoreCharacter(int clusterId, String requestor, int bactaId, String characterName, long characterId, int templateId, boolean jedi);

    void enableCharacter(int bactaId, long characterId, String requestor, boolean enabled, int clusterId);

    void toggleDisableCharacter(int clusterId, long characterId, int bactaId, boolean enabled);

    void toggleCompletedTutorial(int bactaId, boolean completed);

    void sendEnumerateCharacters(SoeUdpConnection connection, int bactaId);
}