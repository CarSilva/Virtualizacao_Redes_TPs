<?php
	function get_connection(){
		$dbuser = postgres;
		$conn;
		try {
			$conn = new PDO("pgsql:host=db;dbname=postgres", $dbuser, $dbuser);
		} catch (PDOException $e) {
			die('Connection failed: ' . $e->getMessage());
		}
		return $conn;
	}

	function select_table($conn){
		$sql = 'SELECT * FROM tokens;';
		$table = $conn->query($sql);
		return $table;
	}

	function exists_token($table, $token){
		$result = 0;
	  foreach ($table as $row) {
	    if($row["token"] == $token){
	      $result = 1;
	      break;
	     }
	   }
	   return $result;
	}

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
