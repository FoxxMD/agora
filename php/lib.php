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

    function checkDuplicate($value) {
        $db = getDB();
        $sql = "select * from users where email=?";
        $statement = $db -> prepare($sql);
        $statement -> bind_param('s',$value);

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

                $sql = "select ID, name from teams where captain=".$userObj -> id;
                if($result = $db -> query($sql))
                {
                    $captainInfo = new stdClass();
                    $count = 0;
                    $userObj -> captainList = array();
                    while($captainInfo = $result -> fetch_object())
                    {
                        $userObj -> captainList[$count] = $captainInfo;
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
                    }
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
        if(($param2 != "paid" && $param2 != "email") || $isAdmin) {
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
            $sql = "update users set ".$fixedParam."=? where id=?";
            //$sql = "update users set ?=? where id=?";

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
                    $statement2 -> bind_param('s',$param1);
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

    function resetPassword($data){
        ChromePhp::log($data -> email);
        $db = getDB();
        $response = new stdClass();
        $response -> success = false;
        $sql = "select * from users where email=?";
        $statement = $db -> prepare($sql);
        $statement -> bind_param('s', $data -> email);
        if($statement -> execute())
        {
            $statement -> store_result();
            if($statement -> num_rows > 0)
            {
                $statement -> close();
                $authToken = getToken(40);
                $sql = "update users set resetToken=? where email=?";
                if($statement1 = $db -> prepare($sql))
                {


                $statement1 -> bind_param('ss', $authToken, $data -> email);
                if($statement1 -> execute())
                {
                $subject = "Forgotten password reset from GT Gamefest";
                $headers   = array();
                $headers[] = "MIME-Version: 1.0";
                $headers[] = "Content-type: text/html; charset=iso-8859-1";
                $headers[] = "From: GT Gamefest <noreply@gtgamefest.com>";
                $headers[] = "Subject: {$subject}";
                $headers[] = "X-Mailer: PHP/".phpversion();
                $headers.="Return-Path:<noreply@gtgamefest.com>";

                $body = "Hello User,\r\n\r\nTo reset your password visit http://beta.gtgamefest.com/#/forgotpw/".$authToken."\r\n\r\nIf HTML does not work please visit http://beta.gtgamefest.com/#/forgotpw/ and use this token to reset your password:\r\n\r\n".$authToken."\r\n\r\n -Gt Gamefest Staff";

                    if(mail($data -> email, $subject, $body, implode("\r\n",$headers), "-f noreply@gtgamefest.com"))
                    {
                        $response -> success = true;
                    }
                    else{
                        $response -> message = "Mail delivery was unsuccessful.";
                    }
                }
                else{
                    $response -> message = "Could not set resetToken: ".$db -> error;
                }
                } else{
                    $response -> message = "SQL preparation error: ".$db -> error;
                }
            }
            else{
                $response -> message = "Email address not found.";
            }
        }
        else{
            $response -> message = "DB error: ".$db -> error;
        }
        return $response;
    }

    function changePassword($data){
        $response = new stdClass();
        $response -> success = false;
        $db = getDB();

        if($data -> resetToken != null)
        {
            $phpassHash = new \Phpass\Hash;
            $pwHash = $phpassHash -> hashPassword($data -> newPassword);
            $sql = "update users set password=? where resetToken=?";
            $statement = $db -> prepare($sql);
            $statement -> bind_param('ss', $pwHash, $data -> resetToken);
            if($statement -> execute())
            {
                $response -> success = true;
            }
            else {
                $response -> message = "Could not change password: ".$db -> error;
            }
        }
        else if($data -> oldPassword != null)
        {
            $sql = "select password from users where email=?";
            $statement1 = $db -> prepare($sql);
            $statement1 -> bind_param('s',$data -> email);
            if($statement1 -> execute())
            {
                $statement1 -> store_result();
                if($statement1 -> num_rows > 0)
                {
                    $statement1 -> bind_result($passwordHash);
                    $statement1 -> fetch();
                    $statement1 -> close();
                    $phpassHash = new \Phpass\Hash;
                    if($phpassHash->checkPassword($data -> oldPassword, $passwordHash))
                    {
                        $pwHash = $phpassHash -> hashPassword($data -> newPassword);
                        $sql = "update users set password=? where email=?";
                        $statement = $db -> prepare($sql);
                        $statement -> bind_param('ss', $pwHash, $data -> email);
                        if($statement -> execute())
                        {
                            $response -> success = true;
                        }
                        else{
                            $response -> message = "Could not change password: ".$db -> error;
                        }
                    }
                    else{
                        $response -> message = "Current password verification failed.";
                    }
                }
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
            $response -> message = $db -> error;
        }
        return $response;
    }

    function payRegistration($token, $authUser)
    {
        require_once("./Stripe.php");

        Stripe::setApiKey("sk_test_MEx8F6JQpTjOfy67AOICA3xf");
        $response = new stdClass();
        $response -> success = false;
        if(property_exists($authUser,"email"))
        {
            try {
                $customer = Stripe_Customer::create(array(
                              "description" => "User for ".$authUser -> email,
                              "email" => $authUser -> email,
                              "card" => $token
                            ));

                $charge = Stripe_Charge::create(array(
                    "amount" => 15 * 100,
                    "currency" => "usd",
                    "customer" => $customer -> id,
                    "description" => "Charge for registration for ".$authUser -> email,
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
                    $response -> message = "Your card has been charged successfully, however there was a problem updating your account. Please contact an admin. Error: ".$db -> error;
                }

            } catch(Stripe_CardError $e) {
                $body = $e -> getJsonBody();
                $err = $body['error'];
                $response -> message = $err['code']." -- ".$err['message'];
            } catch (Stripe_ApiConnectionError $e) {
                error_log($e);
                $response -> message = "A problem occurred within the Stripe API. Possibly a certificate or network issue.";
             } catch (Stripe_Error $e) {
                error_log($e);
                $response -> message = "A general error occurred with Stripe.";
             } catch (Exception $e) {
               $response -> message = $e -> getMessage();
             }
         }
         else{
            $response -> message = "Problem with getting authenticated user, no email existed for returned object.";
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

    function setTeam($data, $authUser, $isAdmin, $isGameAdmin) {
        $db = getDB();
        $response = new stdClass();

        $sql = "select captain, member1, member2, member3, member4 from teams where id=?";
        if($statement1 = $db -> prepare($sql))
        {
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
                            $response -> success = false;
                            $response -> message = "Not authorized to make this change";
                            return $response;
                        }
                        break;
                    case "member2":
                        if($authUser -> id == $captainId || $authUser -> id == $member2Id || $isAdmin || $isGameAdmin)
                        {
                            $fixedParam = "member2";
                        }else{
                            $response -> success = false;
                            $response -> message = "Not authorized to make this change";
                            return $response;
                        }
                        break;
                    case "member3":
                        if($authUser -> id == $captainId || $authUser -> id == $member3Id || $isAdmin || $isGameAdmin)
                        {
                            $fixedParam = "member3";
                        }else{
                            $response -> success = false;
                            $response -> message = "Not authorized to make this change";
                            return $response;
                        }
                        break;
                    case "member4":
                        if($authUser -> id == $captainId || $authUser -> id == $member4Id || $isAdmin || $isGameAdmin)
                        {
                            $fixedParam = "member4";
                        }else{
                            $response -> success = false;
                            $response -> message = "Not authorized to make this change";
                            return $response;
                        }
                        break;
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
                        $response -> success = false;
                        $response -> message = $db -> error;
                    }
                }
                else{
                    $response -> success = false;
                    $response -> message = $db -> error;
                };
            }
            else{
                $response -> success = false;
                $response -> message = $db -> error;
            }
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
            $statement -> close();

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
                    else{
                        $response -> success = false;
                        $response -> message = $db -> error;
                    }
                }
                else{
                    $response -> success = false;
                    $response -> message = $db -> error;
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
