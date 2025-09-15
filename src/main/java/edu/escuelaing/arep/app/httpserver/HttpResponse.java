package edu.escuelaing.arep.app.httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpResponse {

    private int statusCode = 200;
    private String statusText = "OK";
    private String contentType = "text/plain";
    private String body = "";
    private String filePath = null;

    
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public String getBody() {
        return body;
    }
    
    public String getStatusText() {
        return statusText;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setStatus(int statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public void setFile(String filePath){
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }


    public boolean isFileResponse() {
        return filePath != null;
    }   
    
    public static void buildFileResponse(String requestedFile, OutputStream out) throws IOException {
        Path filePath = Paths.get(HttpServer.WEB_ROOT, requestedFile);
        if (Files.exists(filePath)) {
            try {
                String mimeType = getMimeType(requestedFile);
                byte[] fileContent = Files.readAllBytes(filePath);
                sendResponse(out, 200, "OK", mimeType, fileContent);
            } catch (IOException e) {
                sendResponse(out, 500, "Internal Server Error", "text/plain", "500 Internal Server Error".getBytes());
            }
        } else {
            sendResponse(out, 404, "Not Found", "text/plain", "404 File not found".getBytes());
        }
    }

    public String toHttpString() {
        System.out.println("por aqui salio");
        return sendHttpHeaders(contentType, statusCode, statusText) + body;
    }

    private static void sendResponse(OutputStream out, int statusCode, String statusText, String mimeType, byte[] content) throws IOException {
        String headers = sendHttpHeaders(mimeType, statusCode, statusText);
        out.write(headers.getBytes());
        if (content != null) {
            out.write(content);
        }
        out.flush();
    }

    private static String sendHttpHeaders(String MimeType, int statusCode, String statusText) {
        System.out.println("Sending a response...");
        String headers = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n"
                + "Content-Type: " + MimeType + "\r\n"
                + "\r\n";
        return headers;
    }

        private static String getMimeType(String filename) {
        if (filename.endsWith(".html")) {
            return "text/html";
        } else if (filename.endsWith(".txt")) {
            return "text/plain";
        } else if (filename.endsWith(".css")) {
            return "text/css";
        } else if (filename.endsWith(".js")) {
            return "application/javascript";
        } else if (filename.endsWith(".json")) {
            return "application/json";
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }
}
