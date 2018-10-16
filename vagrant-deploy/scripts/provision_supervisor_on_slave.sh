#!/usr/bin/env bash

mkdir -p /var/log/

# prepare mongodb command
mkdir -p /data/db/
touch /var/log/mongodb.log

cp $HOME/mongod.conf /etc/mongod.conf

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
supervisorctl update mongodb

supervisorctl update easydb

