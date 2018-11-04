#!/usr/bin/env bash

# prometheus
puppet module install puppet-prometheus --version 6.2.0
puppet apply $HOME/prometheus.pp
rm $HOME/prometheus.pp

# grafana
puppet module install puppetlabs-apt
puppet module install puppet-grafana --version 5.0.0
puppet module install puppet-healthcheck --version 0.4.1

puppet apply $HOME/grafana.pp
rm $HOME/grafana.pp
rm $HOME/grafana_dashboard.json

