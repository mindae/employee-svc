FROM eclipse-temurin:21-jre
LABEL authors="mindae"
WORKDIR /app
COPY /target/employee-svc-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]