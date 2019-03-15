sh.addShard("mongo-replicaset/mongo-replicaset-node1");
sh.addShard("mongo-replicaset/mongo-replicaset-node2");
sh.addShard("mongo-replicaset/mongo-replicaset-node3");
sh.enableSharding("easydb");