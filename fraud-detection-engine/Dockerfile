# Use Maven to build the image
FROM maven:3.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /build

# Copy pom.xml
COPY pom.xml .

# Download dependencies (leverage Docker cache)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src/ ./src/

# Compile and package
RUN mvn clean package -DskipTests

# Runtime image
FROM eclipse-temurin:17.0.8.1_1-jre


# Set working directory
WORKDIR /app

# Copy JAR file
COPY --from=build /build/target/*.jar app.jar

# Define environment variables with ByteBuddy experimental flag and Java module opens for Hibernate
ENV SERVER_PORT=8080 \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m -Dnet.bytebuddy.experimental=true --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED"

# Expose port
EXPOSE 8080

# Startup command
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 