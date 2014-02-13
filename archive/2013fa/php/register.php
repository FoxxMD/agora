<?php

    require_once("./lib.php");

    $alias = $_POST["alias"];
    $email = $_POST["email"];
    $pw    = $_POST["password"];

    $db = getDB();

    if(!checkDuplicate("email", $email) && !checkDuplicate("alias",$alias)) {
        $sql = "insert into users values (NULL, '".$email."','".$pw."','".$alias."','0','0','','','','','','0',NULL,0)";
        $result = mysql_query($sql, $db);
        echo "1";
    } else
        echo "0";
?>