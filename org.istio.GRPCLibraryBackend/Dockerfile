FROM openjdk:8-jdk-alpine
ADD target/org.istio.GRPCLibraryBackend-1.0.1.jar /org.istio.GRPCLibraryBackend-1.0.1.jar
ADD target/libs/* /libs/
ENTRYPOINT ["java", "-jar", "org.istio.GRPCLibraryBackend-1.0.1.jar"]

