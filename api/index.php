<?php

require_once("Utils/Utils.php");
require_once("Utils/Authentification.php");
require_once("Services/ApiService.php");

//$data = array();
//$data["test"] = "ok";
//sendJson(400, $data);

// Verifie que le call a l'api contient bien le $_GET["demande"]
if (!isset($_GET["demande"]) || empty($_GET["demande"])) {
    $data["message"] = "URL inexistant";
    sendJson(404, $data);
}

$url = explode("/", filter_var($_GET["demande"], FILTER_SANITIZE_URL));
// Recuperation du type de requete (GET, POST, ...)
switch ($_SERVER["REQUEST_METHOD"]) {

    // Si c'est une requete GET
    case "GET" :

        switch($url[0]) {

            case "getAllFestivals" :

                CheckIsAuthentified();

                if (isset($url[1])) {
                    getAllFestivals($url[1]);
                } else {
                    $infos["message"] = "Paramètre id non renseigné";
                    sendJson(400, $infos);
                }

                break;

            case "getFavoriteFestivals":

                CheckIsAuthentified();

                if (isset($url[1])) {
                    getFavoriteFestivals($url[1]);
                } else {
                    $infos["message"] = "Paramètre id non renseigné";
                    sendJson(400, $infos);
                }

                break;

            case "getFestivalInfo":

                CheckIsAuthentified();

                if (isset($url[1])) {
                    getFestivalInfo($url[1]);
                } else {
                    $infos["message"] = "Paramètre id non renseigné";
                    sendJson(400, $infos);
                }

                break;
                
            case "authentification":

                if (!isset($url[1])) {
                    $infos["message"] = "Paramètre login non renseigné";
                    sendJson(400, $infos);
                }

                if (!isset($url[2])) {
                    $infos["message"] = "Paramètre password non renseigné";
                    sendJson(400, $infos);
                }

                authentification($url[1], $url[2]);
                break;
                
            default:
                $infos["message"] = $url[0] . " inexistant";
                sendJson(404, $infos);

        }

        break;

    // Si c'est une requete PUT
    case "PUT":

        //switch($url[0]) {
        // case "deleteFavoriteFestival":
        //     deleteFavoriteFestival($url[1], $url[2]);
        //     break;
        //}

        break;
}
