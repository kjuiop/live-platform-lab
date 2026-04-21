FROM gradle:8.14-jdk21 AS build

WORKDIR /app

COPY gradlew .
COPY gradle/wrapper/gradle-wrapper.jar gradle/wrapper/gradle-wrapper.properties ./gradle/wrapper/
RUN chmod +x gradlew

COPY build.gradle settings.gradle gradle.properties ./
COPY live-commerce-core/build.gradle ./live-commerce-core/
COPY live-commerce-api/build.gradle ./live-commerce-api/
COPY gradle ./gradle

RUN gradle :live-commerce-api:dependencies --no-daemon || true

COPY live-commerce-core/src ./live-commerce-core/src
COPY live-commerce-api/src ./live-commerce-api/src

RUN gradle :live-commerce-api:bootJar --no-daemon

RUN test -f live-commerce-api/build/libs/*.jar || (echo "JAR file not found" && exit 1)

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S live && adduser -S live -G live

WORKDIR /app

COPY --from=build --chown=live:live /app/live-commerce-api/build/libs/*.jar app.jar

USER live:live

EXPOSE 8090

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", \
  "app.jar"]
