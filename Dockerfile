FROM openjdk:17-oracle
WORKDIR /app
EXPOSE 80
COPY ./target/*.jar /app/app1.jar
ENTRYPOINT ["java", "-jar", "/app/app1.jar","--server.port=80"]

