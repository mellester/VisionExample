# Vision Service

This is the documentation for the Vision Service sub-folder in the VissionExample project.

## Table of Contents

- [Introduction](#introduction)
- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Introduction

A spring boot apllication that interacts with googles vison API to try and find lincense plate text in images.


## Installation

### Google
Make sure you follow the instruction in [google auth login](https://cloud.google.com/docs/authentication/provide-credentials-adc)
Make sure this command runs and note were the json file is located
```sh
gcloud auth application-default login
```
which by default get stored in 
- Linux, macOS: `$HOME/.config/gcloud/application_default_credentials.json`
- Windows: `%APPDATA%\gcloud\application_default_credentials.json`



### Docker

To run a docker image 
For quick and dirty onliner.
`docker run --rm  -p 8080:8080 -it $(docker build -q .)`

This both builds and runs the application

or in 2 lines
```sh
docker build -t vision-service:latest .
docker run  --rm -p 8080:8080 vision-service:latest
```

## Usage

Provide instructions on how to use the Vision Service. Include code examples or API documentation if applicable.

## Contributing

Explain how others can contribute to the Vision Service. Include guidelines for submitting pull requests or reporting issues.

## License

Specify the license under which the Vision Service is distributed.

