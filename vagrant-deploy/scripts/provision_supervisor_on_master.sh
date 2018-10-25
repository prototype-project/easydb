#!/usr/bin/env bash

mkdir -p /var/log/

# prepare mongodb command
mkdir -p /data/mongodb/mongodb_config_server
mkdir -p /data/mongodb/mongodb_shard
mkdir -p /data/mongodb/mongodb_router

touch /var/log/mongodb_config_server.log
touch /var/log/mongodb_shard.log
touch /var/log/mongodb_router.log

mkdir -p /etc/mongodb/
cp $HOME/mongodb_config_server.conf /etc/mongodb/mongodb_config_server.conf
cp $HOME/mongodb_shard.conf /etc/mongodb/mongodb_shard.conf
cp $HOME/mongodb_router.conf /etc/mongodb/mongodb_router.conf

rm $HOME/mongodb_config_server.conf
rm $HOME/mongodb_shard.conf
rm $HOME/mongodb_router.conf

# prepare easydb command
touch /var/log/easydb.log

export JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64"

rsync -av --exclude=".*" --exclude "build" --exclude "vagrant-deploy" /vagrant_data/ $HOME/easydb
#$HOME/easydb/gradlew clean test integrationTest -p $HOME/easydb

rm $HOME/easydb/src/main/resources/*.yml

$HOME/easydb/gradlew clean distZip -p $HOME/easydb

rm -rf /opt/easydb
mkdir -p /opt/easydb/dist
mkdir -p /opt/easydb/resources

cp $HOME/easydb/build/distributions/* /opt/easydb/dist
cp -r $HOME/easydb/src/main/resources/ /opt/easydb/
cp $HOME/application.yml /opt/easydb/resources/application.yml

rm -rf $HOME/easydb

unzip /opt/easydb/dist/*.zip -d /opt/easydb/dist/


# run supervisor processes
cp $HOME/supervisor.conf /etc/supervisor/conf.d/supervisor.conf
rm $HOME/supervisor.conf

supervisorctl reread
supervisorctl update mongodb_config_server
supervisorctl update mongodb_shard
supervisorctl update mongodb_router

#create config-server replica set
mongo localhost:27018/config < $HOME/mongoConfigServerInit.js
while [ $? -ne 0 ]
do
  sleep 1s
  mongo localhost:27018/config < $HOME/mongoConfigServerInit.js
done

rm $HOME/mongoConfigServerInit.js

# wait until mongo slaves are reachable
nc -zv 10.10.10.12 27017
while [ $? -ne 0 ]
do
  sleep 1s
  nc -zv 10.10.10.12 27017
done

# create mongodb shard
mongo localhost:27019/config < $HOME/mongoRouterInit.js
while [ $? -ne 0 ]
do
  sleep 1s
  mongo localhost:27019/config < $HOME/mongoRouterInit.js
done

rm $HOME/mongoRouterInit.js

mongo localhost:27019/easydb < $HOME/mongoCreateUser.js # firstly shard this collections
rm $HOME/mongoCreateUser.js

# run easydb
supervisorctl update easydb
