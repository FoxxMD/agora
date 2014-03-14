<?php

ini_set('display_errors', 1);
error_reporting(E_ALL);

    require_once("./lib.php");

    $data = file_get_contents('php://input');
    $user = json_decode($data);

    $db = getDB();

    if(!checkDuplicate("email", $user -> email) && !checkDuplicate("alias",$user -> alias)) {

        ChromePhp::log('going to create user');

        $sql = "insert into users values (NULL, '".$user -> email."','".$user -> password."','','".$user -> alias."','0','0','','','','','','0',NULL,0)";
        $result = mysql_query($sql, $db);

        if(!$result){
                ChromePhp::log('did not create user');
                error_log(mysql_error());
                echo "0";
        }
        else{
                echo "1";
        }
    } else
        echo "0";
?>