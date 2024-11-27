FROM adoptopenjdk:16-jre-openj9-focal

RUN mkdir /app

WORKDIR /app

ADD ./api/target/api-1.0-SNAPSHOT.jar /app

EXPOSE 8080

ENV DB_URL=db_url \
    DB_USER=db_user \
    DB_PASSWORD=db_pass \
    JWT_SECRET=jwt_secret

ENTRYPOINT ["java", "-jar", "api-1.0-SNAPSHOT.jar"]
