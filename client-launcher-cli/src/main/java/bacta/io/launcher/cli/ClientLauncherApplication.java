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

package bacta.io.launcher.cli;

import bacta.io.session.client.SessionClient;
import bacta.io.session.client.SessionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Created by crush on 7/4/2017.
 */
@Slf4j
@SpringBootApplication
@EnableAutoConfiguration
@PropertySources({
        @PropertySource("classpath:application.properties")
})
public final class ClientLauncherApplication {
    private final ClientLauncherApplicationProperties properties;
    private final SessionClient sessionClient;
    private final PrintStream printStream;
    private final InputStream inputStream;

    @Inject
    private ClientLauncherApplication(final ClientLauncherApplicationProperties properties,
                                      final SessionClient sessionClient,
                                      final PrintStream printStream,
                                      final InputStream inputStream) {
        this.properties = properties;
        this.sessionClient = sessionClient;
        this.printStream = printStream;
        this.inputStream = inputStream;
    }

    private void launchClient() {
        //Check config. If no username + password, then prompt for it from standard input.
        if (properties.getSessionServerAddress() == null || properties.getSessionServerAddress().isEmpty())
            throw new IllegalArgumentException("sessionServerAddress must be defined in the properties file.");

        if (properties.getClientPath() == null || properties.getClientPath().isEmpty())
            throw new IllegalArgumentException("clientPath must be defined in the properties file.");

        final String username;
        final String password;

        try (final Scanner scanner = new Scanner(inputStream)) {
            username = promptPropertyIfNeeded(scanner, "Username", properties.getUsername());
            password = promptPropertyIfNeeded(scanner, "Username", properties.getPassword());
        }

        final SessionResult result = sessionClient.login(username, password);

        internalLaunchClient(result);
    }

    private String promptPropertyIfNeeded(final Scanner scanner, final String label, final String existingValue) {
        if (existingValue == null || existingValue.isEmpty()) {
            printStream.printf("%s: ", label);
            return scanner.next();
        } else {
            printStream.printf("%s: %s%n", label, existingValue);
            return existingValue;
        }
    }

    private SessionResult obtainSession(final String username, final String password) {
        printStream.print("Obtaining session...");
        final SessionResult sessionResult = sessionClient.login(username, password);
        printStream.printf("done.%n");

        return sessionResult;
    }

    private void internalLaunchClient(SessionResult result) {
        ///TODO: Implement process launch with session key logic.
    }

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(ClientLauncherApplication.class, args);
        final ClientLauncherApplication app = context.getBean(ClientLauncherApplication.class);
        app.launchClient();
    }
}
