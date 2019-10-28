#!/bin/bash

mongod --bind_ip 0.0.0.0 &
mongo localhost:27017/easydb < /opt/easydb/resources/init_dev_mongo.js
while [ $? -eq 1 ]
do
    mongo localhost:27017/easydb < /opt/easydb/resources/init_dev_mongo.js
done
/bin/bash /opt/zookeeper/bin/zkServer.sh start /opt/zookeeper/conf/zoo_sample.cfg