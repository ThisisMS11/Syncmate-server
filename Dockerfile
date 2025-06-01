# Use Maven to build the app (build stage)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Use a smaller base image to run the app (run stage)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/SyncMate-0.0.1-SNAPSHOT.jar app.jar

# Verify the file was copied
RUN echo "Contents of /app directory:" && ls -la /app/

# Make sure the JAR file is executable
RUN chmod +x app.jar

EXPOSE 8080

ENTRYPOINT ["java", "--add-opens=java.base/java.io=ALL-UNNAMED", "--add-opens=java.base/java.lang=ALL-UNNAMED", "--add-opens=java.base/java.util=ALL-UNNAMED", "-Dspring.profiles.active=dev", "-jar", "app.jar"]
