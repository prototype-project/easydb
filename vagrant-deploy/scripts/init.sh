#!/usr/bin/env bash

# install java8
sudo add-apt-repository ppa:openjdk-r/ppa 2>/dev/null
sudo apt-get -y update
sudo apt-get -y install openjdk-8-jdk

# install supervisor
sudo apt-get -y install supervisor
sudo mkdir -p /etc/supervisor/conf.d/

# install mongodb
MONGO_VERSION=4.0.2

sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 9DA31620334BD75D9DCB49F368818C72E52529D4
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/4.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-4.0.list
sudo apt-get -y update
sudo apt-get -y install mongodb-org=${MONGO_VERSION} mongodb-org-server=${MONGO_VERSION} mongodb-org-shell=${MONGO_VERSION} mongodb-org-mongos=${MONGO_VERSION} mongodb-org-tools=${MONGO_VERSION}

echo "mongodb-org hold" | sudo dpkg --set-selections
echo "mongodb-org-server hold" | sudo dpkg --set-selections
echo "mongodb-org-shell hold" | sudo dpkg --set-selections
echo "mongodb-org-mongos hold" | sudo dpkg --set-selections
echo "mongodb-org-tools hold" | sudo dpkg --set-selections

sudo mkdir -p /data/db

# TODO create specific mongo user

# install zookeeper
sudo apt-get -y install zookeeperd

# install puppet-agent
wget http://apt.puppetlabs.com/puppet5-release-xenial.deb
sudo dpkg -i puppet5-release-xenial.deb
sudo apt-get -y update
sudo apt-get -y install puppet

wget https://downloads.puppetlabs.com/puppet/puppet-5.3.5.tar.gz
tar -xzf puppet-5.3.5.tar.gz
sudo ./puppet-5.3.5/install.rb

rm puppet5-release-xenial.deb
rm puppet-5.3.5.tar.gz
rm -rf puppet-5.3.5