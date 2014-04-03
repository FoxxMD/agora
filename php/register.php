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

        $sql = "insert into users values (NULL, ?,?,'',?,0,0,'','','','','',0,NULL,0,?,UNIX_TIMESTAMP(DATE_ADD(NOW(),INTERVAL 1 DAY)),NULL)";

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

            $body = "Hello New User,\r\n\r\n You have just registered for Gamefest, which will take place on April 19-20, 2014.\r\n\r\n If you would like to compete in any tournaments at the event, you will need to pay the $15 fee online at https://www.gtgamefest.com or at the registration desk at the event. Free play is free.\r\n\r\n See You There,\r\nGamefest Staff";

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
