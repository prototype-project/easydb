rs.initiate(
    {
        _id: "easydb_shard",
        members: [
            {_id: 0, host: "10.10.10.11:27018"},
            {_id: 1, host: "10.10.10.12:27018"}
        ]
    }
);

db.getSiblingDB("easydb").runCommand(
    {
        createUser: "easydb",
        pwd: "123456",
        roles: [
            "dbOwner"
        ]
    }
);