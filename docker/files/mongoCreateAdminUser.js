db.getSiblingDB("admin").runCommand(
    {
        createUser: "admin",
        pwd: "123456",
        roles: [ { role: "userAdminAnyDatabase", db: "admin" }, "readWriteAnyDatabase" ]
    }
);