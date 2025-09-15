package edu.escuelaing.arep.app.httpserver;

/**
 * @author juan.rpenuela
 */

import java.net.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


import edu.escuelaing.arep.app.microspringboot.annotations.GetMapping;
import edu.escuelaing.arep.app.microspringboot.annotations.RequestParam;
import edu.escuelaing.arep.app.microspringboot.annotations.RestController;

public class HttpServer {

    public static String WEB_ROOT;
    public static HashMap<String, Method> services = new HashMap<>();
    private static int clientNumber = 1;
    private static volatile boolean running = true;
    private static ServerSocket serverSocket = null;

    public static void loadServices(String[] args) {
 
        try { 
            Class<?> c = Class.forName(args[0]); 
            System.out.println("Loading services from class: " + c.getName());
            if (c.isAnnotationPresent(RestController.class)){ 
                Method[] methods = c.getDeclaredMethods(); 
                for (Method m : methods){ 
                    if(m.isAnnotationPresent(GetMapping.class)){ 
                        String mapping = m.getAnnotation(GetMapping.class).value(); 
                        services.put(mapping, m); 
                        System.out.println("Registered service: " + mapping + " -> " + m.getName());
                    }
                } 
            } else {
                System.out.println("Class " + c.getName() + " is not annotated with @RestController");
            }
        } catch (ClassNotFoundException ex) { 
            System.getLogger(HttpServer.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex); 
        } 
    }

    public static void startServer(String[] args) throws IOException, URISyntaxException {
        loadServices(args);
        staticfiles("resources/");
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        while (running) {
            try {
                System.out.println("Listo para recibir ...");
                HttpServerThread serverThread = new HttpServerThread(serverSocket.accept());
                Thread thread = new Thread(serverThread);
                thread.start();
            } catch (IOException e) {
                if (running) {
                    System.err.println("Accept failed.");
                    System.exit(1);
                }
            }
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    public static void stopServer() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing server socket: " + e.getMessage());
            }
        }
    }

    public static int getClientNumber() {
        return clientNumber++;
    }

    // methods into a creation of my own framework

    public static void staticfiles(String path) {
        WEB_ROOT = path;
    }

    public static boolean hasFileExtension(String path) {
        return path.contains(".") && path.lastIndexOf(".") > path.lastIndexOf("/");
    }

    public static void invokeService(URI requri, OutputStream out) throws IOException {
        HttpResponse response = new HttpResponse();
        
        try {
            HttpRequest req = new HttpRequest(requri);
            System.out.println("value" + requri.getPath().substring(4));
            String servicePath = requri.getPath().substring(4);
            Method m = services.get(servicePath);
            
            if (m == null) {
                response.setStatus(404, "Not Found");
                response.setContentType("text/plain");
                response.setBody("Service not found: " + servicePath);
                String httpResponse = response.toHttpString();
                out.write(httpResponse.getBytes());
                out.flush();
                return;
            }
            
            String[] argsValues = null;
            
            if (m.getParameterCount() > 0) {
                RequestParam rp = (RequestParam) m.getParameterAnnotations()[0][0];
                
                if (requri.getQuery() == null) {
                    argsValues = new String[]{rp.defaultValue()};
                } else {
                    String queryParamName = rp.value();
                    String paramValue = req.getValue(queryParamName);
                    if (paramValue == null) {
                        paramValue = rp.defaultValue();
                    }
                    argsValues = new String[]{paramValue};
                }
            }
            
            Object result;
            if (argsValues != null) {
                result = m.invoke(null, argsValues[0]); 
            } else {
                result = m.invoke(null);
            }
            
            response.setStatus(200, "OK");
            response.setContentType("text/html");
            response.setBody(result.toString());
            
            String httpResponse = response.toHttpString();
            out.write(httpResponse.getBytes());
            out.flush();
            
        } catch (IllegalAccessException ex) {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500, "Internal Server Error");
            response.setContentType("text/plain");
            response.setBody("Internal Server Error");
            String httpResponse = response.toHttpString();
            out.write(httpResponse.getBytes());
            out.flush();
        } catch (InvocationTargetException ex) {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500, "Internal Server Error");
            response.setContentType("text/plain");
            response.setBody("Internal Server Error");
            String httpResponse = response.toHttpString();
            out.write(httpResponse.getBytes());
            out.flush();
        }
    }

    public static void handleStaticFiles(URI requestURI, OutputStream out) throws IOException {
        String filePath = requestURI.getPath();
        if (filePath.equals("/")) {
            filePath = "/index.html";
        }
        HttpResponse.buildFileResponse(filePath, out);
    }


}
