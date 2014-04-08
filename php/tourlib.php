<?php

    require_once("lib.php");

    function registerPlayer($data) {

            $db = getDB();

            $sql = "select * from tournaments where ID = ?";
            $statement = $db->prepare($sql);
            $statement->bind_param("i", $data -> tourId);

            $response = new stdClass();
            $response -> success = false;

            if($statement->execute()) {
//TODO add check to see if they are already registered.
                $statement -> store_result();
                if($statement -> num_rows > 0)
                {
                    $statement -> close();
                    $sql = "insert into tournament_users values (NULL, ?, ?, 0, 0)";
                    $statement2 = $db->prepare($sql);
                    $statement2->bind_param("ii",$data -> userId, $data -> tourId);

                    if($statement2->execute()) {
                        $response->success = true;
                    } else {
                        $response->message = $db->error;
                    }
                }
            } else {
                $response->message = $db->error;
            }

            return $response;
        }

        function registerTeam($data) {

            $db = getDB();

            $sql = "select * from tournaments where ID = ?";
            $statement = $db->prepare($sql);
            $statement->bind_param("i", $data -> tourId);

            $response = new stdClass();
            $response -> success = false;
//TODO add check to see if they are already registered.
            if($statement->execute()) {

                $statement -> store_result();
                if($statement -> num_rows > 0)
                {
                    $statement -> close();
                    $sql2 = "insert into tournament_teams values (NULL, ?, ?, 0)";
                    $statement2 = $db-> prepare($sql2);
                    $statement2-> bind_param("ii",$data -> teamId, $data -> tourId);

                    if($statement2->execute()) {
                        $response-> success = true;
                    } else {
                        $response->message = $db->error;
                    }
                }
                else{
                    $response -> message = "Tournament ID was not valid.";
                }
            }
            else {
            $response->message = $db->error;
        }

            return $response;
        }

        function leaveTeam($data) {

            $response = new stdClass();
            $response -> success = false;

            $db = getDB();
            $sql = "delete from tournament_teams where TournamentId=? and TeamId=?";
            $statement = $db -> prepare($sql);
            $statement -> bind_param('ii',$data -> tourId, $data -> teamId);

            if($statement -> execute()){
                $response -> success = true;
            }
            else{
                error_log("Error removing team ".$data -> teamId." from tournament ".$data -> tourId." : ".$db -> error);
                $response -> message = "There was a problem removing your team from this tournament, please contact an administrator.";
            }
            return $response;
        }

        function leavePlayer($data) {

            $response = new stdClass();
            $response -> success = false;

            $db = getDB();
            $sql = "delete from tournament_users where TournamentId=? and UserId=?";
            $statement = $db -> prepare($sql);
            $statement -> bind_param('ii', $data -> tourId, $data -> userId);

            if($statement -> execute()){
                $response -> success = true;
            }
            else{
                error_log("Error removing user ".$data -> userId." from tournament ".$data -> tourId." : ".$db -> error);
                $response -> message = "There was a problem removing you from this tournament, please contact an administrator.";
            }
            return $response;
        }

        function makePlayerPresent($data) {

            $response = new stdClass();
            $response -> success = false;

            $db = getDB();
            $sql = "update tournament_users set isPresent=1 where TournamentId=? and UserId=?";
            $statement = $db -> prepare($sql);
            $statement -> bind_param('ii', $data -> tourId, $data -> userId);

            if($statement -> execute()){
                $response -> success = true;
            }
            else{
                error_log("Error setting user ".$data -> userId." as present for tournament ".$data -> tourId." : ".$db -> error);
                $response -> message = "There was a problem setting this user as present, please contact an administrator.";
            }
            return $response;
        }

        function makeTeamPresent($data) {

            $response = new stdClass();
            $response -> success = false;

            $db = getDB();
            $sql = "update tournament_teams set isPresent=1 where TournamentId=? and TeamId=?";
            $statement = $db -> prepare($sql);
            $statement -> bind_param('ii',$data -> tourId, $data -> teamId);

            if($statement -> execute()){
                $response -> success = true;
            }
            else{
                error_log("Error setting team ".$data -> teamId." present for tournament ".$data -> tourId." : ".$db -> error);
                $response -> message = "There was a problem setting this team present, please contact an administrator.";
            }
            return $response;
        }

        function getPlayersByTournament($tourneyID) {

            $db = getDB();

            $statement = $db -> prepare("CALL getUsersByTournament(?)");
            $statement->bind_param("i", $tourneyID);

            $tourneyPlayersArray = array();
            $count = 0;

            if($statement->execute()) {

                $user = new stdClass();
                $statement->bind_result($user -> id, $user -> alias, $user -> steam, $user -> lol, $user -> xbox, $user -> ign, $user -> role, $user -> isAdmin, $user -> isPresent);
                while($statement->fetch())
                {
                    $tourneyPlayersArray[$count] = $user;
                    $count = ++$count;
                }

                return $tourneyPlayersArray;
            }
        }

        function getTeamsByTournament($tourneyID) {

            $db = getDB();

            $statement = $db -> prepare("CALL getTeamsByTournament(?)");
            $statement->bind_param("i", $tourneyID);

            $tourneyTeamsArray = array();
            $count = 0;

            if($statement->execute()) {

                $team = new stdClass();
                $statement->bind_result($team -> id, $team -> name, $team -> captain, $team -> isPresent);
                while($statement->fetch())
                {
                    $tourneyTeamsArray[$count] = $team;
                    $count = ++$count;
                }

                return $tourneyTeamsArray;
            }
        }
        function getTournamentInfo($tourId) {

            $db = getDB();
            $tour = new stdClass();
            $info = new stdClass();
            $response = new stdClass();
            $response -> success = false;

            $statement = $db -> prepare("CALL getTournamentInfo(?)");
            $statement->bind_param("i", $tourId);

            if($statement -> execute()) {
                $statement -> store_result();
                $statement -> bind_result($info -> Id, $info -> Game, $info -> Name, $info -> isPlaying, $info -> jsonName, $info -> teamCount, $info -> playerCount);
                $statement -> fetch();
                $statement -> close();

                $tour -> info = $info;

               $sql = "CAll getUsersByTournament(".$info -> Id.")";
                if($result = $db -> query($sql))
                {
                    $tour -> users = array();
                    $count = 0;
                    while($user = $result -> fetch_object())
                      {
                        $tour -> users[$count] = $user;
                        $count = ++$count;
                      }
                      $db -> next_result();
                }
                else{
                    error_log($db -> error." at line 1023");
                }

                $sql = "CALL getTeamsByTournament(".$info -> Id.")";
                if($result = $db -> query($sql))
                {
                    $tour -> teams = array();
                    $count = 0;
                    while($team = $result -> fetch_object())
                      {
                        $tour -> teams[$count] = $team;
                        $count = ++$count;
                      }
                      $db -> next_result();
                }
                else{
                    error_log($db -> error." at line 1041");
                }
                return $tour;
            }
            else{
                error_log($db -> error);
                $response -> message = "There was a problem retrieving information for this tournament.";
                return $response;
            }
        }
        function getAllTournamentInfo() {

            $db = getDB();
            $tourArray = array();
            $response = new stdClass();
            $response -> success = false;

            $sql = "CALL getAllTournamentInfo()";
            if($result = $db -> query($sql))
            {
                $count = 0;
                while($tour = $result -> fetch_object())
                {
                    $tourArray[$count] = $tour;
                    $count = ++$count;
                }
            }
            else{
                $response -> message = "Failed to get all tournament info.";
                return $response;
            }
            return $tourArray;
        }

?>