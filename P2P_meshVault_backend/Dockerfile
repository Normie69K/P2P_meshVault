FROM gradle:8-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# Compile the application into a single executable JAR
# (If your project uses shadowJar instead of buildFatJar, change this command)
RUN gradle buildFatJar --no-daemon

# --- Stage 2: Create the lightweight runtime image ---
FROM eclipse-temurin:21-jre-alpine

# Expose the 8080 port defined in your application.yaml
EXPOSE 8080

# Create the working directory
WORKDIR /app

# Copy the compiled JAR from the build stage
COPY --from=build /home/gradle/src/build/libs/*-all.jar /app/meshvault-backend.jar

# Run the Ktor server
ENTRYPOINT ["java", "-jar", "/app/meshvault-backend.jar"]