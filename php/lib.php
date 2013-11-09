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
            return json_encode($result);
        } else {
            $result = array();
            $sql = "select * from users";
            $count = 0;
            $raw = mysql_query($sql, $db);
            while($curr = mysql_fetch_array($raw))  {
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
?>
