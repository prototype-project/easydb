#!/usr/bin/env bash

sudo puppet module install puppet-prometheus --version 6.2.0
sudo puppet apply /home/vagrant/prometheus.pp
rm /home/vagrant/prometheus.pp
