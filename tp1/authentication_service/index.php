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
            font-family: Arial, Helvetica, sans-serif;
            padding-top: 40px;
            padding-bottom: 40px;
            background-color: #ADABAB;
         }
         
         
         input[type=text], select {
             width: 25%;
             padding: 12px;
             border: 1px solid #ccc;
             border-radius: 4px;
             box-sizing: border-box;
             margin-top: 6px;
             margin-bottom: 16px;
             resize: vertical;
         }
         textarea{
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
      <h1>Serviço de Autenticação</h1> 
      <div class = "container form-signin">
         <?php
            $msg = '';
            $token = '';
            $login;
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

         <!--<form class = "form-signin" role = "form" 
            action = "<?php// echo htmlspecialchars($_SERVER['PHP_SELF']); 
            ?>" method = "post">
            <h2><?php //print $msg; ?></h2>
            <input type = "text" class = "form-control" 
               name = "username" placeholder = "username" 
               required autofocus></br>
            <input type = "password" class = "form-control"
               name = "password" placeholder = "password" required>
            <input class = "play_button" type = "submit" value = "Login" name = "login">
         -->   
         </form>  
      </div> 
      
   </body>
</html>