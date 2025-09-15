package edu.escuelaing.arep.app;

import java.io.IOException;
import java.net.URISyntaxException;

import edu.escuelaing.arep.app.httpserver.HttpServer;

public class WebApplication {

    public static void main(String[] args) throws IOException, URISyntaxException {
            HttpServer.staticfiles("./resources");
            HttpServer.startServer(args);

    }

}

