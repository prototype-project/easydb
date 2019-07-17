#!/usr/bin/env bash

rm tmp.yml

CONFIG_SERVERS=3
for i in $(seq 1 $CONFIG_SERVERS)
do
    sed -e "s/SERVERX/server$i/g; s/CONTAINERX/container$i/g" mongo-config-server.yml  >> tmp.yml
    echo "---" >> tmp.yml
done

SHARDS=3
for i in $(seq 1 $SHARDS)
do
    sed -e "s/SHARDX/shard$i/g; s/CONTAINERX/container$i/g" mongo-shard.yml  >> tmp.yml
    echo "---" >> tmp.yml
done

ROUTERS=2
for i in $(seq 1 $ROUTERS)
do
    sed -e "s/ROUTERX/router$i/g; s/CONTAINERX/container$i/g" mongo-router.yml  >> tmp.yml
    if [ "$i" -lt "$ROUTERS" ]
    then
        echo "---" >> tmp.yml
    fi
done


