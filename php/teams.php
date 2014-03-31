<?php

    require_once('./lib.php');

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

    $id = new stdClass();
    $authUser = authenticateRequest($headers["Authentication"]);

        if($authUser != null)
        {
            $id = $authUser -> id;
            if($data != null && property_exists($data,"id"))
            {
             $id = $data -> id;
            }
            $isOwnData = ($authUser -> id == $id);
            $isAdmin = ($authUser -> role == 1);
            $isGameAdmin = ($authUser -> role == 2 || $authUser -> role == 1);
            $fullAccess = ($isOwnData || $isAdmin);

    switch($mode) {
        case "create":
            $result = createTeam($data,$authUser);
            break;
        case "get":
            $result = getTeam($params["id"], $authUser, $isAdmin, $isGameAdmin);
            break;
        case "getAll":
            $result = getTeams($isAdmin);
            break;
        case "set":
            $result = setTeam($data, $authUser, $isAdmin, $isGameAdmin);
            break;
        case "add":
            $result = addTeamMember($data);
            break;
    }
        }else{
            $result -> success = false;
            $result -> message = "Not Authorized";
        }
    echo json_encode($result);
?>