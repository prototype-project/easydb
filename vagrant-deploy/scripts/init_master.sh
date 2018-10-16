#!/usr/bin/env bash

export HOME="/home/vagrant"

source $HOME/install.sh
source $HOME/provision_supervisor_on_master.sh
source $HOME/provision_prometheus.sh
rm $HOME/install.sh
rm $HOME/provision_supervisor_on_master.sh
rm $HOME/provision_prometheus.sh
