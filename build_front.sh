#!/bin/bash

path=$(pwd)
cd ../../WebstormProjects/download-server-front || exit

yarn install
yarn build

if [ -d "./dist/" ]; then
    cp -r "./dist/"* "$path/src/main/resources/static"
else
    echo "Copping error" >&2
    exit 1
fi