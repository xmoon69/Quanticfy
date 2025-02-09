FROM openjdk:17-jdk-alpine
EXPOSE 8089
ADD target/demo-0.0.1-SNAPSHOT.jar demo-0.01-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","demo-0.01-SNAPSHOT.jar"]
