<html>
 <head>
  <title>PHP Test</title>
 </head>
 <body>
 </body>
</html>

<?php
require 'PHPMailerAutoload.php';

$mail = new PHPMailer;                              // Passing `true` enables exceptions
//try {
    //Server settings
    $mail->SMTPDebug = 2;                                 // Enable verbose debug output
    $mail->isSMTP();                                      // Set mailer to use SMTP
    $mail->Host = 'mail';                          // Specify main and backup SMTP servers
    //$mail->SMTPAuth = false;                              // Enable SMTP authentication
    //$mail->Username = 'g1';                               // SMTP username
    //$mail->Password = 'g1';                               // SMTP password
    //$mail->SMTPSecure = 'tls';                          // Enable TLS encryption, `ssl` also accepted
    $mail->Port = 25;                                     // TCP port to connect to

    //Recipients
    $mail->setFrom('g1@g1.gcom.di.uminho.pt');
    $mail->addAddress('pedronyk1996@hotmail.com');        // Add a recipient
    //$mail->addReplyTo('info@example.com', 'Information');
    //$mail->addCC('cc@example.com');
    //$mail->addBCC('bcc@example.com');

    //Attachments
    //$mail->addAttachment('/var/tmp/file.tar.gz');         // Add attachments
    //$mail->addAttachment('/tmp/image.jpg', 'new.jpg');    // Optional name

    //Content
    //$mail->isHTML(true);                                  // Set email format to HTML
    $mail->Subject = 'Here is the subject';
    $mail->Body    = 'This is the HTML message body <b>in bold!</b>';
    $mail->AltBody = 'This is the body in plain text for non-HTML mail clients';

    $mail->send();
    echo 'Message has been sent';
//} catch (Exception $e) {
  //  echo 'Message could not be sent. Mailer Error: ', $mail->ErrorInfo;
//}
?>
