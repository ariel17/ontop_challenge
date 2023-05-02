FROM openjdk:17-alpine as builder
WORKDIR /app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src ./src
RUN ./gradlew build --no-daemon

FROM openjdk:17-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/ontop.jar ontop.jar
COPY --from=builder /app/src/main/resources ./

# Set the command to run the Spring Boot application
CMD ["java", "-jar", "ontop.jar"]
