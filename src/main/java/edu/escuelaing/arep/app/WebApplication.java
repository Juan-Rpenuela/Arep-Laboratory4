package edu.escuelaing.arep.app;

import java.io.IOException;
import java.net.URISyntaxException;

import edu.escuelaing.arep.app.httpserver.HttpServer;

public class WebApplication {

    public static void main(String[] args) throws IOException, URISyntaxException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown signal received. Stopping server...");
            HttpServer.stopServer();
        }));
        HttpServer.staticfiles("./resources");
        HttpServer.startServer(args);
    }

}

