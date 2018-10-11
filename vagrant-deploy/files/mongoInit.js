db.getSiblingDB("easydb").runCommand(
    {
        createUser: "easydb",
        pwd: "123456",
        roles: [
            "dbOwner"
        ]
    });