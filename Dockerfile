FROM node:20-alpine3.19 AS nodejs
RUN apk add --no-cache git
WORKDIR /usr/src/node
RUN git clone https://github.com/ravel57/download-server-front.git
WORKDIR /usr/src/node/download-server-front
RUN npm install
RUN vite build

FROM gradle:jdk22-alpine AS gradle
COPY --chown=gradle:gradle . /home/gradle/
COPY --from=nodejs /usr/src/node/itdesk-front/dist/.   /home/gradle/src/main/resources/static/
WORKDIR /home/gradle/
RUN gradle bootJar

FROM alpine/java:22-jdk AS java
WORKDIR /home/java/
COPY --from=gradle /home/gradle/build/libs/*.jar /home/java/DownloadServer.jar
CMD ["java", "-jar", "DownloadServer.jar"]