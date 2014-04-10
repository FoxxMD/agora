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

    function payRegistration($token, $authUser)
    {
        require_once("./Stripe.php");

        Stripe::setApiKey("sk_live_c3DX07FPOXWKvXaAHLb7g2mB");
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
?>
