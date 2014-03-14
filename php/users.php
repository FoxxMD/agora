<?php

    require_once("./lib.php");


    $mode = $_POST["mode"];

    $data = file_get_contents('php://input');


    //$param1 = $_POST["param1"];

    if($mode == "get")  {
        $result = getUsers($param1);
    } else if($mode == "set") {
        $param2 = $_POST["param2"];
        $param3 = $_POST["param3"];
        $result = setUsers($param1, $param2, $param3);
    } else if($mode == "verify") {
        $result = verifyUser($data -> email,$data -> password);
    } else if($mode == "check") {
        $result = ifAlreadyLogged();
    } else if($mode == "logoff") {
        $result = logoff();
    } else if($mode == "delete") {
        $result = deleteUser($_POST["param1"]);
    }

    echo json_encode($result);
?>