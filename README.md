# VisionExample
A small Java application where we use Vision to see a license plate

### Setup
Clone this repro.
```sh
git clone mellester/VisionExample # Cloning into VisionExample
cd VisionExample # Switching folders into the new cloned folder
cp .env.example .env # Copying the exmaple enf file into 
```

VisionService requires google cloud permisons see [google auth login](https://cloud.google.com/docs/authentication/provide-credentials-adc) on how to authenticate you pc

Make sure the .env points to the correct location of your GOOGLE_APPLICATION_CREDENTIALS

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



Run:
```sh
docker compose build
docker compose up
```

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


# Progress Notes.
The projects VisionService currently runs on [app.mellesterk.nl](http://app.mellesterk.nl)
ATM. Only the VisionService runs.