#!/usr/bin/env bash

TMP_SERVICE_FILE="tmp.yml"
echo "" > $TMP_SERVICE_FILE

for i in $(seq 1 $CONFIG_SERVERS)
do
    sed -e "s/SERVERX/server$i/g; s/CLAIMX/claim$i/g" mongo-config-server.yml  >> $TMP_SERVICE_FILE
    echo "---" >> $TMP_SERVICE_FILE
done

for i in $(seq 1 $SHARDS)
do
    sed -e "s/SHARDX/shard$i/g; s/CLAIMX/claim$(($i + $CONFIG_SERVERS))/g" mongo-shard.yml  >> $TMP_SERVICE_FILE
    echo "---" >> $TMP_SERVICE_FILE
done

ROUTERS=2
for i in $(seq 1 $ROUTERS)
do
    sed -e "s/ROUTERX/router$i/g" mongo-router.yml  >> $TMP_SERVICE_FILE
    if [ "$i" -lt "$ROUTERS" ]
    then
        echo "---" >> $TMP_SERVICE_FILE
    fi
done

kubectl apply -f $TMP_SERVICE_FILE