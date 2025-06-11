# 使用Maven构建镜像
FROM maven:3.9-eclipse-temurin-17 AS build

# 设置工作目录
WORKDIR /build

# 复制pom.xml
COPY pom.xml .

# 下载依赖（利用Docker缓存）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src/ ./src/

# 编译并打包
RUN mvn clean package -DskipTests

# 运行镜像
FROM eclipse-temurin:17.0.8.1_1-jre

# Set working directory
WORKDIR /app

# Copy JAR file
COPY --from=build /build/target/*.jar app.jar

# Define environment variables
ENV SERVER_PORT=8082 \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m"

# Expose port
EXPOSE 8082

# Startup command
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 