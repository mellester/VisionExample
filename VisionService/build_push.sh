#!/bin/bash

# Get the current git commit hash
GIT_HASH=$(git rev-parse --short HEAD)

# Build the Docker image with the git hash as the tag
docker buildx build --platform linux/amd64,linux/arm64 -t vision-service:$GIT_HASH .

# Push the Docker image to a remote repository
docker tag vision-service:$GIT_HASH vision-service:latest
docker tag vision-service:$GIT_HASH public.ecr.aws/i3m5a4v5/vision-example:$GIT_HASH
docker tag vision-service:$GIT_HASH public.ecr.aws/i3m5a4v5/vision-example:latest
docker push public.ecr.aws/i3m5a4v5/vision-example:$GIT_HASH public.ecr.aws/i3m5a4v5/vision-example:latest






