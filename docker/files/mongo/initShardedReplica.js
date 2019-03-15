rs.initiate(
    {
        _id: "mongo-replicaset",
        members: [
                { _id : 0, host : "mongo-replicaset-node1" },
                { _id : 1, host : "mongo-replicaset-node2" },
                { _id : 2, host : "mongo-replicaset-node3" }
        ]
    }
);