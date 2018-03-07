<?php ob_start(); ?>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
body {font-family: Arial, Helvetica, sans-serif;}

input[type=text], select, textarea {
    width: 100%;
    padding: 12px;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
    margin-top: 6px;
    margin-bottom: 16px;
    resize: vertical;
}

input[type=submit] {
    background-color: #4CAF50;
    color: white;
    padding: 12px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

input[type=submit]:hover {
    background-color: #45a049;
}

h1 {
  text-align: center;
}

h2 {
  text-align: center;
  color: red;
}

.container {
    border-radius: 5px;
    background-color: #f2f2f2;
    padding: 20px;
}
</style>
</head>
<body>
<h1>Serviço de email - Autenticação</h1>
<?php
$action = '';
if(isset($_POST['submit'])) {
  $url = 'http://172.82.0.2/server.php';
  $ch = curl_init($url);
  $data = array(
    'token' => $_POST['token']
   );
  $payload = json_encode($data);
  curl_setopt($ch, CURLOPT_POSTFIELDS, $payload);
  curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type:application/json'));
  curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
  $result = curl_exec($ch);
  echo $result;
  if($result == 1) {
<<<<<<< HEAD:tp1/mail_service/index.php
    $action = 1;
=======
    header("Location: http://172.82.0.3/email.php");
    die();
>>>>>>> 67ac85edc1d1a62b2d9f0beddfd15ad789772860:tp1/mail_service/mail.php
  }
  else {
    $action = 0;
  }
  curl_close($ch);
}
?>
<div class="container">
  <?php
  if($action == 1) {
      echo "<form action = \"servico2/email.php\" method = \"post\">";
  }
  else {
    echo "<form action = \"index.php\" method = \"post\">";
  }
  ?>

    <label for="token">Por favor insira o token de autenticação para ter acesso ao serviço:</label>
    <input type="text" id="token" name="token" placeholder="Token...">
    <input type="submit" value="Submit" name="submit">
  </form>
</div>
</body>
</html>