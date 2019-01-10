class { 'prometheus::server':
  version        => '2.0.0',
  scrape_configs => [
    { 'job_name' => 'prometheus',
      'scrape_interval' => '10s',
      'scrape_timeout'  => '10s',
      'metrics_path' => '/prometheus',
      'static_configs'  => [
        { 'targets' => [],
          'labels'  => { 'alias' => 'Easydb'}
        }
      ]
    }
  ]
}