<?php
	include "database.php";
	header("Content-Type:application/json");
	$data = json_decode(file_get_contents('php://input'), true);
	$token = $data['token'];
	$conn = get_connection();
	$table = select_table($conn);
	$exists = exists_token($table, $token);
	if($exists == 1){
		print_r('1');
	}
	else{
		print_r('0');
	}
?>
