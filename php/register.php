<?php

ini_set('display_errors', 0);
//error_reporting(E_ALL);

    require_once("./lib.php");

    $data = file_get_contents('php://input');
    $user = json_decode($data);

    $db = getDB();

    if(!checkDuplicate("email", $user -> email) && !checkDuplicate("alias",$user -> alias)) {

        ChromePhp::log('going to create user');

        $response = new stdClass();

        $pwHash = password_hash($user -> password, PASSWORD_DEFAULT);
        $authToken = getToken(40);

        $sql = "insert into users values (NULL, '".$user -> email."','".$pwHash."','','".$user -> alias."','0','0','','','','','','0',NULL,0,'".$authToken."',DATE_ADD(NOW(),INTERVAL 1 DAY))";
        $result = mysql_query($sql, $db);

        if(!$result){
                ChromePhp::log('did not create user');
                error_log(mysql_error());
                //echo "0";
                $response -> success = false;
                $response -> message = mysql_error();
        }
        else{
                $response -> success = true;
                $response -> authtoken = $authToken;
                //echo "1";
        }
    } else
    {
        $response -> success = false;
        $response -> message = "Duplicate Email or Alias";
    }
        echo json_encode($response);
        //echo "0";
?>