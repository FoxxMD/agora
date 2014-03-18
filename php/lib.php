<?php
ini_set('display_errors', 0);
require_once("./crypto.php");
include 'ChromePhp.php'; //using for logging into chrome dev console because setting up an IDE would make too much sense


    function getDB() {
        $db = new mysqli("localhost:3306","matt","preparis", "gtgamefest_db"); //change to local environment's variables
        return $db;
    }

    function checkDuplicate($field, $value) {
        $db = getDB();
        $sql = "select * from users where ?=?";
        $statement = $db -> prepare($sql);
        $statement -> bind_param('ss',$field,$value);

        if($statement -> execute())
        {
            $result = $statement -> get_result();
            if($result -> num_rows == 0)
            {
                return false;
            }
            else{
                return true;
            }
        }
    }

    function getUser($param, $showAllData) {
        $db = getDB();
        $sql = "select * from users where id='".$param."'";
        if($result = $db -> query($sql))
        {
            if($result -> num_rows > 0)
            {
            $userObj = $result -> fetch_object();
                if(!$showAllData)
                {
                    $userObj -> password = null;
                    $userObj -> lock = null;
                    $userObj -> attempt = null;
                    $userObj -> salt = null;
                    $userObj -> email = null;
                    $userObj -> paid = null;
                    $userObj -> authtoken = null;
                    $userObj -> authExpire = null;
                }
                return $userObj;
            }
            else{
                return null;
            }
        }
    }
    function getUsers($showAllData)
    {
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
        }
        return $userArray;
    }

    function setUsers($param1, $param2, $param3, $isAdmin) {
        $db = getDB();
        $response = new stdClass();
        if(($param2 != "paid" && param2 != "email") || $isAdmin) {
            $sql = "update users set ".$param2."='".$param3."' where id='".$param1."'";
            if($result = $db -> query($sql))
            {
               $response -> success = true;
            }
            else{
                $response -> success = false;
                $response -> message = $db -> error;
            }
        } else {
            $response -> success = false;
            $response -> message = "Unauthorized";
        }
        return $response;
    }

    function authenticateRequest($authToken)
    {
         $db = getDB();
         $sql = "select * from users where authtoken='".$authToken."'";
         $user = new stdClass();
         if($result = $db -> query($sql))
         {
             if($result -> num_rows > 0)
            {
                 $userObj = $result -> fetch_object();
                 $user -> email = $userObj -> email;
                 $user -> id = $userObj -> id;
                 $user -> authtoken = $authToken;
                 $user -> role =  $userObj -> role;
                 $result -> close();
                 return $user;
            }
            else{
                return null;
            }
         }
         else{
            return null;
         }
    }

    function verifyUser($param1, $param2) {
        $db = getDB();
        $sqlTop = "select * from users where email='".$param1."' or alias='".$param1."'";

        $response = new stdClass();
        $response -> success = false;
        if($result = $db -> query($sqlTop))
        {
            if($result -> num_rows > 0)
            {
                $userObj = $result -> fetch_object();
                $result -> close();
                if($userObj -> attempt >= 5) {
                    $time = time();
                    if($time - strtotime($userObj -> locktime >= (60 * 15))) {

                        $sql = "update users set attempt=0 where email='".$param1."' or alias='".$param1."'";
                        $db -> query($sql);
                    } else {
                       $response -> success = false;
                       $response -> msg = "You have tried to login too many times.";
                       return $response;
                    }
                }
                if(!password_verify($param2, $userObj -> password)) {
                    $result = $db -> query($sqlTop);
                    $userObj = $result -> fetch_object();
                    $result -> close();

                    $sql = "update users set attempt=".($userObj -> attempt + 1)." where email='".$param1."' or alias='".$param1."'";
                    $db -> query($sql);
                    $result = $db -> query($sqlTop);
                    $userObj = $result -> fetch_object();
                    $result -> close();

                    if($userObj -> attempt >= 4) {
                        $time = time();
                        $sql = "update users set locktime=DATE_ADD(NOW(), INTERVAL 15 MINUTE) where email='".$param1."' or alias='".$param1."'";
                        $db -> query($sql);
                        $fp = fopen("log.txt",'a');
                        $content = $userObj -> email."   ".date("Y-m-d H:i:s")."   ".$_SERVER["REMOTE_ADDR"]."\n";
                        fwrite($fp, $content);
                        fclose($fp);
                    }
                    $response -> success = false;
                    $response -> message = "Username or Password incorrect";
                    return $response;
                }
                $authToken = getToken(40);
                        $sql = "update users set attempt=0,authtoken='".$authToken."',authExpire=DATE_ADD(NOW(),INTERVAL 1 DAY) where email='".$param1."' or alias='".$param1."'";
                        $db -> query($sql);
                        $result = $db -> query($sqlTop);
                        $userObj = $result -> fetch_object();
                        $result -> close();

                        $response -> success = true;
                        $response -> alias = $userObj -> alias;
                        $response -> id = $userObj -> id;
                        $response -> authtoken = $authToken;
                        $response -> role = $userObj -> role;
            }
            else{
                $response -> success = false;
                $response -> message = "Username or Password incorrect";
            }
        }
        else{
            $response -> message = "Could not perform query";
        }
          $db -> close();
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
            $response -> message = $db -> error;
        }
        return $response;
    }

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
            $response -> message = $db -> error;
        }
        return $response;
    }

    function getTeam($id) {
        $db = getdb();
        $sql = "select * from teams where id = ?";

        $statement = $db -> prepare($sql);
        $statement -> bind_param('i',$id);


        if($statement -> execute())
        {
            $result = $statement -> get_result();
            $teamObj = new stdClass();
            $teamObj = $result -> fetch_object();

         /*   $statement1 = $db -> prepare("CALL getTeamMemberNames(?)");
              $statement1 -> bind_param('i',$id); */

             $statement1 = $db -> prepare("CALL getTeamByIds(?,?,?,?,?)");
             $statement1 -> bind_param('iiiii',$teamObj -> captain ,$teamObj -> member1,$teamObj -> member2,$teamObj -> member3, $teamObj -> member4);

            if($statement1 -> execute())
            {
                $result = $statement1 -> get_result();
                $result = $result -> fetch_row();
                $teamObj -> captainName = $result[0];
                $teamObj -> member1Name = $result[1];
                $teamObj -> member2Name = $result[2];
                $teamObj -> member3Name = $result[3];
                $teamObj -> member4Name = $result[4];
            }
            else{
            ChromePhp::log($db -> error);
            }



        }
        return $teamObj;
    }

    function getTeams($showAllData){
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
    }

    function setTeams($data) {
        $db = getDB();
        $response = new stdClass();
        $sql = "update teams set ?=? where id=?";

        $statement = $db -> prepare($sql);
        $statement -> bind_param('ssi',$data -> param, $data -> paramValue, $data -> id);

        if($statement -> execute())
        {
            $response -> success = true;
        }
        else{
            $response -> success = false;
            $response -> message = $db -> error;
        }
        return $response;
    }

    function addTeamMember($data) {
        $db = getDB();
        $response = new stdClass();
        $sql = "select * from teams where id=?";

        $statement = $db -> prepare($sql);
        $statement -> bind_param('i',$data -> teamId);

        if($statement ->  execute())
        {
            $result = $statement -> get_result();
            $result = $result -> fetch_object();
            if($result -> password == "" || $result -> password == $data -> password) {
                $currSlot = 0;
                $memberProp = "";
                for($i = 1; $i <= 4; $i++) {
                $memberProp = "member".$i;
                    if($result -> $memberProp == 0) {
                        $currSlot = $i;
                        break;
                    }
                }
                if($currSlot == 0)
                {
                    $response -> success = false;
                    $response -> message = "No available slots";
                }
                $sql = "UPDATE teams SET ".$memberProp."=? where id=?";
                if($statement1 = $db -> prepare($sql))
                {
                    $statement1 -> bind_param('ii', $data -> id, $data -> teamId);

                    if($statement1 -> execute())
                    {
                        $response -> success = true;
                    }
                }
                else{
                ChromePhp::log($db -> error);
                }
            }
            else{
                $response -> success = false;
                $response -> message = "Password incorrect";
            }
        }
       else{
           $response -> success = false;
           $response -> message = $db -> error;
       }
       return $response;
    }
?>
