FROM openjdk:8 AS builder
RUN chmod +x /gradlew
RUN ./gradlew bootJar
FROM openjdk:8
COPY --from=builder build/libs/shadowmate-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]