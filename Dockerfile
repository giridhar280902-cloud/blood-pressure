FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/blood-pressure.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]