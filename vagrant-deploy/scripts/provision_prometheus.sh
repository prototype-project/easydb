#!/usr/bin/env bash

puppet module install puppet-prometheus --version 6.2.0
puppet apply /home/vagrant/prometheus.pp
rm /home/vagrant/prometheus.pp
