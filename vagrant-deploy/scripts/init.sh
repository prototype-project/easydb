#!/usr/bin/env bash

source /home/vagrant/install.sh
source /home/vagrant/provision_supervisor.sh
source /home/vagrant/provision_prometheus.sh
rm /home/vagrant/install.sh
rm /home/vagrant/provision_supervisor.sh
rm /home/vagrant/provision_prometheus.sh