<?php
ini_set('display_errors', 0);

require_once("./crypto.php"); //for token authentication
require_once("./Phpass.php"); //for password hashing/verification

include 'ChromePhp.php'; //using for logging into chrome dev console because setting up an IDE would make too much sense


    function getDB() {
        //$db = new mysqli("localhost:3306","matt","preparis", "gtgamefest_db"); //for my local
        $db = new mysqli("localhost","gtgamefe_beta","G=C?r.%Kd0np", "gtgamefe_beta"); //for beta
        return $db;
    }

    function checkDuplicate($field, $value) {
        $db = getDB();
        $sql = "select * from users where ?=?";
        $statement = $db -> prepare($sql);
        $statement -> bind_param('ss',$field,$value);

        if($statement -> execute())
        {
            $statement -> store_result();
            if($statement -> num_rows == 0)
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
        if(!$showAllData)
        {
            $sql = "select id,alias,steam,bn,lol,xbox,ign from users where id=?";
        }
        else{
            $sql = "select id,email,alias,paid,entered,steam,bn,lol,xbox,ign,role,locktime,attempt from users where id=?";
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
                    $statement -> bind_result($userObj -> id, $userObj -> email, $userObj -> alias, $userObj -> paid, $userObj -> entered, $userObj -> steam, $userObj -> bn, $userObj -> lol, $userObj -> xbox, $userObj -> ign, $userObj -> role, $userObj -> locktime, $userObj -> attempt);
                }

                $statement -> fetch();

               /* if(!$showAllData)
                {
                    $userObj -> password = null;
                    $userObj -> lock = null;
                    $userObj -> attempt = null;
                    $userObj -> salt = null;
                    $userObj -> email = null;
                    $userObj -> paid = null;
                    $userObj -> authtoken = null;
                    $userObj -> authExpire = null;
                } */
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
            $sql = "update users set ?=? where id=?";

            $statement = $db -> prepare($sql);
            $statement -> bind_param('ssi', $param1, $param2, $param1);

            if($statement -> execute())
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
         $sql = "select email,id,role from users where authtoken=?";
         $statement = $db -> prepare($sql);
         $statement -> bind_param('s',$authToken);

         $user = new stdClass();
         if($statement -> execute())
         {
            $statement -> store_result();
            if($statement -> num_rows > 0)
            {
                 $userObj = new stdClass();
                 $statement -> bind_result($userObj -> email,$userObj -> id, $userObj -> role);
                 $statement -> fetch();
                 return $userObj;
            }
            else{
                return null;
            }
         }
         else{
         ChromePhp::log($db -> error);
            return null;
         }
    }

    function verifyUser($param1, $param2) {
        $db = getDB();

        $sqlTop = "select id,email,password,alias,role,locktime,attempt from users where email=? ";

        $statement = $db -> prepare($sqlTop);
        $statement -> bind_param('s', $param1);

        $response = new stdClass();
        $response -> success = false;
        if($statement -> execute())
        {
            $statement -> store_result();
            if($statement -> num_rows > 0)
            {
                $userObj = new stdClass();

                $statement -> bind_result($userObj -> id, $userObj -> email, $userObj -> password, $userObj -> alias,$userObj -> role, $userObj -> locktime, $userObj -> attempt);
                $statement -> fetch();
                $statement -> close();
                if($userObj -> attempt >= 5) {
                    $time = time();
                    if($time - strtotime($userObj -> locktime >= (60 * 15))) {
                        $sql = "update users set attempt=0 where email=?";
                        $statement1 = $db -> query($sql);
                        $statement1 -> bind_param('s', $param1);
                        $db -> execute();
                        $statement1 -> close();
                    } else {
                       $response -> success = false;
                       $response -> msg = "You have tried to login too many times.";
                       return $response;
                    }
                }

                $phpassHash = new \Phpass\Hash;

                if(!$phpassHash->checkPassword($param2, $userObj -> password))
                {
                    $sql = "update users set attempt=".($userObj -> attempt + 1)." where email=?";

                    $statement2 = $db -> prepare($sql);
                    $statement2 -> bind_param($param1);
                    $statement2 -> execute();
                    $statement2 -> close();
                    if(($userObj -> attempt +1 ) >= 4) {
                        $time = time();
                        $sql = "update users set locktime=DATE_ADD(NOW(), INTERVAL 15 MINUTE) where email=?";

                        $statement3 = $db -> prepare($sql);
                        $statement3 -> bind_param('s', $param1);
                        $statement3 -> execute();
                        $statement3 -> close();

                        $fp = fopen("log.txt",'a');
                        $content = $userObj -> email."   ".date("Y-m-d H:i:s")."   ".$_SERVER["REMOTE_ADDR"]."\n";
                        fwrite($fp, $content);
                        fclose($fp);
                    }
                    $response -> success = false;
                    $response -> message = "Username or Password incorrect";
                    return $response;
                }
                else{
                    $authToken = getToken(40);
                    $sql = "update users set attempt=0,authtoken=?,authExpire=DATE_ADD(NOW(),INTERVAL 1 DAY) where email=?";
                    $statement4 = $db -> prepare($sql);
                    $statement4 -> bind_param('ss', $authToken, $param1);
                    $statement4 -> execute();
                    $statement4 -> close();

                    $response -> success = true;
                    $response -> alias = $userObj -> alias;
                    $response -> id = $userObj -> id;
                    $response -> authtoken = $authToken;
                    $response -> role = $userObj -> role;
                }
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

    function payRegistration($token, $authUser)
    {
        require_once("./Stripe.php");

        Stripe::setApiKey("sk_test_MEx8F6JQpTjOfy67AOICA3xf");
        $response = new stdClass();

        try {
            $charge = Stripe_Charge::create(array(
                "amount" => 2 * 100,
                "currency" => "usd",
                "card" => $token,
                "description" => $authUser -> email,
                "statement_description" => "GT Gamefest"
            ));

            $response -> success = true;
            $db = getDB();
            $sql = "update users set paid=1 where id=?";

            $statement = $db -> prepare($sql);
            $statement -> bind_param('i', $authUser -> id);

            if($statement -> execute())
            {
               $response -> success = true;
            }
            else{
                $response -> success = false;
                $response -> message = "Your card has been charged successfully, however there was a problem updating your account. Please contact an admin. Error: ".$db -> error;
            }


        } catch(Stripe_CardError $e) {
            $response -> success = false;
            $body = $e -> getJsonBody();
            $err = $body['error'];
            $response -> message = $err['code']." -- ".$err['message'];
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
        $sql = "select * from teams where ID=?";

        $statement = $db -> prepare($sql);
        $statement -> bind_param('i',$id);
        $teamObj = new stdClass();

        if($statement -> execute())
        {
            //TODO check for captain/admin to include password
            $statement -> bind_result($teamObj -> ID, $teamObj -> name, $teamObj -> captain, $teamObj -> password, $teamObj -> des, $teamObj -> logo, $teamObj -> game, $teamObj -> member1, $teamObj -> member2, $teamObj -> member3, $teamObj -> member4);
            $statement -> fetch();
            $statement -> close();

            $statement1 = $db -> prepare("CALL getTeamByIds(?,?,?,?,?)");
            $statement1 -> bind_param('iiiii',$teamObj -> captain ,$teamObj -> member1,$teamObj -> member2,$teamObj -> member3, $teamObj -> member4);

            if($statement1 -> execute())
            {
                $statement1 -> bind_result($teamObj -> captainName, $teamObj -> member1Name, $teamObj -> member2Name, $teamObj -> member3Name, $teamObj -> member4Name);
                $statement1 -> fetch();
            }
            else{
            ChromePhp::log($db -> error);
            }
        }
        else{
            ChromePhp::error($db -> error);
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
        $sql = "select password,member1,member2,member3,member4 from teams where id=?";

        $statement = $db -> prepare($sql);
        $statement -> bind_param('i',$data -> teamId);

        if($statement ->  execute())
        {
            $teamObj = new stdClass();
            $statement -> bind_result($teamObj -> password, $teamObj -> member1, $teamObj -> member2, $teamObj -> member3, $teamObj -> member4);
            $statement -> fetch();

            if($teamObj -> password == "" || $teamObj -> password == $data -> password) {
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
