<?php


//error_reporting(E_ALL);

    require_once("./lib.php");

    $data = file_get_contents('php://input');
    $user = json_decode($data);
    $response = new stdClass();
    $db = getDB();

    if(!checkDuplicate("email", $user -> email) && !checkDuplicate("alias",$user -> alias)) {

        $phpassHash = new \Phpass\Hash;
        $pwHash = $phpassHash -> hashPassword($user -> password);
        $authToken = getToken(40);

        $sql = "insert into users values (NULL, ?,?,'',?,0,0,'','','','','',0,NULL,0,?,DATE_ADD(NOW(),INTERVAL 1 DAY))";

        $statement = $db -> prepare($sql);
        $statement -> bind_param('ssss',$user -> email, $pwHash, $user -> alias, $authToken);

        if($statement -> execute())
        {
            $response -> success = true;
            $response -> authtoken = $authToken;
        }
        else{
            $response -> success = false;
            $response -> message = $db -> error;
        }
    } else
    {
        $response -> success = false;
        $response -> message = "Duplicate Email or Alias";
    }
        echo json_encode($response);
?>