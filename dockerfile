FROM openjdk:17

# Set working directory
WORKDIR /usrapp/bin

ENV PORT 6000

# Copy compiled classes and dependencies
COPY /target/classes /usrapp/bin/classes
COPY /target/dependency /usrapp/bin/dependency

# Copy static resources
COPY resources /usrapp/bin/resources

# Run the application with the correct main class and arguments
CMD ["java", "-cp", "./classes:./dependency/*", "edu.escuelaing.arep.app.WebApplication", "edu.escuelaing.arep.app.microspringboot.controllers.AppController"]