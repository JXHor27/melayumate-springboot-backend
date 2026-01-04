# --- Stage 1: The Build Stage ---
# Use a full JDK image to build the application
FROM eclipse-temurin:17-jdk-jammy as builder

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper and pom.xml to leverage Docker's layer caching
# This step is only re-run if the dependencies in pom.xml change
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Set execution permission for the Maven wrapper
RUN chmod +x ./mvnw

# Download all the dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the application, skipping the tests
RUN ./mvnw package -DskipTests


# --- Stage 2: The Final, Lean Runtime Stage ---
# Use a much smaller JRE-only image to run the application
FROM eclipse-temurin:17-jre-jammy

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the "builder" stage
# This copies ONLY the final artifact, not the source code or Maven cache
COPY --from=builder /app/target/*.jar app.jar

# Expose the port that Spring Boot will run on (Render will map this)
EXPOSE 8080

# The command to run the application when the container starts
# Spring Boot will automatically use the PORT environment variable provided by Render
ENTRYPOINT ["java", "-jar", "app.jar"]