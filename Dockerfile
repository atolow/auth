# Build stage
FROM amazoncorretto:21 AS builder
WORKDIR /build
COPY . .
RUN ./gradlew build -x test

# Run stage
FROM amazoncorretto:21-alpine

WORKDIR /app
COPY --from=builder /build/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]