rs.initiate(
    {
        _id: "mongo-config-server",
        configsvr: true,
        members: [
                { _id : 0, host : "mongo-configserver-node1" },
                { _id : 1, host : "mongo-configserver-node2" },
                { _id : 2, host : "mongo-configserver-node3" }
        ]
    }
);