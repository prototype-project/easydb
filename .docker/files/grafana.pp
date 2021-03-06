http_conn_validator { 'grafana-conn-validator' :
  host     => 'localhost',
  port     => '9095',
  use_ssl  => false,
  test_url => '/public/img/grafana_icon.svg',
}
-> grafana_datasource { 'easydb':
   grafana_url      => 'http://localhost:9095',
   grafana_user     => 'admin',
   grafana_password => 'admin',
   grafana_api_path => '/api',
   type             => 'prometheus',
   url              => 'http://localhost:9090',
   access_mode      => 'proxy',
   is_default       => true,
}
-> grafana_dashboard { 'easydb':
   grafana_url       => 'http://localhost:9095',
   grafana_user      => 'admin',
   grafana_password  => 'admin',
   grafana_api_path  => '/api',
   content           => template('/tmp/grafana_dashboard.json'),
}