<?php

    require_once('./lib.php');

    $mode = $_GET["mode"];

    switch($mode) {
        case "create" : $result = createTeam($_GET["param1"],$_GET["param2"],$_GET["param3"],$_GET["param4"]); break;
        case "get" : $result = getTeams($_GET["param1"], $_GET["param2"]); break;
        case "set" : $result = setTeams($_GET["param1"], $_GET["param2"], $_GET["param3"], $_GET["param4"]); break;
    }

    echo $result;
?>