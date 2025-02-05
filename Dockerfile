FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY src/main/resources/application-dev.yml /config/application-dev.yml
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar", "--spring.profiles.active=dev", "--spring.config.location=/config/application-dev.yml"]
