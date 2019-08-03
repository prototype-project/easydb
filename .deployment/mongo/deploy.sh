#!/usr/bin/env bash

echo "" > tmp.yml

CONFIG_SERVERS=3
for i in $(seq 1 $CONFIG_SERVERS)
do
    sed -e "s/SERVERX/server$i/g" mongo-config-server.yml  >> tmp.yml
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


kubectl apply -f tmp.yml


#config-servers
echo "Waiting for config server containers"
kubectl get pods | grep "mongo-config" | grep "ContainerCreating"

while [ $? -eq 0 ]
do
  sleep 1
  echo "\n\nWaiting for the following containers:"
  kubectl get pods | grep "mongo-config" | grep "ContainerCreating"
done

POD_NAME=$(kubectl get pods | grep "mongo-config" | awk 'NR==1{print $1}')
echo "Initializing mongo-config-service... connecting to: $POD_NAME"

CMD='rs.initiate({_id: "mongo-config-server", configsvr: true, members: [{ _id : 0, host : "mongo-config-server1" },{ _id : 1, host : "mongo-config-server2" },{ _id : 2, host : "mongo-config-server3" }]});'
kubectl exec -it $POD_NAME -- bash -c "mongo localhost:27017 --eval '$CMD'"



#shards
echo "Waiting for shard containers"
kubectl get pods | grep "mongo-shard" | grep "ContainerCreating"

while [ $? -eq 0 ]
do
  sleep 1
  echo "\n\nWaiting for the following containers"
  kubectl get pods | grep "mongo-shard" | grep "ContainerCreating"
done

POD_NAME=$(kubectl get pods | grep "mongo-shard" | awk 'NR==1{print $1}')
echo "Initializating mongo-shards... connecting to: $POD_NAME"

CMD='rs.initiate({_id: "mongo-replicaset", members: [{ _id : 0, host : "mongo-shard1" }, { _id : 1, host : "mongo-shard2" }, { _id : 2, host : "mongo-shard3" }]});'

kubectl exec -it $POD_NAME -- bash -c "mongo localhost:27017 --eval '$CMD'"


#routers
echo "Waiting for router containers"
kubectl get pods | grep "mongo-router" | grep "ContainerCreating"

while [ $? -eq 0 ]
do
  sleep 1
  echo "\n\nWaiting for the following containers"
  kubectl get pods | grep "mongo-router" | grep "ContainerCreating"
done

POD_NAME=$(kubectl get pods | grep "mongo-router" | awk 'NR==1{print $1}')
echo "Initializating mongo-routers... connecting to: $POD_NAME"

CMD='sh.addShard("mongo-replicaset/mongo-shard1");sh.addShard("mongo-replicaset/mongo-shard2");sh.addShard("mongo-replicaset/mongo-shard3");sh.enableSharding("easydb");'

kubectl exec -it $POD_NAME -- bash -c "mongo localhost:27017 --eval '$CMD'"

while [ $? -ne 0 ]
do
  sleep 1
  echo "\n\nWaiting for mongo router to be alive..."
  kubectl exec -it $POD_NAME -- bash -c "mongo localhost:27017 --eval '$CMD'"
done

echo "Creating database users"

CMD='db.getSiblingDB("easydb").runCommand({createUser: "easydb",pwd: "123456",roles: ["dbOwner"]});'
kubectl exec -it $POD_NAME -- bash -c "mongo localhost:27017 --eval '$CMD'"

CMD='db.getSiblingDB("admin").runCommand({createUser: "admin",pwd: "123456",roles: [ { role: "userAdminAnyDatabase", db: "admin" }, "readWriteAnyDatabase" ]});'
kubectl exec -it $POD_NAME -- bash -c "mongo localhost:27017 --eval '$CMD'"

echo "Server is ready !"