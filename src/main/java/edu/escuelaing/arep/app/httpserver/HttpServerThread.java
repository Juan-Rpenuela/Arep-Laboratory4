package edu.escuelaing.arep.app.httpserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;

public class HttpServerThread implements Runnable {
    private Socket socket;

    public HttpServerThread (Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try{
        int clientNumber = HttpServer.getClientNumber();
        System.out.println("Client " + clientNumber + " at " + socket.getInetAddress() + " has connected.");
        
        OutputStream out = socket.getOutputStream();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));
        String inputLine;
        boolean isFirstLine = true;
            URI requestURI = null;
            while ((inputLine = in.readLine()) != null) {
                if (isFirstLine) {
                    requestURI = new URI(inputLine.split(" ")[1]);
                    System.out.println("Path: " + requestURI.getPath());
                    isFirstLine = false;
                }
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
            
            String requestPath = requestURI.getPath();
            if (requestPath.startsWith("/app") && !HttpServer.hasFileExtension(requestPath)) {
                HttpServer.invokeService(requestURI, out);
            } else {
                HttpServer.handleStaticFiles(requestURI, out);
            }
            
            in.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

