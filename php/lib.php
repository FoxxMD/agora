<?php

    function getDB() {
        $db = mysql_connect("localhost","gtgamefe_beta","G=C?r.%Kd0np");
        mysql_select_db("gtgamefe_beta",$db);
        return $db;
    }

    function checkDuplicate($field, $value) {
        $db = getDB();
        $sql = "select * from users where ".$field."='".$value."'";
        $result = mysql_fetch_array(mysql_query($sql, $db));
        if($result != null)
            return true;
        return false;
    }

    function getUsers($param) {
        $db = getDB();
        if($param != null) {
            $sql = "select * from users where alias='".$param."' or email='".$param."'";
            $result = mysql_fetch_array(mysql_query($sql, $db));
            unset($result["password"]);
            return json_encode($result);
        } else {
            $result = array();
            $sql = "select * from users";
            $count = 0;
            $raw = mysql_query($sql, $db);
            while($curr = mysql_fetch_array($raw))  {
                unset($curr["password"]);
                $result[$count] = $curr;
                $count++;
            }
            return json_encode($result);
        }
    }

    function setUsers($param1, $param2, $param3) {
        $db = getDB();
        if($param1 != null) {
            $sql = "update users set ".$param2."='".$param3."' where alias='".$param1."' or email='".$param1."'";
            $result = mysql_query($sql);
            return "1";
        } else {
            $sql = "update users set ".$param2."='".$param3."'";
            $result = mysql_query($sql);
            return "1";
        }
    }

    function verifyUser($param1, $param2) {
        $db = getDB();
        $sql = "select * from users where email='".$param1."' or alias='".$param1."'";
        $result = mysql_fetch_array(mysql_query($sql));
        if($result == null || $result["password"] != $param2)
            return "0";
        setcookie("currentUser",$param1,time() + 7200);
        if($result["role"] == "1") {
            setcookie("currentAdmin",$param1, time() + 7200);
            return "2";
        }
        return "1";
    }


    function ifAlreadyLogged() {
        $result = array();
        if($_COOKIE["currentUser"] != null) {
            $result["user"] = $_COOKIE["currentUser"];
            if($_COOKIE["currentAdmin"] != null) {
                $result["code"] = 2;
            } else {
                $result["code"] = 1;
            }
        } else {
            $result["code"] = 0;
        }
        return json_encode($result);
    }

    function logoff() {
        setcookie("currentUser","",time() - 3600);
        setcookie("currentAdmin","",time() - 3600);
    }

    function deleteUser($user) {
        $db = getDB();
        $sql = "delete from users where email='".$user."'";
        mysql_query($sql, $db);
        return "1";
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
