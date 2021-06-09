FROM openjdk:11
ARG WAR_FILE=build/libs/*.war
COPY ${WAR_FILE} app.war
CMD ["sh", "-c", "java -Dspring.profiles.active=dev -jar app.war"]
