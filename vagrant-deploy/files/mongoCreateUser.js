db.getSiblingDB("easydb").runCommand(
    {
        createUser: "easydb",
        pwd: "123456",
        roles: [
            "dbOwner"
        ]
    }
);

db.getSiblingDB("admin").runCommand(
    {
        createUser: "admin",
        pwd: "123456",
        roles: [ { role: "userAdminAnyDatabase", db: "admin" }, "readWriteAnyDatabase" ]
    }
);