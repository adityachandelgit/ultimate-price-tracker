FROM gradle:8.6.0-jdk21-alpine as build
COPY . ./
RUN gradle clean build

FROM amazoncorretto:21.0.2-alpine3.19 as run
COPY --from=build /home/gradle/build/libs/ultimate-price-tracker-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
