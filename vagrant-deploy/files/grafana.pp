class { 'grafana':
  cfg => {
    server   => {
      http_port     => 9095,
    },
    database => {
      type          => 'sqlite3',
      name          => 'easydb',
      user          => 'easydb',
      password      => 'easydb',
    },
    users    => {
      allow_sign_up => false,
    },
  },
}