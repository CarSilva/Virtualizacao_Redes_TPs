<?php
header("Content-Type:application/json");
$data = json_decode(file_get_contents('php://input'), true);
$token = $data['token'];
if($token == 69) {
  print_r('1');
}
else {
  print_r('0');
}
?>
