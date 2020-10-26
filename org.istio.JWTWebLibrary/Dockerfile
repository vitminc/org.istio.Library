FROM openjdk:8-jdk-alpine
ADD target/org.istio.JWTWebLibrary-0.0.1.jar /org.istio.JWTWebLibrary-0.0.1.jar
ENV SPRING_CONFIG_LOCATION=/opt/application.properties
ENTRYPOINT ["java", "-jar", "org.istio.JWTWebLibrary-0.0.1.jar"]

