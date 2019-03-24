package io.bacta.soe.util;

import io.bacta.game.GameControllerMessageType;
import io.bacta.game.message.object.CommandQueueEnqueue;

import java.nio.ByteBuffer;
import java.nio.file.Path;

public class NullGameNetworkMessageTemplateWriter implements GameNetworkMessageTemplateWriter {

    @Override
    public void setControllerInfo(Path filePath, String packageName) {
        // Null Implementation
    }

    @Override
    public void setMessagePackage(Path filePath, String packageName) {
        // Null Implementation
    }

    @Override
    public void setObjectControllerInfo(Path filePath, String packageName) {
        // Null Implementation
    }

    @Override
    public void setObjMessagePackage(Path filePath, String packageName) {
        // Null Implementation
    }

    @Override
    public void setCommandControllerInfo(Path filePath, String packageName) {
        // Null Implementation
    }

    @Override
    public void createGameNetworkMessageFiles(short priority, int opcode, ByteBuffer buffer) {
        // Null Implementation
    }

    @Override
    public void createObjFiles(GameControllerMessageType type, ByteBuffer buffer) {
        // Null Implementation
    }

    @Override
    public void createCommandFiles(CommandQueueEnqueue data, ByteBuffer buffer) {
        // Null Implementation
    }
}
