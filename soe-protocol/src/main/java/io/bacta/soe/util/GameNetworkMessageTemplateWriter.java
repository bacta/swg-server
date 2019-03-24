package io.bacta.soe.util;

import io.bacta.game.GameControllerMessageType;
import io.bacta.game.message.object.CommandQueueEnqueue;

import java.nio.ByteBuffer;
import java.nio.file.Path;

public interface GameNetworkMessageTemplateWriter {
    void setControllerInfo(Path filePath, String packageName);

    void setMessagePackage(Path filePath, String packageName);

    void setObjectControllerInfo(Path filePath, String packageName);

    void setObjMessagePackage(Path filePath, String packageName);

    void setCommandControllerInfo(Path filePath, String packageName);

    void createGameNetworkMessageFiles(short priority, int opcode, ByteBuffer buffer);

    void createObjFiles(GameControllerMessageType type, ByteBuffer buffer);

    void createCommandFiles(CommandQueueEnqueue data, ByteBuffer buffer);
}
