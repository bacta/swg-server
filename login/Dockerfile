FROM adoptopenjdk:11-jre-hotspot

COPY target/login*-spring-boot.jar /login.jar

EXPOSE 8080
EXPOSE 44453
EXPOSE 8053

CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8053", "-jar", "/login.jar"]
