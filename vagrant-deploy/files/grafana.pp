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


grafana_dashboard { 'easydb_dashboard':
  grafana_url       => 'http://localhost:9095',
  grafana_user      => 'admin',
  grafana_password  => 'admin',
  grafana_api_path  => '/grafana/api',
  content           => template('grafana_dashboard.json'),
}