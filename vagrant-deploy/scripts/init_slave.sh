#!/usr/bin/env bash

export HOME="/home/vagrant"

source $HOME/install.sh
source $HOME/provision_supervisor.sh
rm $HOME/install.sh
rm $HOME/provision_supervisor.sh
