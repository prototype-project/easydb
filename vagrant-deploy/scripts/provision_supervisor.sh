#!/usr/bin/env bash

mkdir -p /var/log/

# prepare mongodb command
mkdir -p /data/db/
touch /var/log/mongodb.log

# prepare easydb command
touch /var/log/easydb.log

JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64"

rsync -av --exclude=".*" --exclude "build" --exclude "vagrant-deploy" /vagrant_data/ /home/vagrant/easydb
/home/vagrant/easydb/gradlew clean test integrationTest -p /home/vagrant/easydb

rm /home/vagrant/easydb/src/main/resources/*.yml

/home/vagrant/easydb/gradlew clean distZip -p /home/vagrant/easydb

rm -rf /opt/easydb

mkdir -p /opt/easydb/dist
mkdir -p /opt/easydb/resources

cp /home/vagrant/easydb/build/distributions/* /opt/easydb/dist
cp -r /home/vagrant/easydb/src/main/resources/ /opt/easydb/
cp /home/vagrant/application.yml /opt/easydb/resources/application.yml

rm -rf /home/vagrant/easydb

unzip /opt/easydb/dist/*.zip -d /opt/easydb/dist/

# run supervisor processes
cp /home/vagrant/supervisor.conf /etc/supervisor/conf.d/supervisor.conf
rm /home/vagrant/supervisor.conf

supervisorctl reread
supervisorctl update mongodb

# create mongodb user
mongo localhost:27017/easydb /home/vagrant/mongoInit.js
while [ $? -ne 0 ]
do
  sleep 1s
  mongo localhost:27017/easydb /home/vagrant/mongoInit.js
done

rm /home/vagrant/mongoInit.js

supervisorctl update easydb

