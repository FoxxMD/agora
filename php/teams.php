<?php

    require_once('./lib.php');

    $postData = file_get_contents('php://input');
    $data = json_decode($postData);

    parse_str($_SERVER['QUERY_STRING'], $params);
    $mode = $params["mode"];
    $headers = getallheaders();
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

    switch($mode) {
        case "create":
            $result = createTeam($data,$authUser);
            break;
        case "get":
            $result = getTeam($params["id"]);
            break;
        case "getAll":
            $result = getTeams();
            break;
        case "set":
            $result = setTeam($data);
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