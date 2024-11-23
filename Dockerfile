FROM adoptopenjdk:16-jre-openj9-focal

RUN mkdir /app

WORKDIR /app

ADD ./api/target/api-1.0-SNAPSHOT.jar /app

EXPOSE 8080

ENV DB_URL=jdbc:postgresql://host.docker.internal:5434/uporabniki
ENV DB_USER=postgres
ENV DB_PASSWORD=postgres
# Only for development
ENV JWT_SECRET=secret


ENTRYPOINT ["java", "-jar", "api-1.0-SNAPSHOT.jar"]
