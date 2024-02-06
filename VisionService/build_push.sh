#!/bin/bash

# Check if Docker is online
if docker info &> /dev/null; then
    echo "Docker is online"
else
    echo "Docker is offline"
    exit 1
fi
    # Hack to get login to wotk on Windows
    echo '{"auths": {"https://index.docker.io/v1/": {}}, "HttpHeaders": { "User-Agent": "Docker-Client/19.03.12 (windows)"}}' > ~/.docker/config.json
    # Login to AWS ECR which is always us-east-1
    aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws 

    # Get the current git commit hash
    GIT_HASH=$(git rev-parse --short HEAD)

    # Build the Docker image with the git hash as the tag
    docker buildx build --push  --platform linux/amd64,linux/arm64 -t public.ecr.aws/i3m5a4v5/vision-example:$GIT_HASH .
    docker buildx build --push  --platform linux/amd64,linux/arm64 -t public.ecr.aws/i3m5a4v5/vision-example:latest .


    









