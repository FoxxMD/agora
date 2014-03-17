<?php

    require_once("./lib.php");

    $postData = file_get_contents('php://input');
    $data = json_decode($postData);

    parse_str($_SERVER['QUERY_STRING'], $params);
    $mode = $params["mode"];
    $headers = getallheaders();

    if($mode == "verify") {
        $result = verifyUser($data -> email,$data -> password);
    }
    else {
        $authUser = authenticateRequest($headers["Authentication"]);
        if($authUser != null)
        {
        ChromePhp::log(var_dump($authUser));
        ChromePhp::log(var_dump($data));
            $isOwnData = ($authUser -> id == $data -> id);
            $isAdmin = ($authUser -> role == 1);
            $fullAccess = ($isOwnData || $isAdmin);

            if($mode == "get")  {
                $result = getUser($params["id"], $fullAccess);
            } else if($mode == "set" && $fullAccess) {
                $result = setUsers($data -> id, $data -> param, $data -> updatevalue);
            } else if($mode == "delete" && $fullAccess) {
                $result = deleteUser($data -> id);
            } else if($mode == "getAll") {
                $result = getUsers($isAdmin);
            } else if($mode == "logoff") {
                $result = logoff();
            }
        }
        else {
            $result -> success = false;
            $result -> message = "Not Authorized";
        }
    }
    echo json_encode($result);
?>