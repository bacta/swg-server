FROM adoptopenjdk:11-jre-hotspot

COPY target/login*-spring-boot.jar /login.jar

EXPOSE 8080
EXPOSE 44453

CMD ["java", "-Dspring.pid.file=login.pid -Dlogging.file=logs/login.log", "-jar", "/login.jar"]
