FROM openjdk:17-alpine as builder
WORKDIR /app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src ./src
RUN ./gradlew build --no-daemon

FROM openjdk:17-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/ontop.jar ontop.jar
COPY --from=builder /app/src/main/resources/application.properties application.properties

ENV ONTOP_ROUTING 012345678
ENV ONTOP_ACCOUNT 0123456789
ENV ONTOP_NAME ontop name

ENV TRANSACTIONS_FEE_PERCENT 0.1

ENV PAYMENT_PROVIDER_HOST http://localhost:8080

ENV WALLET_HOST http://localhost:8080

ENV SERVER_PORT 8090

ENV REDIS_HOST localhost
ENV REDIS_PORT 6379

ENV DATABASE_HOST localhost
ENV DATABASE_PORT 3306
ENV DATABASE_NAME ontop
ENV DATABASE_USERNAME username
ENV DATABASE_PASSWORD password

# Set the command to run the Spring Boot application
CMD ["java", "-jar", "ontop.jar"]
