# server base image - java 17
FROM eclipse-temurin:17.0.11_9-jre-alpine

# copy .jar file to docker
COPY ./build/libs/FoodKing-0.0.1-SNAPSHOT.jar app.jar

# copy application.properties to the correct location
COPY ./src/main/resources/DB.properties /resources/DB.properties

EXPOSE 8080
ENTRYPOINT ["java","-Dspring.config.location=file:/resources/application.properties", "-jar", "app.jar"]