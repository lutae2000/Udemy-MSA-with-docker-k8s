#Java runtime based on openJDK 17
FROM openjdk:23-ea-17-jdk-slim

#information around who maintains the image
MAINTAINER utlee

RUN apt-get update && apt-get install -y curl

# Add the application's jar to the image
COPY build/libs/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar

# execute the application below command
ENTRYPOINT ["java", "-jar", "accounts-0.0.1-SNAPSHOT.jar"]

#도커 이미지 빌드
#docker build . -t utlee/accounts:s4