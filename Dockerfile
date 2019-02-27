FROM openjdk:8-alpine

COPY target/uberjar/propellerhead-technical.jar /propellerhead-technical/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/propellerhead-technical/app.jar"]
