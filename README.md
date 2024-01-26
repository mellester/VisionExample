# VisionExample
A small Java application where we use Vision to see a license plate

To build the application build each of the modules:

```
.\DatabaseModule\gradlew build -p .\DatabaseModule\
.\Management\gradlew build -p .\Management\
.\VisionService\gradlew build -p .\VisionService\
```

```bash
DatabaseModule/gradlew build -p DatabaseModule/
Management/gradlew build -p Management/
VisionService/gradlew build -p VisionService/
```


Run with:

`docker compose up`

The frontend folder is exposed on http://localhost:8080/

The three services are exposed on the following paths:
* http://localhost:8080/api/rdw
* http://localhost:8080/api/management
* http://localhost:8080/api/vision

If you don't have access to docker you can choose to run the application on e.g. https://labs.play-with-docker.com/
```
apk add openjdk17
git clone https://github.com/mellester/VisionExample.git
```

And run the build scripts
