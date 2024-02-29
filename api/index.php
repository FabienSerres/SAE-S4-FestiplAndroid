<?php

require("Utils/Utils.php");

$data = array();
$data["test"] = "ok";

sendJson(400, $data);

// Recuperation du type de requete (GET, POST, ...)
switch ($_SERVER["REQUEST_METHOD"]) {

    // Si c'est une requete GET
    case "GET" :
        if (!empty($_GET["demande"])) {
            $url = explode("/", filter_var($_GET["demande"], FILTER_SANITIZE_URL));
            switch($url[0]) {
                case "articlesStocksPrix" :
                    getInfos();
                    break;

                default :
                    $infos["message"] = $url[0] . " inexistant";
                    sendJson($infos, 404);
            }

        } else {
            $infos["message"] = "URL inexistant";
            sendJson($infos, 404);
        }
        break;

    // Si c'est une requete PUT
    case "PUT":
        if (!empty($_GET["demande"])) {
            $url = explode("/", filter_var($_GET["demande"], FILTER_SANITIZE_URL));
            switch($url[0]) {
                case "CB_modifPrixStock" :
                    if (isset($url[1]) && !empty($url[1])) {
                        updateStocksPrice($url[1]);
                    } else {
                        $infos["message"] = "Veuillez renseigner un code barre";
                        sendJson($infos, 404);
                    }
                    break;
                default :
                    $infos["message"] = $url[0] . " inexistant";
                    sendJson($infos, 404);
            }
        } else {
            $infos["message"] = "URL inexistant";
            sendJson($infos, 404);
        }
        break;
}
