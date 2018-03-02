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

<h1>Serviço de email - Autenticação</h1>

<div class="container">
  <form action = "<?php echo htmlspecialchars($_SERVER['PHP_SELF']);
  ?>" method = "post">
    <label for="token">Por favor insira o token de autenticação para ter acesso ao serviço:</label>
    <input type="text" id="token" name="token" placeholder="Token...">
    <input type="submit" value="Submit" name="submit" formaction="email.php">
  </form>
</div>
<?php

?>
</body>
</html>
