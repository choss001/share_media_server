# Stage 1: Build the JAR
FROM gradle:7.6.0-jdk17 AS builder

# Set the working directory in the container
WORKDIR /app

# Copy Gradle and application files to the container
COPY . .

# Build the application with the Gradle wrapper
RUN apt-get update --fix-missing && \
    apt-get clean && \
    apt-get upgrade -y && \
    ./gradlew bootJar
#RUN ./gradlew bootJar

# Stage 2: Run the application
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy only the generated JAR file from the previous stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Set the command to run the JAR file
CMD ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-Dspring.profiles.active=prod", "app.jar"]

