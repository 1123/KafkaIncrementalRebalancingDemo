FROM openjdk:17 
COPY target/demo-producer-0.0.1-SNAPSHOT.jar app.jar
ENV SPRING_PROFILES_ACTIVE benediktcloud
# RUN apk add --no-cache libstdc++
ENTRYPOINT ["java","-jar","/app.jar"]

