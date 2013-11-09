<?php

    require_once('./lib.php');

    $mode = $_POST["mode"];

    switch($mode) {
        case "create" : $result = createTeam($_POST["param1"],$_POST["param2"],$_POST["param3"],$_POST["param4"]); break;
        case "get" : $result = getTeams($_POST["param1"], $_POST["param2"]); break;
        case "set" : $result = setTeams($_POST["param1"], $_POST["param2"], $_POST["param3"], $_POST["param4"]); break;
    }

    echo $result;
?>