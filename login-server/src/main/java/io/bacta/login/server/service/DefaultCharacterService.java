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

import io.bacta.login.message.EnumerateCharacterId;
import io.bacta.soe.network.connection.SoeUdpConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Services character information and requests on the LoginServer.
 */
@Slf4j
@Service
public class DefaultCharacterService implements CharacterService {
    @Override
    public void deleteCharacter(int clusterId, long networkId, int bactaId) {

    }

    @Override
    public void deleteAllCharacters(int bactaId) {

    }

    @Override
    public void createCharacter(int clusterId, int bactaId, String characterName, long characterId, int templateId, boolean jedi) {

    }

    @Override
    public void renameCharacter(int clusterId, long characterId, String name) {

    }

    @Override
    public void restoreCharacter(int clusterId, String requestor, int bactaId, String characterName, long characterId, int templateId, boolean jedi) {

    }

    @Override
    public void enableCharacter(int bactaId, long characterId, String requestor, boolean enabled, int clusterId) {

    }

    @Override
    public void toggleDisableCharacter(int clusterId, long characterId, int bactaId, boolean enabled) {

    }

    @Override
    public void toggleCompletedTutorial(int bactaId, boolean completed) {

    }

    @Override
    public void sendEnumerateCharacters(SoeUdpConnection connection, int bactaId) {
        final Set<EnumerateCharacterId.CharacterData> characterDataSet = new HashSet<>();

        //TODO: Retrieve characters and populate character data for the given account.

        final EnumerateCharacterId message = new EnumerateCharacterId(characterDataSet);
        connection.sendMessage(message);
    }

}