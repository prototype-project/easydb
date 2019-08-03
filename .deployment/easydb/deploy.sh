#!/usr/bin/env bash

echo "" > tmp.yml

cat discovery-scraper.yml >> tmp.yml
echo "---" >> tmp.yml

cat easydb.yml >> tmp.yml
echo "---" >> tmp.yml

cat monitoring.yml >> tmp.yml
echo "---" >> tmp.yml

cat zookeeper.yml >> tmp.yml
echo "---" >> tmp.yml

cat nginx.yml >> tmp.yml
echo "---" >> tmp.yml

kubectl apply -f tmp.yml

echo "Waiting for containers to create..."
kubectl get pods | grep "ContainerCreating"

while [ $? -eq 0 ]
do
  sleep 1
  echo "\n\nWaiting for the following containers"
  kubectl get pods | grep "ContainerCreating"
done