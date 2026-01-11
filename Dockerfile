FROM gradle:8.14-jdk21-alpine AS build

WORKDIR /app

COPY gradlew .
COPY gradle/wrapper/gradle-wrapper.jar gradle/wrapper/gradle-wrapper.properties ./gradle/wrapper/
RUN chmod +x gradlew

COPY build.gradle settings.gradle ./
COPY live-chat-server/build.gradle ./live-chat-server/
COPY gradle ./gradle

RUN gradle :live-chat-server:dependencies --no-daemon || true

COPY live-chat-server/src ./live-chat-server/src

RUN gradle :live-chat-server:bootJar --no-daemon

RUN test -f live-chat-server/build/libs/*.jar || (echo "JAR file not found" && exit 1)

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S live && adduser -S live -G live

WORKDIR /app

COPY --from=build --chown=live:live /app/live-chat-server/build/libs/*.jar app.jar

USER live:live

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", \
  "app.jar"]