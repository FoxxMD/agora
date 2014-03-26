<?php


//error_reporting(E_ALL);

    require_once("./lib.php");

    $data = file_get_contents('php://input');
    $user = json_decode($data);
    $response = new stdClass();
    $db = getDB();

    if(!checkDuplicate($user -> email)) {

        $phpassHash = new \Phpass\Hash;
        $pwHash = $phpassHash -> hashPassword($user -> password);
        $authToken = getToken(40);

        $sql = "insert into users values (NULL, ?,?,'',?,0,0,'','','','','',0,NULL,0,?,DATE_ADD(NOW(),INTERVAL 1 DAY),NULL)";

        $statement = $db -> prepare($sql);
        $statement -> bind_param('ssss',$user -> email, $pwHash, $user -> alias, $authToken);

        if($statement -> execute())
        {
            $response -> success = true;

            $subject = "Welcome to GT Gamefest!";
            $headers   = array();
            $headers[] = "MIME-Version: 1.0";
            $headers[] = "Content-type: text/html; charset=iso-8859-1";
            $headers[] = "From: GT Gamefest <noreply@gtgamefest.com>";
            $headers[] = "Subject: {$subject}";
            $headers[] = "X-Mailer: PHP/".phpversion();
            $headers.="Return-Path:<noreply@gtgamefest.com>";

            $body = "Hello New User,\r\n\r\n You have just registered for Georgia Tech's GameFest taking place April 19th-20th.\r\n\r\n To complete your registration please login at http://beta.gtgamefest.com and pay your registration fee, this will allow you to particiapte in any tournaments taking place during the event.\r\n\r\n See You There,\r\n-GT Gamefest Staff";

            if(!mail($user -> email, $subject, $body, implode("\r\n",$headers), "-f noreply@gtgamefest.com"))
            {
                error_log("Sending email was unsuccessful for ".$user -> email);
            }

            $response -> authtoken = $authToken;
        }
        else{
            $response -> success = false;
            $response -> message = $db -> error;
        }
    } else
    {
        $response -> success = false;
        $response -> message = "Duplicate email detected.";
    }
        echo json_encode($response);
?>