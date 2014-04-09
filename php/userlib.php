<?php
    require_once('./lib.php');

    function getUser($param, $showAllData) {
        $response = new stdClass();
        $response -> success = false;

        $db = getDB();
        if(!$showAllData)
        {
            $sql = "select id,alias,steam,bn,lol,xbox,ign from users where id=?";
        }
        else{
            $sql = "select id,email,alias,paid,entered,steam,bn,lol,xbox,ign,role,locktime,attempt,authtoken,authExpire,resetToken from users where id=?";
        }

        $statement = $db -> prepare($sql);
        $statement -> bind_param('i',$param);

        if($statement -> execute())
        {
        $statement -> store_result();
            if($statement -> num_rows > 0)
            {
                $userObj = new stdClass();
                if(!$showAllData)
                {
                    $statement -> bind_result($userObj -> id, $userObj -> alias, $userObj -> steam, $userObj -> bn, $userObj -> lol, $userObj -> xbox, $userObj -> ign);
                }
                else{
                    $statement -> bind_result($userObj -> id, $userObj -> email, $userObj -> alias, $userObj -> paid, $userObj -> entered, $userObj -> steam, $userObj -> bn, $userObj -> lol, $userObj -> xbox, $userObj -> ign, $userObj -> role, $userObj -> locktime, $userObj -> attempt, $userObj -> authtoken, $userObj -> authExpire, $userObj -> resetToken);
                }

                $statement -> fetch();
                $statement -> close();

                //$sql = "select ID, name from teams where captain=".$userObj -> id;
                $sql = "CALL getTeamsByCaptain(".$userObj -> id.")";
                if($result = $db -> query($sql))
                {
                    $captainInfo = new stdClass();
                    $count = 0;
                    $userObj -> captainList = array();
                    while($captainInfo = $result -> fetch_object())
                    {
                        $captainInfo -> members = array();
                        $captainInfo -> members[0] = $captainInfo -> member1;
                        $captainInfo -> members[1] = $captainInfo -> member2;
                        $captainInfo -> members[2] = $captainInfo -> member3;
                        $captainInfo -> members[3] = $captainInfo -> member4;
                        $userObj -> captainList[$count] = $captainInfo;
                        $count = ++$count;
                    }
                  if($db -> more_results())
                  {
                      $db -> next_result();
                  }
                }
                $sql = "select ID, name from teams where member1=".$userObj -> id." or member2=".$userObj -> id." or member3=".$userObj -> id." or member4=".$userObj -> id;
                if($result = $db -> query($sql))
                {
                    $memberInfo = new stdClass();
                    $count = 0;
                    $userObj -> memberList = array();
                    while($memberInfo = $result -> fetch_object())
                    {
                        $userObj -> memberList[$count] = $memberInfo;
                        $count = ++$count;
                    }
                }
                $sql = "CALL getTournamentsByUserId(".$userObj -> id.")";
                if($result = $db -> query($sql))
                {
                    $userObj -> tournaments = array();
                    $tourney = new stdClass();
                    $count = 0;
                    while($tourney = $result -> fetch_object())
                    {
                        $userObj -> tournaments[$count] = $tourney;
                        $count = ++$count;
                    }
                    $db -> next_result();
                }
                return $userObj;
            }
            else{
                $response -> message = "No user was found with the Id provided.";
            }
        } else {
            $response -> message = "There was a problem getting the user, please contact an administrator.";
        }
        return $response;
    }
    function getUsers($showAllData)
    {
        $response = new stdClass();
        $response -> success = false;

        $db = getDB();
        $userArray = array();
        $userObj = new stdClass();
        $sql = "select * from users";
        $count = 0;
        if($result = $db -> query($sql))
        {
            while($userObj = $result -> fetch_object())
            {
                if(!$showAllData)
                {
                    $userObj -> password = null;
                    $userObj -> lock = null;
                    $userObj -> attempt = null;
                    $userObj -> salt = null;
                    $userObj -> email = null;
                    $userObj -> paid = null;
                }
                $userArray[$count] = $userObj;
                $count++;
            }
        } else {
            $response -> message = "There was a problem getting users, please contact an administrator.";
            return $response;
        }
        return $userArray;
    }

    function setUsers($param1, $param2, $param3, $isAdmin) {
        $db = getDB();
        $response = new stdClass();
        $fixedParam = "";
            switch($param2)
            {
            case "alias":
                $fixedParam = "alias";
                break;
            case "lol":
                $fixedParam = "lol";
                break;
            case "bn":
                $fixedParam = "bn";
                break;
            case "xbox":
                $fixedParam = "xbox";
                break;
            case "ign":
                $fixedParam = "ign";
                break;
            case "steam":
                $fixedParam = "steam";
                break;
            }
            if($param2 == "paid" || $param2 == "email" || $param2 == "entered" && $isAdmin) {
                switch($param2)
                {
                case "paid":
                    $fixedParam = "paid";
                    break;
                case "email":
                    $fixedParam = "email";
                    break;
                case "entered":
                    $fixedParam = "entered";
                    break;
                }
            }
            else{
                $response -> success = false;
                $response -> message = "Unauthorized";
            }
            if($fixedParam != "")
            {
                $sql = "update users set ".$fixedParam."=? where id=?";

                $statement = $db -> prepare($sql);
                $statement -> bind_param('si', $param3, $param1);

                if($statement -> execute())
                {
                   $response -> success = true;
                }
                else{
                    $response -> success = false;
                    $response -> message = $db -> error;
                }
            }
            else{
                $response -> success = false;
                if(!property_exist($response, "message"))
                {
                    $response -> message = "A valid field was not provided.";
                }
            }

        return $response;
    }

    function deleteUser($user) {
        $db = getDB();
        $response = new stdClass();
        $sql = "delete from users where id=?";

        $statement = $db -> prepare($sql);
        $statement -> bind_param('i',$user);
        if($statement -> execute())
        {
            $response -> success = true;
        }
        else{
            $response -> success = false;
            error_log($db -> error);
            $response -> message = "Could not delete user, please contact an administrator.";
        }
        return $response;
    }

    function getTeamsCaptained($id) {
        $response = new stdClass();
        $response -> success = false;

        $sql = "CALL getTeamsByCaptain(?)";
        $statement = $db -> prepare($sql);
        $statement -> bind_param('i', $id);

        if(!$statement -> execute()){
            error_log($db -> error);
            $response -> message = "Error returning teams captained.";
            return $response;
        }

        $team = new stdClass();
        $teamArray = array();
        $count = 0;
        while($statement -> more_results)
        {
           $statement -> bind_result($team -> Id, $team -> Name, $team -> Game);
           $teamArray[$count] = $team;
           $count = ++$count;
           $statement -> next_result();
        }
        return $teamArray();
    }

?>