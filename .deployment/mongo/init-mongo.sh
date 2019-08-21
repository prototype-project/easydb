#config-servers

EASYDB_USERNAME="easydb"
EASYDB_PASSWORD="123456"

EASYDB_ADMIN_USERNAME="admin"
EASYDB_ADMIN_PASSWORD="123456"

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

while [ $? -ne 0 ]
do
  sleep 1
  echo "\n\nWaiting for $POD_NAME to be alive..."
  kubectl exec -it $POD_NAME -- bash -c "mongo localhost:27017 --eval '$CMD'"
done


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

while [ $? -ne 0 ]
do
  sleep 1
  echo "\n\nWaiting for $POD_NAME to be alive..."
  kubectl exec -it $POD_NAME -- bash -c "mongo localhost:27017 --eval '$CMD'"
done

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

CMD='db.getSiblingDB("easydb").runCommand({createUser: "'$EASYDB_USERNAME'",pwd: "'$EASYDB_PASSWORD'",roles: ["dbOwner"]});'
kubectl exec -it $POD_NAME -- bash -c "mongo localhost:27017 --eval '$CMD'"

CMD='db.getSiblingDB("admin").runCommand({createUser: "'$EASYDB_ADMIN_USERNAME'",pwd: "'$EASYDB_ADMIN_PASSWORD'",roles: [ { role: "userAdminAnyDatabase", db: "admin" }, "readWriteAnyDatabase" ]});'
kubectl exec -it $POD_NAME -- bash -c "mongo localhost:27017 --eval '$CMD'"
