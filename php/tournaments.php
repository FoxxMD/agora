<?php

    require_once("lib.php");

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
        //TODO add getting tournament information or flag for returning tournmanet information with some other request
            case "registerPlayer":
                if($fullAccess || $isGameAdmin)
                {
                    $result = registerPlayer($data);
                }
                else {
                    $result -> success = false;
                    $result -> message = "Not Authorized";
                }
                break;

            case "registerTeam":
                if($fullAccess || $isGameAdmin)
                    $result = registerTeam($data);
                else {
                    $result -> success = false;
                    $result -> message = "Not Authorized";
                }
                break;
            //TODO add leaving tournament as player or team
            case "getUsers":
                if($isAdmin || $isGameAdmin)
                    $result = getTourneyPlayers($params["tourneyId"]);
                else {
                    $result -> success = false;
                    $result -> message = "Not Authorized";
                }
                break;

            case "getTeams":
                if($isAdmin || $isGameAdmin)
                    $result = getTourneyTeams($params["tourneyId"]);
                else {
                    $result -> success = false;
                    $result -> message = "Not Authorized";
                }
                break;
        }
    }else{
        $result -> success = false;
        $result -> message = "Not Authorized";
    }
    echo json_encode($result);
?>