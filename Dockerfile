FROM gradle:8.13-jdk17 AS build

WORKDIR /app

COPY . .

RUN gradle build -x test --no-daemon

FROM openjdk:17-jdk

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]