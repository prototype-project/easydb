#!/usr/bin/env bash

sudo mkdir -p /var/log/

# prepare mongodb command
sudo mkdir -p /data/db/
sudo touch /var/log/mongodb.log

# prepare easydb command
sudo touch /var/log/easydb.log

JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64"

rsync -av --exclude=".*" --exclude "build" --exclude "vagrant-deploy" /vagrant_data/ /home/vagrant/easydb
/home/vagrant/easydb/gradlew clean test integrationTest distZip -p /home/vagrant/easydb

sudo rm -rf /opt/easydb

sudo mkdir -p /opt/easydb/dist
sudo mkdir -p /opt/easydb/resources

sudo cp /home/vagrant/easydb/build/distributions/* /opt/easydb/dist
sudo cp -r /home/vagrant/easydb/src/main/resources/ /opt/easydb/resources/

rm -rf /home/vagrant/easydb

sudo unzip /opt/easydb/dist/*.zip -d /opt/easydb/dist/

# run supervisor processes
sudo cp /home/vagrant/supervisor.conf /etc/supervisor/conf.d/supervisor.conf
sudo rm /home/vagrant/supervisor.conf

sudo supervisorctl reread
sudo supervisorctl update

