rs.initiate(
    {
        _id: "easydb-config-server",
        configsvr: true,
        members: [
            { _id : 0, host : "10.10.10.11:27018" }
        ]
    }
);