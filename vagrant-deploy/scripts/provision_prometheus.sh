#!/usr/bin/env bash

puppet module install puppet-prometheus --version 6.2.0
puppet apply $HOME/prometheus.pp
rm $HOME/prometheus.pp
