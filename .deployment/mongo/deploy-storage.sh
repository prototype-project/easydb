#!/usr/bin/env bash

TMP_VOLUME_FILE="volumes-tmp.yml"
echo "" > $TMP_VOLUME_FILE

for i in $(seq 1 $CONFIG_SERVERS)
do
    sed -e "s/VOLUMEX/volume$i/g" persistent-volume.yml  >> $TMP_VOLUME_FILE
    echo "---" >> $TMP_VOLUME_FILE

    sed -e "s/CLAIMX/claim$i/g" volume-claim.yml  >> $TMP_VOLUME_FILE
    echo "---" >> $TMP_VOLUME_FILE
done

for i in $(seq 1 $SHARDS)
do
    sed -e "s/VOLUMEX/volume$(($i + $CONFIG_SERVERS))/g" persistent-volume.yml  >> $TMP_VOLUME_FILE
    echo "---" >> $TMP_VOLUME_FILE

    sed -e "s/CLAIMX/claim$(($i + $CONFIG_SERVERS))/g" volume-claim.yml  >> $TMP_VOLUME_FILE
    echo "---" >> $TMP_VOLUME_FILE
done

kubectl apply -f storage-class.yml
kubectl apply -f $TMP_VOLUME_FILE

