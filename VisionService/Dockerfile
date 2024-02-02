# Stage 1: Build stage
FROM gradle:jdk17-alpine AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon
RUN rm /app/build/libs/*-plain.jar ; mv /app/build/libs/*.jar /app/build/libs/app.jar

# Stage 2: Production stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Location were the credentials.json file needs to be mounted by docker.
ARG GOOGLE_APPLICATION_CREDENTIALS="/app/credentials.json"
ENV GOOGLE_APPLICATION_CREDENTIALS=$GOOGLE_APPLICATION_CREDENTIALS
COPY --from=build /app/build/libs/app.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
