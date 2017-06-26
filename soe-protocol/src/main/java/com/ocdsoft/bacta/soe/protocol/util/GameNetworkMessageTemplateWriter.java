package com.ocdsoft.bacta.soe.protocol.util;

import com.ocdsoft.bacta.soe.protocol.SharedNetworkConfiguration;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.ByteBuffer;

/**
 * Created by kburkhardt on 1/31/15.
 */
public final class GameNetworkMessageTemplateWriter implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameNetworkMessageTemplateWriter.class);

    private final VelocityEngine ve;

    private final String controllerClassPath;
    private final String controllerFilePath;

    private final String messageFilePath;
    private final String messageClassPath;

    private final String objControllerClassPath;
    private final String objControllerFilePath;

    private final String objMessageFilePath;
    private final String objMessageClassPath;

    private final String commandControllerClassPath;
    private final String commandControllerFilePath;

    private final String commandMessageFilePath;
    private final String commandMessageClassPath;

    private final String tangibleClassPath;

    @Inject
    public GameNetworkMessageTemplateWriter(final SharedNetworkConfiguration configuration, final TemplateWriterConfig templateWriterConfig) {

        ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, LOGGER);
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true");
        ve.init();

        controllerClassPath = templateWriterConfig.getBasePackage() + ".controller";
        String fs = System.getProperty("file.separator");

        controllerFilePath = System.getProperty("user.dir") + fs  + "src"
                + fs + "main" + fs + "java" + fs +
                templateWriterConfig.getBasePackage().replace(".", fs) + fs +
                "controller" + fs;
        
        messageClassPath = templateWriterConfig.getBasePackage() + "." + ".com.ocdsoft.bacta.swg.login.message";
        messageFilePath = System.getProperty("user.dir") + fs  + "src"
                + fs + "main" + fs + "java" + fs +
                templateWriterConfig.getBasePackage().replace(".", fs) + fs +
                "com.ocdsoft.bacta.swg.login.message" + fs;

        tangibleClassPath = templateWriterConfig.getBasePackage() + ".object.tangible.TangibleObject";

        objControllerClassPath = controllerClassPath  + ".object";
        objControllerFilePath = controllerFilePath + fs + "object" + fs;

        objMessageClassPath = messageClassPath + ".object";
        objMessageFilePath = messageFilePath + fs + "object" + fs;

        commandControllerClassPath = objControllerClassPath + ".command";
        commandControllerFilePath = objControllerFilePath + fs + "command" + fs;

        commandMessageClassPath = objMessageClassPath + ".command";
        commandMessageFilePath = objMessageFilePath + fs + "command" + fs;
    }

    public void createGameNetworkMessageFiles(short priority, int opcode, ByteBuffer buffer) {

        String messageName = ClientString.get(opcode);
        
        if (messageName.isEmpty() || messageName.equalsIgnoreCase("unknown")) {
            LOGGER.error("Unknown com.ocdsoft.bacta.swg.login.message opcode: 0x" + Integer.toHexString(opcode));
            return;
        }

        writeGameNetworkMessage(opcode, priority, messageName, buffer);
        writeGameNetworkController(messageName);
    }

    private void writeGameNetworkMessage(int opcode, short priority, String messageName, ByteBuffer buffer)  {

        String outFileName = messageFilePath + messageName + ".java";
        File file = new File(outFileName);
        if(file.exists()) {
            LOGGER.debug("'{}' already exists", messageName);
            return;
        }

        Template template = ve.getTemplate("/template/GameNetworkMessage.vm");

        VelocityContext context = new VelocityContext();

        context.put("packageName", messageClassPath);
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
        writeTemplate(outFileName, context, template);
    }
    
    private void writeGameNetworkController(String messageName) {

        String className = messageName + "Controller";
        
        String outFileName = controllerFilePath + className + ".java";
        File file = new File(outFileName);
        if(file.exists()) {
            LOGGER.debug("'" + className + "' already exists");
            return;
        }

        Template template = ve.getTemplate("/template/GameNetworkController.vm");

        VelocityContext context = new VelocityContext();

        context.put("packageName", controllerClassPath);
        context.put("messageClasspath", messageClassPath);
        context.put("messageName", messageName);
        context.put("messageNameClass", messageName + ".class");
        context.put("className", className);

        /* lets render a template */
        writeTemplate(outFileName, context, template);
    }

    public void createObjFiles(int opcode, ByteBuffer buffer) {

        String messageName = ObjectControllerNames.get(opcode);

        if (messageName.isEmpty() || messageName.equalsIgnoreCase("unknown")) {
            LOGGER.error("Unknown com.ocdsoft.bacta.swg.login.message opcode: 0x" + Integer.toHexString(opcode));
            return;
        }

        writeObjMessage(opcode, messageName, buffer, messageName);
        writeObjController(opcode, messageName);
    }

    private void writeObjMessage(int opcode, String messageName, ByteBuffer buffer, String objectControllerName)  {

        String outFileName = objMessageFilePath + messageName + ".java";
        File file = new File(outFileName);
        if(file.exists()) {
            LOGGER.debug("'" + messageName + "' already exists");
            return;
        }

        Template template = ve.getTemplate("/template/ObjMessage.vm");

        VelocityContext context = new VelocityContext();

        context.put("packageName", objMessageClassPath);
        context.put("objectControllerName", objectControllerName);
        context.put("messageName", messageName);
        context.put("controllerid", Integer.toHexString(opcode));

        String messageStruct = SoeMessageUtil.makeMessageStruct(buffer);
        context.put("messageStruct", messageStruct);

        /* lets render a template */
        writeTemplate(outFileName, context, template);
    }

    private void writeObjController(int opcode, String messageName) {

        String className = messageName + "ObjController";
        String outFileName = objControllerFilePath + className + ".java";
        File file = new File(outFileName);
        if(file.exists()) {
            LOGGER.debug("'" + className + "' already exists");
            return;
        }

        Template template = ve.getTemplate("/template/ObjController.vm");

        VelocityContext context = new VelocityContext();

        context.put("packageName", objControllerClassPath);
        context.put("tangibleClassPath", tangibleClassPath);
        context.put("messageClasspath", objMessageClassPath);
        context.put("messageName", messageName);
        context.put("className", className);
        context.put("controllerid", opcode);

        /* lets render a template */
        writeTemplate(outFileName, context, template);
    }

    public void createCommandFiles(int commandHash, ByteBuffer buffer) {

        String messageName = CommandNames.get(commandHash);

        if (messageName.isEmpty() || messageName.equalsIgnoreCase("unknown")) {
            LOGGER.error("Unknown com.ocdsoft.bacta.swg.login.message opcode: 0x" + Integer.toHexString(commandHash));
            return;
        }

        messageName = messageName.substring(0, 1).toUpperCase() + messageName.substring(1);

        writeCommandMessage(commandHash, messageName, buffer);
        writeCommandController(commandHash, messageName);
    }

    private void writeCommandMessage(int opcode, String messageName, ByteBuffer buffer)  {

        String outFileName = commandMessageFilePath + messageName + ".java";
        File file = new File(outFileName);
        if(file.exists()) {
            LOGGER.debug("'" + messageName + "' already exists");
            return;
        }

        Template template = ve.getTemplate("/template/CommandMessage.vm");

        VelocityContext context = new VelocityContext();

        context.put("packageName", commandMessageClassPath);
        //context.put("objectControllerName", objectControllerName);
        context.put("messageName", messageName);
        context.put("controllerid", Integer.toHexString(opcode));

        String messageStruct = SoeMessageUtil.makeMessageStruct(buffer);
        context.put("messageStruct", messageStruct);

        /* lets render a template */
        writeTemplate(outFileName, context, template);
    }

    private void writeCommandController(int opcode, String messageName) {

        String className = messageName + "QueueCommandController";

        String outFileName = commandControllerFilePath + className + ".java";
        File file = new File(outFileName);
        if(file.exists()) {
            LOGGER.debug("'" + className + "' already exists");
            return;
        }

        Template template = ve.getTemplate("/template/QueueCommandController.vm");

        VelocityContext context = new VelocityContext();

        context.put("packageName", commandControllerClassPath);
        context.put("tangibleClassPath", tangibleClassPath);
        context.put("messageClasspath", commandMessageClassPath + "." + messageName);
        context.put("messageName", messageName);
        context.put("className", className);
        context.put("controllerid", opcode);

        /* lets render a template */
        writeTemplate(outFileName, context, template);
    }

    private void writeTemplate(String outFileName, VelocityContext context, Template template) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outFileName)));

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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
