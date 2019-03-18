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

package io.bacta.soe.util;

import io.bacta.engine.utils.SOECRC32;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.message.object.CommandQueueEnqueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.nio.file.Path;

/**
 * Created by kburkhardt on 1/31/15.
 */
@Slf4j
public class GameNetworkMessageTemplateWriter {

    private final VelocityEngine ve;

    private String controllerPackageName;
    private Path controllerFilePath;

    private String messagePackageName;
    private Path messageFilePath;

    private String objControllerPackageName;
    private Path objControllerFilePath;

    private String objMessagePackageName;
    private Path objMessageFilePath;

    private String commandControllerPackage;
    private Path commandControllerFilePath;

    public GameNetworkMessageTemplateWriter() {

        ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, LOGGER);
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
        ve.init();
    }

    public void setControllerInfo(final Path filePath, final String packageName) {
        controllerPackageName = packageName;
        controllerFilePath = filePath;
    }

    public void setMessagePackage(final Path filePath, final String packageName) {
        messagePackageName = packageName;
        messageFilePath = filePath;
    }

    public void setObjectControllerInfo(final Path filePath, final String packageName) {
        objControllerPackageName = packageName;
        objControllerFilePath = filePath;
    }

    public void setObjMessagePackage(final Path filePath, final String packageName) {
        objMessagePackageName = packageName;
        objMessageFilePath = filePath;
    }

    public void setCommandControllerInfo(final Path filePath, final String packageName) {
        commandControllerPackage = packageName;
        commandControllerFilePath = filePath;
    }

    public void createGameNetworkMessageFiles(short priority, int opcode, ByteBuffer buffer) {

        String messageName = ClientString.get(opcode);
        
        if (messageName.isEmpty() || messageName.equalsIgnoreCase("unknown")) {
            LOGGER.error("Unknown message opcode: 0x" + Integer.toHexString(opcode));
            return;
        }

        if(messageFilePath == null || controllerFilePath == null ||
                messagePackageName == null || controllerPackageName == null) {
            LOGGER.error("Unable to create GameNetworkMessage files, paths not configured");
            return;
        }

        if(!messageName.equalsIgnoreCase("ObjControllerMessage")) {
            writeGameNetworkMessage(opcode, priority, messageName, buffer);
            writeGameNetworkController(messageName);
        }
    }

    private void writeGameNetworkMessage(int opcode, short priority, String messageName, ByteBuffer buffer)  {

        Path newMessagePath = messageFilePath.resolve(messageName + ".java");
        File file = newMessagePath.toFile();
        if(file.exists()) {
            LOGGER.debug("'{}' already exists", messageName);
            return;
        }

        Template template = ve.getTemplate("/template/GameNetworkMessage.vm");

        VelocityContext context = new VelocityContext();

        context.put("packageName", messagePackageName);
        context.put("messageName", messageName);

        if(SOECRC32.hashCode(messageName) == opcode) {
            context.put("messageSimpleName", "SOECRC32.hashCode(" + messageName + ".class.getSimpleName())");
        } else {
            context.put("messageSimpleName", "0x" + Integer.toHexString(opcode));
        }

        String messageStruct = SoeMessageUtil.makeMessageStruct(buffer);
        context.put("messageStruct", messageStruct);

        context.put("priority", "0x" + Integer.toHexString(priority));
        context.put("opcode", "0x" + Integer.toHexString(opcode));

        /* lets render a template */
        writeTemplate(file, context, template);
    }
    
    private void writeGameNetworkController(String messageName) {

        Path newMessagePath = controllerFilePath.resolve(messageName + "Controller.java");
        File file = newMessagePath.toFile();
        if(file.exists()) {
            LOGGER.debug("'" + messageName + "Controller' already exists");
            return;
        }

        Template template = ve.getTemplate("/template/GameNetworkController.vm");

        VelocityContext context = new VelocityContext();

        context.put("packageName", controllerPackageName);
        context.put("messageClasspath", messagePackageName);
        context.put("messageName", messageName);
        context.put("messageNameClass", messageName + ".class");
        context.put("className", messageName + "Controller");

        /* lets render a template */
        writeTemplate(file, context, template);
    }

    public void createObjFiles(GameControllerMessageType type, ByteBuffer buffer) {

        String typeName = type.name();
        String[] nameParts = typeName.split("_");
        StringBuilder messageNameBuilder = new StringBuilder();
        for(int i = 0; i < nameParts.length; ++i) {
            String part = nameParts[i].toLowerCase();
            part = part.substring(0, 1).toUpperCase() + part.substring(1, part.length());
            messageNameBuilder.append(part);
        }

        String messageName = messageNameBuilder.toString();

        if (messageName.isEmpty() || messageName.equalsIgnoreCase("unknown")) {
            LOGGER.error("Unknown obj message opcode: 0x" + Integer.toHexString(type.value));
            return;
        }

        if(objControllerPackageName == null || objControllerFilePath == null ||
                objMessagePackageName == null || objMessageFilePath == null) {
            LOGGER.error("Unable to create GameNetworkMessage files, paths not configured");
            return;
        }

        writeObjMessage(type, buffer, messageName);
        writeObjController(type, messageName);
    }

    private void writeObjMessage(final GameControllerMessageType type, final ByteBuffer buffer, final String messageName)  {

        Path newMessagePath = objMessageFilePath.resolve(messageName + ".java");
        File file = newMessagePath.toFile();
        if(file.exists()) {
            LOGGER.debug("'" + messageName + "' already exists");
            return;
        }

        Template template = ve.getTemplate("/template/ObjMessage.vm");

        VelocityContext context = new VelocityContext();
        String messageStruct = SoeMessageUtil.makeMessageStruct(buffer);

        context.put("packageName", objMessagePackageName);
        context.put("messageStruct", messageStruct);

        context.put("type", type.name());
        context.put("messageName", messageName);

        /* lets render a template */
        writeTemplate(file, context, template);
    }

    private void writeObjController(GameControllerMessageType type, String messageName) {

        Path newMessagePath = objControllerFilePath.resolve(messageName + "Controller.java");
        File file = newMessagePath.toFile();
        if(file.exists()) {
            LOGGER.debug("'" + messageName + "Controller' already exists");
            return;
        }

        Template template = ve.getTemplate("/template/ObjController.vm");

        VelocityContext context = new VelocityContext();

        context.put("packageName", objControllerPackageName);
        context.put("messageClasspath", objMessagePackageName);
        context.put("messageName", messageName);
        context.put("className", messageName + "Controller");
        context.put("type", type.name());

        /* lets render a template */
        writeTemplate(file, context, template);
    }

    public void createCommandFiles(CommandQueueEnqueue data, ByteBuffer buffer) {

        String messageName = CommandNames.get(data.getCommandHash());

        if (messageName.isEmpty() || messageName.equalsIgnoreCase("unknown")) {
            LOGGER.error("Unknown command name: {}", messageName);
            return;
        }

        String properName = messageName.substring(0, 1).toUpperCase() + messageName.substring(1);

        writeCommandController(messageName, properName);
    }

    private void writeCommandController(String messageName, String className) {

        Path newMessagePath = commandControllerFilePath.resolve(className + "Command.java");
        File file = newMessagePath.toFile();
        if(file.exists()) {
            LOGGER.debug("'" + className + "Command' already exists");
            return;
        }

        Template template = ve.getTemplate("/template/CommandController.vm");

        VelocityContext context = new VelocityContext();

        context.put("packageName", commandControllerPackage);
        context.put("messageName", messageName);
        context.put("className", className + "Command");

        /* lets render a template */
        writeTemplate(file, context, template);
    }

    private void writeTemplate(File outFile, VelocityContext context, Template template) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

            if (!ve.evaluate(context, writer, template.getName(), "")) {
                throw new Exception("Failed to convert the template into class.");
            }

            template.merge(context, writer);

            writer.flush();
            writer.close();
        } catch(Exception e) {
            LOGGER.error("Unable to write com.ocdsoft.bacta.swg.login.message", e);
        }
    }
}
