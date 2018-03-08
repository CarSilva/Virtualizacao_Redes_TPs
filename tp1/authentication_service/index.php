<?php
   ob_start();
   session_start();
?>

<?
?>

<html lang = "pt">

   <head>

      <link rel="stylesheet" type="text/css" href="style.css">
      <title>Serviço de Autenticação</title>
   </head>

   <body>
      <h1>Serviço de Autenticação</h1>
      <div class = "container form-signin">
         <?php
            include "database.php";
            $token = '';
            $conn = get_connection();
            $table = select_table($conn);
            if(isset($_POST['login']) && !empty($_POST['username']) && !empty($_POST['password'])){
              $exists = exists_name($table, $_POST['username'], $_POST['password']);

              if($exists == -2){
                $token = bin2hex(random_bytes(32));
                insert_into_table($conn, $token, $_POST['username'], $_POST['password']);
                echo "<h2>New user! Now registered</h2>";
              }
              else if($exists == -1){
                echo "<h2><span>User exists, but wrong password, try again</span></h2>";
                $token = "Please try again";
              }
              else{
                $token = $exists;
              }
            }
         ?>
      </div> <!-- /container -->

      <div class = "container">
         <form action = "servico1" method = "post">
            <label for="fname">Username</label>
            <input type="text" id="fname" name="username" placeholder="Username...">
            <label for="lname">Password</label>
            <input type="text" id="lname" name="password" placeholder="Password...">
            <input type="submit" value="Login" name="login">
         </form>
      </div>
         <div class = "container">
         <form action = "servico2" method = "post">
            <label for="subject">Token:</label>
            <textarea id="subject" name="mensagem" placeholder="Token..." style="height:35px"><?php echo $token; ?></textarea>
            <input type="submit" value="Serviço de Email" name="email">
         </form>
      </div>

   </body>
</html>
