package com.ocdsoft.bacta.swg.login.service;

import com.google.inject.Inject;

import java.io.IOException;

/**
 * @author Joe Prasanna Kumar
 * This program simulates an SSL Server listening on a specific port for client requests
 *
 * Algorithm:
 * 1. Regsiter the JSSE provider
 * 2. Set System property for keystore by specifying the keystore which contains the server certificate
 * 3. Set System property for the password of the keystore which contains the server certificate
 * 4. Create an instance of SSLServerSocketFactory
 * 5. Create an instance of SSLServerSocket by specifying the port to which the SSL Server socket needs to bind with
 * 6. Initialize an object of SSLSocket
 * 7. Create InputStream object to read data sent by clients
 * 8. Create an OutputStream object to write data back to clients.
 *
 */


public final class SecureAuthService {

    @Inject
    public SecureAuthService() throws IOException {
        new SSLServer().start();
    }

    private class SSLServer extends Thread {

        SSLServer() throws IOException {

        }

        @Override
        public void run() {
//            try {
//                ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();
//                ServerSocket ss = ssf.createServerSocket(44450);
//
//                Socket socket;
//                while((socket = ss.accept()) != null) {
//
//                    socket.getInputStream().
//                    BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
//                    PrintWriter pw = new PrintWriter(sock.getOutputStream());
//
//                    String data = br.readLine();
//                    pw.println(data);
//                    pw.close();
//                }
//
//                socket.close();
//            } catch (IOException ioe) {
//                // Client disconnected
//            }
        }
    }
}