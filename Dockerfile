FROM maven:3.6.1-jdk-11-slim AS builder
COPY . /src
WORKDIR /src
RUN mvn package
RUN ls /src/target

FROM adoptopenjdk/openjdk11:x86_64-debian-jre-11.0.3_7
COPY --from=builder /src/target /apps
ENV AWS_CREDENTIAL_PROFILES_FILE=/credentials/credentials.aws
WORKDIR /apps
EXPOSE 8877
ENTRYPOINT ["java","-Xmx40m", "-jar", "compte-1.0.0-SNAPSHOT-fat.jar"]


