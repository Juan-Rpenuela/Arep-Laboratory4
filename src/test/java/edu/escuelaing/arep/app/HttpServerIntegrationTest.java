package edu.escuelaing.arep.app;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

import edu.escuelaing.arep.app.httpserver.HttpServer;

/**
 * Tests de integración para el servidor HTTP
 */
public class HttpServerIntegrationTest {

    private static final String SERVER_URL = "http://localhost:35000";
    private static Thread serverThread;

    @BeforeAll
    public static void startServer() throws InterruptedException {
        // Iniciar el servidor en un hilo separado
        serverThread = new Thread(() -> {
            try {
                String[] args = {"edu.escuelaing.arep.app.microspringboot.controllers.AppController"};
                HttpServer.startServer(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
        
        // Esperar a que el servidor se inicie
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void testStaticFileHTML() throws IOException {
        String response = makeHttpRequest("/index.html");
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-Type: text/html"));
    }

    @Test
    public void testStaticFileJS() throws IOException {
        String response = makeHttpRequest("/app.js");
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-Type: application/javascript"));
    }

    @Test
    public void testStaticFileCSS() throws IOException {
        String response = makeHttpRequest("/styles.css");
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-Type: text/css"));
    }

    @Test
    public void testStaticFileImage() throws IOException {
        String response = makeHttpRequest("/ballena.jpg");
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-Type: image/jpeg"));
    }


    @Test
    public void testWebServiceGreetingDefault() throws IOException {
        String response = makeHttpRequest("/app/greeting");
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-Type: text/html"));
        assertTrue(response.contains("Hola World"));
    }

    @Test
    public void testWebServiceGreetingWithParam() throws IOException {
        String response = makeHttpRequest("/app/greeting?name=AREP");
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-Type: text/html"));
        assertTrue(response.contains("Hola AREP"));
    }

    @Test
    public void testNonExistentService() throws IOException {
        String response = makeHttpRequest("/app/noexiste");
        assertTrue(response.contains("HTTP/1.1 404 Not Found"));
        assertTrue(response.contains("Service not found"));
    }

    @Test
    public void testNonExistentFile() throws IOException {
        String response = makeHttpRequest("/noexiste.html");
        assertTrue(response.contains("HTTP/1.1 404 Not Found"));
        assertTrue(response.contains("404 File not found"));
    }
    private String makeHttpRequest(String path) throws IOException {
        URL url = new URL(SERVER_URL + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        StringBuilder response = new StringBuilder();
        
        String statusLine = connection.getHeaderField(null);
        if (statusLine != null) {
            response.append(statusLine).append("\n");
        }
        
        for (int i = 1; ; i++) {
            String headerName = connection.getHeaderFieldKey(i);
            String headerValue = connection.getHeaderField(i);
            if (headerName == null && headerValue == null) {
                break;
            }
            if (headerName != null) {
                response.append(headerName).append(": ").append(headerValue).append("\n");
            }
        }
        response.append("\n");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        } catch (IOException e) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");
                }
            }
        }

        return response.toString();
    }
}
