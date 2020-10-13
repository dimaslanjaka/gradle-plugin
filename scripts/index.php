<?php

$json = __DIR__ . '/servers.json';
if (!file_exists($json)) file_put_contents($json, json_encode([]));
$json = json_decode(file_get_contents($json), true);
if (isset($_POST['server'])) {
  $domain = trim($_POST['server']);
  if (filter_var(gethostbyname($domain), FILTER_VALIDATE_IP)) {
    $json[] = $domain;
    file_put_contents($json, json_encode($json));
  }
}
