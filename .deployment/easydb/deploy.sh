#!/usr/bin/env bash

MONGO_USERNAME="easydb"
MONGO_PASSWORD="123456"

MONGO_ADMIN_USERNAME="admin"
MONGO_ADMIN_PASSWORD="123456"

echo "" > tmp.yml
echo "" > config-map-tmp.yml

cat easydb.yml >> tmp.yml
echo "---" >> tmp.yml

cat monitoring.yml >> tmp.yml
echo "---" >> tmp.yml

cat zookeeper.yml >> tmp.yml
echo "---" >> tmp.yml

cat nginx.yml >> tmp.yml
echo "---" >> tmp.yml

sed -e "s/MONGO_USERNAME/$MONGO_USERNAME/g; s/MONGO_PASSWORD/$MONGO_PASSWORD/g; s/MONGO_ADMIN_USERNAME/$MONGO_ADMIN_USERNAME/g; s/MONGO_ADMIN_PASSWORD/$MONGO_ADMIN_PASSWORD/g" application-config.yml  >> config-map-tmp.yml

kubectl apply -f config-map-tmp.yml
kubectl apply -f tmp.yml

echo "Waiting for containers to create..."
kubectl get pods | grep "ContainerCreating"

while [ $? -eq 0 ]
do
  sleep 1
  echo "\n\nWaiting for the following containers"
  kubectl get pods | grep "ContainerCreating"
done