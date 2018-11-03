#!/usr/bin/env bash

# prometheus
puppet module install puppet-prometheus --version 6.2.0
puppet apply $HOME/prometheus.pp
rm $HOME/prometheus.pp

# grafana
puppet module install puppetlabs-apt
puppet module install puppet-grafana --version 5.0.0
puppet apply $HOME/grafana.pp
rm $HOME/grafana.pp
