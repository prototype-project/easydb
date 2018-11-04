#!/usr/bin/env bash

# install java8
add-apt-repository ppa:openjdk-r/ppa 2>/dev/null
apt-get -y update
apt-get -y install openjdk-8-jdk

# install supervisor
apt-get -y install supervisor
mkdir -p /etc/supervisor/conf.d/

# install mongodb
MONGO_VERSION=4.0.2

apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 9DA31620334BD75D9DCB49F368818C72E52529D4
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/4.0 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-4.0.list
apt-get -y update
apt-get -y install mongodb-org=${MONGO_VERSION} mongodb-org-server=${MONGO_VERSION} mongodb-org-shell=${MONGO_VERSION} mongodb-org-mongos=${MONGO_VERSION} mongodb-org-tools=${MONGO_VERSION}

echo "mongodb-org hold" | dpkg --set-selections
echo "mongodb-org-server hold" | dpkg --set-selections
echo "mongodb-org-shell hold" | dpkg --set-selections
echo "mongodb-org-mongos hold" | dpkg --set-selections
echo "mongodb-org-tools hold" | dpkg --set-selections

mkdir -p /data/db

# install zookeeper
ZOOKEEPER_VERSION=3.4.13
wget https://www-us.apache.org/dist/zookeeper/zookeeper-${ZOOKEEPER_VERSION}/zookeeper-${ZOOKEEPER_VERSION}.tar.gz
mkdir -p /opt/zookeeper
tar -xf zookeeper-${ZOOKEEPER_VERSION}.tar.gz -C /opt/zookeeper/
mv /opt/zookeeper-${ZOOKEEPER_VERSION} /opt/zookeeper
rm zookeeper-${ZOOKEEPER_VERSION}.tar.gz

# install puppet-agent
wget http://apt.puppetlabs.com/puppet5-release-xenial.deb
dpkg -i puppet5-release-xenial.deb
apt-get -y update
apt-get -y install puppet

wget https://downloads.puppetlabs.com/puppet/puppet-5.3.5.tar.gz
tar -xzf puppet-5.3.5.tar.gz
./puppet-5.3.5/install.rb

rm puppet5-release-xenial.deb
rm puppet-5.3.5.tar.gz
rm -rf puppet-5.3.5