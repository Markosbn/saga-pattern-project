FROM openjdk:22-oracle
COPY build/libs/*.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "app.jar"]