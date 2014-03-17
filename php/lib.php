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
        $sql = "select * from users where ".$field."='".$value."'";
        if($result = $db -> query($sql))
        {
            if($result -> num_rows == 0)
            {
                return true;
            }
            else{
                return false;
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
        $sql = "delete from users where id='".$user."'";
        if($result = $db -> query(sql))
        {
            $response -> success = true;
        }
        else{
            $response -> success = false;
            $response -> message = $db -> error;
        }
        return $response;
    }

    function createTeam($name, $captain, $password, $game) {
        $db = getDB();
        if($password == null)
            $password = "";
        $sql = "insert into teams values (NULL, '".$name."','".$captain."','".$password."','','','".$game."','','','','')";
        mysql_query($sql, $db);
        return "1";
    }

    function getTeams($name, $captain) {
        $db = getdb();
        if($name != null) {
            $sql = "select * from teams where name = '".$name."'";
            $result = mysql_fetch_array(mysql_query($sql));
            return json_encode($result);
        } else if($captain != null) {
            $result = array();
            $count = 0;
            $sql = "select * from teams where captain='".$captain."'";
            $raw = mysql_query($sql);
            while($curr = mysql_fetch_array($raw)) {
                $result[$count] = $curr;
                $count++;
            }
            return json_encode($result);
        } else {
            $result = array();
            $count = 0;
            $sql = "select * from teams";
            $raw = mysql_query($sql);
            while($curr = mysql_fetch_array($raw)) {
                $result[$count] = $curr;
                $count++;
            }
            return json_encode($result);
        }
    }

    function setTeams($name, $captain, $key, $value) {
        $db = getDB();
        if($name != null) {
            $sql = "update teams set ".$key."='".$value."' where name='".$name."'";
            mysql_query($sql);
            return "1";
        } else if($captain != null) {
            $sql = "update teams set ".$key."='".$value."' where captain='".$captain."'";
            mysql_query($sql);
            return "1";
        }
    }

    function addTeamMember($name, $member, $password) {
        $db = getDB();
        $sql = "select * from teams where name='".$name."'";
        $result = mysql_fetch_array(mysql_query($sql));
        if($result["password"]== "" || $result["password"]==$password) {
            $currSlot = 0;
            for($i = 1; $i <= 4; $i++) {
                if($result["member".$i] == "") {
                    $currSlot = $i;
                    break;
                }
            }
            if($currSlot == 0)
                return "-1";
            $sql = "update teams set member".$currSlot."='".$member."' where name='".$name."'";
            mysql_query($sql);
            return "1";
        } else {
            return "0";
        }
    }
?>
