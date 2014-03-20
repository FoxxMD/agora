<?php

    require_once("./lib.php");

    $postData = file_get_contents('php://input');
    $data = json_decode($postData);

    parse_str($_SERVER['QUERY_STRING'], $params);
    $mode = $params["mode"];

    $headers = array();
    foreach ($_SERVER as $key => $value) {
        if (strpos($key, 'HTTP_') === 0) {
            $chunks = explode('_', $key);
            $header = '';
            for ($i = 1; $y = sizeof($chunks) - 1, $i < $y; $i++) {
                $header = ucfirst(strtolower($chunks[$i]));
            }
            $header = ucfirst(strtolower($chunks[$i]));
            $headers[$header] = $value;
        }
    }

    if($mode == "verify") {
        $result = verifyUser($data -> email,$data -> password);
    }
    else {
        $authUser = authenticateRequest($headers["Authentication"]);
        if($authUser != null)
        {
            $id = $authUser -> id;
            if($data != null)
            {
             $id = $data -> id;
            }
            $isOwnData = ($authUser -> id == $id);
            $isAdmin = ($authUser -> role == 1);
            $fullAccess = ($isOwnData || $isAdmin);

            if($mode == "get")  {
                if(array_key_exists("id", $params))
                {
                    $id = $params["id"];
                }
                $result = getUser($id, $fullAccess);
            } else if($mode == "set" && $fullAccess) {
                $result = setUsers($data -> id, $data -> param, $data -> updatevalue, $isAdmin);
            } else if($mode == "delete" && $fullAccess) {
                $result = deleteUser($id);
            } else if($mode == "getAll") {
                $result = getUsers($isAdmin);
            }
        }
        else {
            $result -> success = false;
            $result -> message = "Not Authorized";
        }
    }
    echo json_encode($result);
?>