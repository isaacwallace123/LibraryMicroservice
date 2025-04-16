FROM gradle:8.12.1-jdk17-corretto AS builder
WORKDIR /usr/src/app

COPY build.gradle settings.gradle ./
RUN gradle --refresh-dependencies dependencies --no-daemon --info --stacktrace

COPY src ./src
RUN gradle bootJar --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /usr/src/app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]