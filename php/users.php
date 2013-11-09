<?php

    require_once("./lib.php");

    $mode = $_POST["mode"];
    $param1 = $_POST["param1"];

    if($mode == "get")  {
        $result = getUsers($param1);
    } else if($mode == "set") {
        $param2 = $_POST["param2"];
        $param3 = $_POST["param3"];
        $result = setUsers($param1, $param2, $param3);
    } else if($mode == "verify") {
        $result = verifyUser($_POST["param1"],$_POST["param2"]);
    }

    echo $result;
?>