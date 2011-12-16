<?php


$xml = file_get_contents('php://input');
$filename = tempnam("/tmp/", "xmlpostdata");
file_put_contents($filename, $xml);
$ip=$_SERVER['REMOTE_ADDR'];
error_log("WARNING: Wrote XML contents to $filename from host $ip");

?>