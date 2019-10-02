#!/bin/bash

mongod &
mongo localhost:27017/easydb < /opt/easydb/resources/init_dev_mongo.js
while [ $? -eq 1 ]
do
    mongo localhost:27017/easydb < /opt/easydb/resources/init_dev_mongo.js
done
/bin/bash /opt/zookeeper/bin/zkServer.sh start /opt/zookeeper/conf/zoo_sample.cfg

/opt/easydb/dist/bin/easydb -port 9000 -environment local