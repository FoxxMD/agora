<?php
ini_set('display_errors', 0);

require_once("./crypto.php"); //for token authentication
require_once("./Phpass.php"); //for password hashing/verification

include 'ChromePhp.php'; //using for logging into chrome dev console because setting up an IDE would make too much sense


    function getDB() {
        $db = new mysqli("localhost:3306","matt","preparis", "gtgamefest_db"); //for my local
        //$db = new mysqli("localhost","gtgamefe_beta","G=C?r.%Kd0np", "gtgamefe_beta"); //for beta
        //$db = new mysqli("localhost","gtgamefe_live","3{{(a=lc?JFN", "gtgamefe_live"); //for live
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

                $sql = "select ID, name from teams where captain=".$userObj -> id;
                if($result = $db -> query($sql))
                {
                    $captainInfo = new stdClass();
                    $count = 0;
                    $userObj -> captainList = array();
                    while($captainInfo = $result -> fetch_object())
                    {
                        $userObj -> captainList[$count] = $captainInfo;
                        $count = ++$count;
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
                    while($tourney = $result -> fetch_object())
                    {
                        $userObj -> tournaments[$tourney -> Id] = $tourney;
                    }
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

    function authenticateRequest($authToken)
    {
         $response = new stdClass();
         $response -> success = false;
         $db = getDB();
         $sql = "select email,id,role,authExpire from users where authtoken=?";
         $statement = $db -> prepare($sql);
         $statement -> bind_param('s',$authToken);

         $user = new stdClass();
         if($statement -> execute())
         {
            $statement -> store_result();
            if($statement -> num_rows > 0)
            {
                 $userObj = new stdClass();
                 $statement -> bind_result($userObj -> email,$userObj -> id, $userObj -> role, $userObj -> authExpire);
                 $statement -> fetch();

                 //check to see if token is still valid
                 $date = new DateTime();
                 if($date -> getTimestamp() >= $userObj -> authExpire) {
                    $response -> message = "Your login has expired! Please log back in.";
                    $response -> authExpire = true;
                    return $response;
                 }
                 return $userObj;
            }
            else{
                $response -> message = "Authentication Token was not valid.";
                return $response;
            }
         }
         else{
            $response -> message = "There was a problem authenticating your request, please contact an administrator.";
            error_log($db -> error);
            return $response;
         }
    }

    function verifyUser($param1, $param2) {
        $db = getDB();

        $sqlTop = "select id,email,password,alias,role,locktime,attempt,authtoken,authExpire from users where email=? ";

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

                $statement -> bind_result($userObj -> id, $userObj -> email, $userObj -> password, $userObj -> alias,$userObj -> role, $userObj -> locktime, $userObj -> attempt, $userObj -> authtoken, $userObj -> authExpire);
                $statement -> fetch();
                $statement -> close();
                if($userObj -> attempt >= 5) {
                    $date = new DateTime();
                    if($date -> getTimestamp() >= $userObj -> locktime) {
                        $sql = "update users set attempt=0 where email=?";
                        $statement1 = $db -> prepare($sql);
                        $statement1 -> bind_param('s', $param1);
                        $statement1 -> execute();
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
                        $sql = "update users set locktime=UNIX_TIMESTAMP(DATE_ADD(NOW(), INTERVAL 15 MINUTE)) where email=?";

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
                else {
                    $date = new DateTime();
                    if($date -> getTimestamp() >= $userObj -> authExpire) {
                        $authToken = getToken(40);
                        $sql = "update users set attempt=0,authtoken=?,authExpire=UNIX_TIMESTAMP(DATE_ADD(NOW(),INTERVAL 1 DAY)) where email=?";
                        $statement4 = $db -> prepare($sql);
                        $statement4 -> bind_param('ss', $authToken, $param1);
                        $statement4 -> execute();
                        $statement4 -> close();
                        $response -> authtoken = $authToken;
                    }
                    else{
                        $response -> authtoken = $userObj -> authtoken;
                    }

                    $response -> success = true;
                    $response -> alias = $userObj -> alias;
                    $response -> id = $userObj -> id;
                    $response -> authExpire = $userObj -> authExpire;
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

                    $body = "Hello User,\r\n\r\nTo reset your password visit https://www.gtgamefest.com/forgotpw/".$authToken."\r\n\r\nIf HTML does not work please visit https://www.gtgamefest.com/forgotpw/ and use this token to reset your password:\r\n\r\n".$authToken."\r\n\r\nGamefest Staff\r\ncontact@gtgamefest.com";

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
                }
                else{
                    $response -> message = "SQL preparation error: ".$db -> error;
                }
            }
            else{
                $response -> message = "Email address not found.";
            }
        }
        else{
            $response -> message = "There was a problem resetting your password, please contact an administrator.";
            error_log($db -> error);
        }
        return $response;
    }

    function changePassword($data, $isAdmin){
        $response = new stdClass();
        $response -> success = false;
        $db = getDB();

        if(property_exists($data,"resetToken"))
        {
            $phpassHash = new \Phpass\Hash;
            $sql = "select * from users where resetToken=?";
            $statement1 = $db -> prepare($sql);
            $statement1 -> bind_param('s',$data -> resetToken);
            if($statement1 -> execute())
            {
                $statement1 -> store_result();
                if($statement1 -> num_rows > 0)
                {
                    $statement1 -> close();
                    $pwHash = $phpassHash -> hashPassword($data -> newPassword);
                    $sql = "update users set password=? where resetToken=?";
                    $statement = $db -> prepare($sql);
                    $statement -> bind_param('ss', $pwHash, $data -> resetToken);
                    if($statement -> execute())
                    {
                        $response -> success = true;
                    }
                    else {
                        $response -> message = "Could not change password, please contact an administrator.";
                        error_log($db -> error);
                    }
                }
                else{
                    $response -> message = "Could not find reset token, please make sure it is inputted correctly.";
                }
            }
            else{
                error_log($db -> error);
                $response -> message = "there was a problem changing your password, please contact an administrator";
            }
        }
        else if(property_exists($data,"oldPassword") || $isAdmin)
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
                    if((property_exists($data,"oldPassword") && $phpassHash->checkPassword($data -> oldPassword, $passwordHash)) || $isAdmin)
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
                        $response -> message = "Current password was incorrect.";
                    }
                }
                else{
                    $response -> message = "Email provided was invalid.";
                }
            }
        }
        else{
            $response -> message = "Current password was not provided or your account does not have privileges to perform this action.";
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

    function payRegistration($token, $authUser)
    {
        require_once("./Stripe.php");

        Stripe::setApiKey("sk_live_EQe99LHgK8Pk8qSwT7xCqEIz");
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

    function addTeamMember($data) {
        $db = getDB();
        $response = new stdClass();
        $response -> success = false;

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

    function registerPlayer($data) {

            $db = getDB();

            $sql = "select * from tournaments where ID = ?";
            $statement = $db->prepare($sql);
            $statement->bind_param("i", $data -> tourId);

            $response = new stdClass();
            $response -> success = false;

            if($statement->execute()) {

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
                $statement -> bind_result($info -> Id, $info -> Game, $info -> Name, $info -> isPlaying, $info -> teamCount, $info -> playerCount);
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

            //this is terrible and needs to be replaced by a stored proc but I am stupid and stackoverflow won't answer my question

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