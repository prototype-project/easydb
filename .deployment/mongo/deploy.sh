#!/usr/bin/env bash

CONFIG_SERVERS=3
SHARDS=3

source ./deploy-storage.sh
source ./deploy-services.sh
source ./init-mongo.sh

echo "Server is ready !"