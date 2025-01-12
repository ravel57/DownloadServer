FROM gradle:jdk22-alpine AS gradle
COPY --chown=gradle:gradle . /home/gradle/
WORKDIR /home/gradle/
RUN gradle bootJar

FROM alpine/java:22-jdk AS java
WORKDIR /home/java/
COPY --from=gradle /home/gradle/build/libs/*.jar /home/java/DownloadServer.jar
CMD ["java", "-jar", "DownloadServer.jar"]