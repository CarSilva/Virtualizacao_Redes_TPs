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
      <title>Serviço de Autenticação</title>
      
      <style>
         body {
            padding-top: 40px;
            padding-bottom: 40px;
            background-color: #ADABAB;
         }
         
         .form-signin {
            max-width: 330px;
            padding: 15px;
            margin: 0 auto;
            color: #017572;
         }
         
         .form-signin .form-signin-heading,
         .form-signin .checkbox {
            margin-bottom: 10px;
         }
         
         .form-signin .checkbox {
            font-weight: normal;
         }
         
         .form-signin .form-control {
            position: relative;
            height: auto;
            -webkit-box-sizing: border-box;
            -moz-box-sizing: border-box;
            box-sizing: border-box;
            padding: 10px;
            font-size: 16px;
         }
         
         .form-signin .form-control:focus {
            z-index: 2;
         }
         
         .form-signin input[type="email"] {
            margin-bottom: -1px;
            border-bottom-right-radius: 0;
            border-bottom-left-radius: 0;
            border-color:#017572;
         }
         
         .form-signin input[type="password"] {
            margin-bottom: 10px;
            border-top-left-radius: 0;
            border-top-right-radius: 0;
            border-color:#017572;
         }
         .play_button {
            position:absolute;
            top: 50%;
            left: 37.9%;
            width: 215px
         }
         h2{
            text-align: center;
            color: #017572;
         }
         h4{
            text-align: center;
         }
      </style>
   </head>
   
   <body>
      <h2>Enter Username and Password</h2> 
      <h4>If you don't have an account just type the username and password you want</h4>
      <div class = "container form-signin">
         
         <?php
            $msg = '';
            $token = '';
            $login;

            if (isset($_POST['login']) && !empty($_POST['username']) 
               && !empty($_POST['password'])) {
               if (!isset($login[$_POST['username']])){
                  $login[$_POST['username']] = $_POST['password'];
                  echo 'User added with success';
                  $_SESSION['valid'] = true;
                  $_SESSION['timeout'] = time();
                  $_SESSION['username'] = $_POST['username'];
                  $token = bin2hex(random_bytes(128));
               }
               else if(!empty($login[$_POST['username']]) && $login[$_POST['username']] == $_POST['password']){
                  $_SESSION['valid'] = true;
                  $_SESSION['timeout'] = time();
                  $_SESSION['username'] = $_POST['username'];
                  $token = bin2hex(random_bytes(128));
                  echo 'You have entered valid use name and password';
               }else {
                  $msg = 'Wrong username or password';
               }
            }
         ?>
      </div> <!-- /container -->
      
      <div class = "container">
      
         <form class = "form-signin" role = "form" 
            action = "<?php echo htmlspecialchars($_SERVER['PHP_SELF']); 
            ?>" method = "post">
            <h2><?php print $msg; ?></h2>
            <input type = "text" class = "form-control" 
               name = "username" placeholder = "username" 
               required autofocus></br>
            <input type = "password" class = "form-control"
               name = "password" placeholder = "password" required>
            <input class = "play_button" type = "submit" value = "Login" name = "login">
         </form>  
      </div> 
      
   </body>
</html>