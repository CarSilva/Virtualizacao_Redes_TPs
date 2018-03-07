<?php
function get_connection(){
    $dbuser = postgres;
    $conn;
    try {
      $conn = new PDO("pgsql:host=172.81.0.2;dbname=postgres", $dbuser, $dbuser);
    } catch (PDOException $e) {
      die('Connection failed: ' . $e->getMessage());
    }
    return $conn;
  }
  function  insert_into_table($conn, $token, $name, $pass){
    $conn->beginTransaction();
    $result = $conn->exec("INSERT INTO tokens VALUES ('$token', '$name', '$pass');");
    $conn->commit();
  }
  function select_table($conn){
    $sql = 'SELECT * FROM tokens;';
    $table = $conn->query($sql);
    return $table;
  }
  function exists_name($table, $name, $pass){
    $result = -2;
    foreach ($table as $row) {
      if($row["name"] == $name && $row["password"] == $pass){
        $result = $row["token"];
        break;
      }
      else if($row["name"] == $name && $row["password"] != $pass){
        $result = -1;
        break;
      }
    }
    return $result;
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
?>
