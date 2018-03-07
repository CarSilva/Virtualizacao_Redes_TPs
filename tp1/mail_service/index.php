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
    padding: 5px 15px;
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
  color: #4CAF50;
}

span{
  color: #FF0000;
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
$result = -1;
if(isset($_POST['validar'])) {
  $url = 'http://auth/server.php';
  $ch = curl_init($url);

  $data = array(
    'token' => $_POST['token']
   );
  $payload = json_encode($data);
  curl_setopt($ch, CURLOPT_POSTFIELDS, $payload);
  curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type:application/json'));
  curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
  $result = curl_exec($ch);
  if($result == 1){
    echo "<h2> Token válido. Acesso ao serviço de email</h2>";

  }else{
    echo "<h2><span> Token inválido. Sem acesso ao serviço de email</span></h2>";
  }
  curl_close($ch);
}
?>
<div class="container">
  <form action ="" method = "post">
    <label for="token">Por favor insira o token de autenticação para ter acesso ao serviço:</label>
    <textarea id="subject" name="token" placeholder="Token..." style="height:35px"></textarea>
    <input type="submit" value="Validar Token" name="validar">
  </form>
  <?php 
    if($result == 1){
      echo "<form action = \"servico2/email.php\" method=\"post\">
          <input type=\"submit\" value=\"Enviar Email\" name=\"submit\">
        </form>";
    }
    else if ($result == 0){
      echo "<form action = \"servico1\" method=\"post\">
          <input type=\"submit\" value=\"Autenticação\" name=\"submit\">
        </form>";
    }
    ?>
</div>
</body>
</html>