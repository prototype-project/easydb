#!/usr/bin/env bash

mkdir -p /var/log/

# prepare mongodb command
mkdir -p /data/db/
touch /var/log/mongodb.log

cp $HOME/mongodb_config_server.conf /etc/mongodb_config_server.conf
cp $HOME/mongodb_shard.conf /etc/mongodb_shard.conf

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
cp $HOME/supervisor.conf /etc/supervisor/conf.d/supervisor.conf
rm $HOME/supervisor.conf

supervisorctl reread
supervisorctl update mongodb_config_server

#create config-server replica set
mongo localhost:27018/easydb $HOME/mongoConfigServerInit.js
while [ $? -ne 0 ]
do
  sleep 1s
  mongo localhost:27018/easydb $HOME/mongoConfigServerInit.js
done

rm $HOME/mongoConfigServerInit.js

# create mongodb shard
supervisorctl update mongodb_shard

mongo localhost:27017/easydb $HOME/mongoMasterShardInit.js
while [ $? -ne 0 ]
do
  sleep 1s
  mongo localhost:27017/easydb $HOME/mongoMasterShardInit.js
done

rm $HOME/mongoMasterShardInit.js

supervisorctl update easydb

