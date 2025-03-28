FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/peakmeshop-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 80

ENTRYPOINT ["java", "-Dserver.port=80", "-jar", "app.jar"]