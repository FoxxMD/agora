<?php

    require_once("lib.php");
    require_once("tourlib.php");
    require_once("teamlib.php");
    require_once("userlib.php");

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
    $result = new stdClass();
    $authUser = authenticateRequest($headers["Authentication"]);

    if($authUser != null && !property_exists($authUser, "success"))
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
        case "getTournamentInfo":
            $result = getTournamentInfo($params["tourneyId"]);
            break;

        case "getAllTournamentInfo":
            $result = getAllTournamentInfo();
            break;

        case "registerPlayer":

            if($data -> userId == $authUser -> id || $isGameAdmin)
            {
                $result = registerPlayer($data);
            }
            else {
                $result -> success = false;
                $result -> message = "Not Authorized";
            }
            break;

        case "registerTeam":

        $team = getTeam($data -> teamId, $authUser, $isAdmin, $isGameAdmin);
            if($team -> captain == $authUser -> id || $isGameAdmin)
            {
                $result = registerTeam($data);
            }
            else {
                $result -> success = false;
                $result -> message = "Not Authorized";
            }
            break;

        case "leaveTeam":
            $team = getTeam($data -> teamId, $authUser, $isAdmin, $isGameAdmin);
            if($team -> captain == $authUser -> id || $isGameAdmin)
            {
                $result = leaveTeam($data);
            }
            else {
                $result -> success = false;
                $result -> message = "Not Authorized";
            }
            break;

        case "leavePlayer":
            //if($data -> userId == $authUser -> id || $isGameAdmin)
            //{
                $result = leavePlayer($data);
            //}
            //else {
            //    $result -> success = false;
            //    $result -> message = "Not Authorized";
            //}
            break;

        case "makeTeamPresent":
            if($isGameAdmin)
            {
                $result = makeTeamPresent($data);
            }
            else {
                $result -> success = false;
                $result -> message = "Not Authorized";
            }
            break;

        case "makePlayerPresent":
            if($isGameAdmin)
            {
                $result = makePlayerPresent($data);
            }
            else {
                $result -> success = false;
                $result -> message = "Not Authorized";
            }
            break;

        case "setPlayers":
            if($isGameAdmin)
            {
                $result = setTournamentPlayers($data);
            }
            else {
                $result -> success = false;
                $result -> message = "Not Authorized";
            }
            break;

        case "setEntrantType":
            if($isGameAdmin)
            {
                $result = setTournamentEntrantType($data);
            }
            else {
                $result -> success = false;
                $result -> message = "Not Authorized";
            }
            break;

        case "setTourStatus":
            if($isGameAdmin)
            {
                $result = setTournamentStatus($data);
            }
            else {
                $result -> success = false;
                $result -> message = "Not Authorized";
            }
            break;

        case "getUsers":
                $result = getPlayersByTournament($params["tourneyId"]);
            break;

        case "getTeams":
                $result = getTeamsByTournament($params["tourneyId"]);
            break;
        }
    }else{
        $result = $authUser;
    }
    echo json_encode($result);
?>