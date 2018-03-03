<?php
   ob_start();
   session_start();
  
?>

<?
   // error_reporting(E_ALL);
   // ini_set("display_errors", 1);
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
            $msg = '';
            $token = '';
            $login;
            $dbuser = postgres;
            $dbpass = postgres;
            try {
              $conn = new PDO("pgsql:host=db;port=5432;dbname=auth", $dbuser, $dbpass);
            } catch (PDOException $e) {
              die('Connection failed: ' . $e->getMessage());
            }
            if (isset($_POST['login']) && !empty($_POST['username']) && !empty($_POST['password'])) {
               if (!isset($login[$_POST['username']])){
                  $login[$_POST['username']] = $_POST['password'];
                  echo 'User added with success';
                  $_SESSION['valid'] = true;
                  $_SESSION['timeout'] = time();
                  $_SESSION['username'] = $_POST['username'];
                  $token = bin2hex(random_bytes(32));

               }
               else if(!empty($login[$_POST['username']]) && $login[$_POST['username']] == $_POST['password']){
                  $_SESSION['valid'] = true;
                  $_SESSION['timeout'] = time();
                  $_SESSION['username'] = $_POST['username'];
                  $token = bin2hex(random_bytes(32));
                  echo 'You have entered valid use name and password';
               }else {
                  $msg = 'Wrong username or password';
               }
            }
         ?>
      </div> <!-- /container -->
      
      <div class = "container">
         
         <form action = "<?php echo htmlspecialchars($_SERVER['PHP_SELF']);
           ?>" method = "post">
            <label for="fname">Username</label>
            <input type="text" id="fname" name="username" placeholder="Username...">
            <label for="lname">Password</label>
            <input type="text" id="lname" name="password" placeholder="Password...">
            <input type="submit" value="Login" name="login">
         </form>
      </div>
         <div class = "container">
         <form action = "<?php echo htmlspecialchars($_SERVER['PHP_SELF']);
           ?>" method = "post">
            <label for="subject">Token:</label>
            <textarea id="subject" name="mensagem" placeholder="Token..." style="height:35px"><?php echo $token; ?></textarea>  
         </form>  
      </div> 
      
   </body>
</html>