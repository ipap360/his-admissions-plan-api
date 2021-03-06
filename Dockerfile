# fetch basic image
FROM maven:3.6-jdk-8-alpine AS builder

RUN apk add --update --no-cache netcat-openbsd

# application placed into /opt/app
RUN mkdir -p /opt/app
WORKDIR /opt/app

# utility for waiting other services
COPY wait-for.sh /opt/app/wait-for.sh
RUN chmod u+x wait-for.sh
RUN chmod u+x /usr/local/bin/mvn-entrypoint.sh

# selectively add the POM file and
# install dependencies
COPY pom.xml /opt/app/
# RUN mvn install

# rest of the project
COPY src /opt/app/src
RUN mvn package

FROM openjdk:8-jre-alpine
WORKDIR /root/

COPY --from=builder /opt/app/target/dependency ./lib
COPY --from=builder /opt/app/target/app.jar .

# local application port
EXPOSE 8080

ENV LOG_LEVEL INFO

# execute it
# CMD ["mvn", "exec:java"]
ENTRYPOINT ["java", "-jar", "./app.jar"]