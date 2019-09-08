db.getSiblingDB("easydb").runCommand(
    {
        createUser: "easydb",
        pwd: "easydb",
        roles: [
            "dbOwner"
        ]
    }
);

db.getSiblingDB("admin").runCommand(
    {
        createUser: "admin",
        pwd: "admin",
        roles: [ { role: "userAdminAnyDatabase", db: "admin" }, "readWriteAnyDatabase" ]
    }
);