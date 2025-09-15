# AREP Laboratory 4 - Concurrent server

## Descripción

Este proyecto implementa un servidor web HTTP en Java con un framework IoC (Inversión de Control) similar a Spring Boot. El servidor puede servir archivos estáticos (HTML, CSS, JS, imágenes) y manejar servicios web RESTful a través de anotaciones.

## Arquitectura del Sistema

### Componentes Principales:

1. **HttpServer**: Servidor HTTP principal que maneja las conexiones
2. **HttpRequest**: Maneja las solicitudes HTTP entrantes
3. **HttpResponse**: Construye respuestas HTTP con headers dinámicos
4. **WebApplication**: Clase principal para ejecutar el servidor
5. **Framework de Anotaciones**:
   - `@RestController`: Marca clases como controladores web
   - `@GetMapping`: Define rutas HTTP GET
   - `@RequestParam`: Extrae parámetros de consulta

### Flujo de Funcionamiento:

```
Cliente HTTP Request → HttpServer → 
    ├── /app/* → invokeService() → Reflexión → Método anotado
    └── /* → handleStaticFiles() → Archivos desde /resources
```
## Implementacion de concurrencia

```java
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
```
## Instalación y Uso

### Prerrequisitos:
- Java 8 o superior
- Maven 3.6 o superior

### Compilación:
```bash
mvn clean compile
```

### Ejecución:
```bash
java -cp target/classes edu.escuelaing.arep.app.WebApplication edu.escuelaing.arep.app.microspringboot.controllers.AppController
```

### URLs Disponibles:
- `http://localhost:35000/app/` - Página principal
- `http://localhost:35000/app/greeting` - Saludo por defecto  
- `http://localhost:35000/app/greeting?name=Juan` - Saludo personalizado
- `http://localhost:35000/index.html` - Archivo estático
- `http://localhost:35000/app.js` - JavaScript
- `http://localhost:35000/ballena.jpg` - Imagen

## Evidencia de Pruebas

![test](./resources/img/Test.png)

### Ejecución pruebas:
```bash
mvn test
```

## Extensibilidad

Para agregar nuevos servicios, simplemente crear métodos en AppController:

```java
@GetMapping("/nuevo")
public static String nuevoServicio(@RequestParam(value = "param", defaultValue = "default") String param) {
    return "Nuevo servicio: " + param;
}
```

## Autor
Juan Andres Rodriguez Penuela 


