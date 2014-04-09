<?php

    require_once('./lib.php');

    function createTeam($data, $user) {
        $db = getDB();
        $response = new stdClass();

        $sql = "insert into teams values (NULL, ?,?,?,?,'',?,0,0,0,0)";

        $statement = $db -> prepare($sql);
        $statement -> bind_param('sisss', $data -> name, $user -> id, $data -> password, $data -> des, $data -> game);
        if($statement -> execute())
        {
            $response -> success = true;
        }
        else{
            $response -> success = false;
            error_log($db -> error);
            $response -> message = "There was a problem creating your team, please ensure all information is correctly entered. If this problem persists contact an administrator.";
        }
        return $response;
    }

    function getTeam($id, $authUser, $isAdmin, $isGameAdmin) {
        $response = new stdClass();
        $response -> success = false;

        $db = getdb();
        $sql = "select * from teams where ID=?";

        $statement = $db -> prepare($sql);
        $statement -> bind_param('i',$id);
        $teamObj = new stdClass();

        if($statement -> execute())
        {
            $statement -> store_result();
            if($statement -> num_rows == 0)
                {
                    $response -> message = "No team found with that Team Id!";
                    return $response;
                }

            $statement -> bind_result($teamObj -> ID, $teamObj -> name, $teamObj -> captain, $teamObj -> password, $teamObj -> des, $teamObj -> logo, $teamObj -> game, $teamObj -> member1, $teamObj -> member2, $teamObj -> member3, $teamObj -> member4);
            $statement -> fetch();
            if($teamObj -> captain != $authUser -> id && (!$isAdmin && !$isGameAdmin))
            {
                $teamObj -> password = null;
            }
            $statement -> close();

            $statement1 = $db -> prepare("CALL getTeamByIds(?,?,?,?,?)");
            $statement1 -> bind_param('iiiii',$teamObj -> captain ,$teamObj -> member1,$teamObj -> member2,$teamObj -> member3, $teamObj -> member4);

            if($statement1 -> execute())
            {
                $statement1 -> bind_result($teamObj -> captainName, $teamObj -> member1Name, $teamObj -> member2Name, $teamObj -> member3Name, $teamObj -> member4Name);
                $statement1 -> fetch();
            }
            else{
                error_log("Error making a call to stored proc".$db -> error);
            }
            $statement1 -> close();
            $sql = "CALL getTournamentsByTeamId(".$teamObj -> ID.")";
            if($result = $db -> query($sql))
            {
                $teamObj -> tournaments = array();
                $count = 0;
                while($tour = $result -> fetch_object())
                {
                    $teamObj -> tournaments[$count] = $tour;
                    $count = ++$count;
                }
                $db -> next_result();
            }
            else{
                error_log("Error getting tournaments team is registered for: ".$db -> error);
            }
        }
        else{
            error_log($db -> error);
            $response -> message = "There was a problem getting the team, please contact an administrator.";
            return $response;
        }
        return $teamObj;
    }

    function getTeams($showAllData){
        $response = new stdClass();
        $response -> success = false;

        $db = getdb();
        $teamArray = array();
        $teamObj = new stdClass();
        $count = 0;
        $sql = "select * from teams";
        if($result = $db -> query($sql))
        {
            while($teamObj = $result -> fetch_object())
            {
                if(!$showAllData)
                {
                    $teamObj -> password = null;
                }
                $teamArray[$count] = $teamObj;
                $count++;
            }
                return $teamArray;
        }
        else{
            $response -> message = "Could not get teams, please contact an administrator.";
            return $response;
        }
    }

    function setTeam($data, $authUser, $isAdmin, $isGameAdmin) {
        $db = getDB();
        $response = new stdClass();
        $response -> success = false;

        $sql = "select captain, member1, member2, member3, member4 from teams where id=?";
        $statement1 = $db -> prepare($sql);
        $statement1 -> bind_param('i', $data -> teamId);
        if($statement1 -> execute())
        {
            $statement1 -> bind_result($captainId, $member1Id, $member2Id, $member3Id, $member4Id);
            $statement1 -> fetch();
            $statement1 -> close();
            $fixedParam = "";

            if($authUser -> id == $captainId || $isAdmin || $isGameAdmin)
            {
                switch($data -> param)
                {
                case "name":
                    $fixedParam = "name";
                    break;
                case "password":
                    $fixedParam = "password";
                    break;
                case "des":
                    $fixedParam = "des";
                    break;
                case "game":
                    $fixedParam = "game";
                    break;
                }
            }
            switch($data -> param)
            {
                //THIS IS CRAP
                //needs a more elegant solution ;__;

                case "member1":
                    if($authUser -> id == $captainId || $authUser -> id == $member1Id || $isAdmin || $isGameAdmin)
                    {
                        $fixedParam = "member1";
                    }else{
                        $response -> message = "Not authorized to make this change";
                        return $response;
                    }
                    break;
                case "member2":
                    if($authUser -> id == $captainId || $authUser -> id == $member2Id || $isAdmin || $isGameAdmin)
                    {
                        $fixedParam = "member2";
                    }else{
                        $response -> message = "Not authorized to make this change";
                        return $response;
                    }
                    break;
                case "member3":
                    if($authUser -> id == $captainId || $authUser -> id == $member3Id || $isAdmin || $isGameAdmin)
                    {
                        $fixedParam = "member3";
                    }else{
                        $response -> message = "Not authorized to make this change";
                        return $response;
                    }
                    break;
                case "member4":
                    if($authUser -> id == $captainId || $authUser -> id == $member4Id || $isAdmin || $isGameAdmin)
                    {
                        $fixedParam = "member4";
                    }else{
                        $response -> message = "Not authorized to make this change";
                        return $response;
                    }
                    break;
            }
            if($fixedParam == "")
            {
                $response -> message = "The field name provided was not valid.";
                return $response;
            }
            $sql = "update teams set ".$fixedParam."=? where id=?";
            if($statement = $db -> prepare($sql))
            {
                $statement -> bind_param('si', $data -> updatevalue, $data -> teamId);

                if($statement -> execute())
                {
                    $response -> success = true;
                }
                else{
                    $response -> message = "Could not update field, please contact an administrator.";
                    error_log($db -> error);
                }
            }
            else{
                $response -> message = "Could not update field, please contact an administrator.";
                error_log($db -> error);
            };
        }
        else{
            $response -> message = "Team Id was not valid, please contact an administrator.";
            error_log($db -> error);
        }
        return $response;
    }

    function addTeamMember($data, $authUser) {
        $db = getDB();
        $response = new stdClass();
        $response -> success = false;

        $sql = "select password,member1,member2,member3,member4,captain from teams where id=?";

        $statement = $db -> prepare($sql);
        $statement -> bind_param('i',$data -> teamId);

        if($statement ->  execute())
        {
            $teamObj = new stdClass();
            $statement -> bind_result($teamObj -> password, $teamObj -> member1, $teamObj -> member2, $teamObj -> member3, $teamObj -> member4, $teamObj -> captain);
            $statement -> fetch();
            $statement -> close();

            if($teamObj -> password == "" || $teamObj -> password == $data -> password || $authUser -> id == $teamObj -> captain) {
                $currSlot = 0;
                $memberProp = "";
                for($i = 1; $i <= 4; $i++) {
                $memberProp = "member".$i;
                    if($teamObj -> $memberProp == 0) {
                        $currSlot = $i;
                        break;
                    }
                }
                if($currSlot == 0)
                {
                    $response -> message = "No available slots on this team!";
                }
                $sql = "UPDATE teams SET ".$memberProp."=? where id=?";
                if($statement1 = $db -> prepare($sql))
                {
                    $statement1 -> bind_param('ii', $data -> id, $data -> teamId);

                    if($statement1 -> execute())
                    {
                        $response -> success = true;
                    }
                    else{
                        $response -> message = "Joining team failed due to powers beyond your control. Please contact an administrator.";
                        error_log($db -> error);
                    }
                }
                else{
                    $response -> message = "Joining team failed due to powers beyond your control. Please contact an administrator.";
                    error_log($db -> error);
                }
            }
            else{
                $response -> message = "Team password is incorrect.";
            }
        }
       else{
           error_log($db -> error);
           $response -> message = "Joining team failed due to powers beyond your control. Please contact an administrator.";
       }
       return $response;
    }

    function deleteTeam($id, $authUser, $isAdmin, $isGameAdmin) {
        $db = getDB();
        $response = new stdClass();
        $response -> success = false;

        $sql = "select captain from teams where id=?";
        $statement1 = $db -> prepare($sql);
        $statement1 -> bind_param('i', $id);
        if($statement1 -> execute())
        {
            $statement1 -> store_result();
            if($statement -> num_rows == 0)
                {
                    $response -> message = "No team found with that Team Id!";
                    return $response;
                }

            $statement1 -> bind_result($captain);
            $statement1 -> fetch();
            $statement1 -> close();

            if($authUser -> id == $captain || $isAdmin || $isGameAdmin)
            {
                $sql = "delete from teams where id=?";

                $statement = $db -> prepare($sql);
                $statement -> bind_param('i', $id);
                if($statement -> execute())
                {
                    $response -> success = true;
                }
                else{
                    error_log($db -> error);
                    $response -> message = "There was a problem deleting this team";
                }
            }
            else{
                $response -> message = "You are not authorized to delete a team!";
            }

        }
        else{
            $response -> message = $db -> error;
            error_log($db -> error);
        }
        return $response;
    }

?>