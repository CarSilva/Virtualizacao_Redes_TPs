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
.container {
    border-radius: 5px;
    background-color: #f2f2f2;
    padding: 20px;
}
</style>
</head>
<body>

<h1>Serviço de email</h1>

<div class="container">
  <form action = "email.php" method = "post">
    <label for="email">Para:</label>
    <input type="text" id="email" name="email" placeholder="Email do destinatário...">

    <label for="assunto">Assunto:</label>
    <input type="text" id="assunto" name="assunto" placeholder="Assunto da mensagem...">

    <label for="subject">Mensagem:</label>
    <textarea id="mensagem" name="mensagem" placeholder="Mensagem..." style="height:200px"></textarea>

    <input type="submit" value="Submit" name="submit">
  </form>
</div>
<?php
require 'PHPMailerAutoload.php';
if(isset($_POST['submit'])) {
  $email = $_POST['email'];
  $assunto = $_POST['assunto'];
  $mensagem = $_POST['mensagem'];
  $mail = new PHPMailer;
  try {
    $mail->isSMTP();
    $mail->Host = 'mailServer';
    $mail->Port = 25;
    //$mail->SMTPAuth = false;
    //$mail->Username = 'g1';
    //$mail->Password = 'g1';
    $mail->SMTPDebug = 2;
    $mail->setFrom('g1@g1.gcom.di.uminho.pt');
    $mail->addAddress($email);
    $mail->Subject = $assunto;
    $mail->Body    = $mensagem;
    $mail->send();
  } catch (Exception $e) {
    echo 'Message could not be sent. Mailer Error: ', $mail->ErrorInfo;
  }
}
?>
</body>
</html>
