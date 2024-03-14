<?php

// Inclusion des fichiers nécessaires
require_once("Utils/Utils.php");
require_once("Utils/Authentification.php");
require_once("Services/ApiService.php");

// Vérifie que le call à l'API contient bien le $_GET["demande"]
if (!isset($_GET["demande"]) || empty($_GET["demande"])) {
    $data["message"] = "URL inexistant";
    sendJson(404, $data);
}

// Découpe de l'URL en segments
$url = explode("/", filter_var($_GET["demande"], FILTER_SANITIZE_URL));

// Récupération du type de requête (GET, POST, ...)
switch ($_SERVER["REQUEST_METHOD"]) {

    // Si c'est une requête GET
    case "GET":

        switch($url[0]) {

            case "getAllFestivals" :

                // Vérification de l'authentification de l'utilisateur
                CheckIsAuthentified();

                if (isset($url[1])) {
                    getAllFestivals($url[1]);
                } else {
                    $infos["message"] = "Paramètre id non renseigné";
                    sendJson(400, $infos);
                }

                break;

            case "getFavoriteFestivals":

                // Vérification de l'authentification de l'utilisateur
                CheckIsAuthentified();

                if (isset($url[1])) {
                    getFavoriteFestivals($url[1]);
                } else {
                    $infos["message"] = "Paramètre id non renseigné";
                    sendJson(400, $infos);
                }

                break;

            case "getFestivalInfo":

                // Vérification de l'authentification de l'utilisateur
                CheckIsAuthentified();

                if (isset($url[1])) {
                    getFestivalInfo($url[1]);
                } else {
                    $infos["message"] = "Paramètre id non renseigné";
                    sendJson(400, $infos);
                }

                break;

            case "authentification":

                // Vérification des paramètres de la requête
                if (!isset($url[1])) {
                    $infos["message"] = "Paramètre login non renseigné";
                    sendJson(400, $infos);
                }

                if (!isset($url[2])) {
                    $infos["message"] = "Paramètre password non renseigné";
                    sendJson(400, $infos);
                }

                // Appel de la fonction d'authentification
                authentification($url[1], $url[2]);
                break;

            default:
                $infos["message"] = $url[0] . " inexistant";
                sendJson(404, $infos);

        }

        break;

    // Si c'est une requête PUT
    case "PUT":

        // Actions à effectuer pour les requêtes PUT (non implémenté dans cet exemple)
        switch ($url[0]) {


            case "addFavoriteFestival":
                
                CheckIsAuthentified();

                if (!isset($url[1])) {
                    $infos["message"] = "Paramètre de l'utilisateur non renseigné";
                    sendJson(400, $infos);
                }

                if (!isset($url[2])) {
                    $infos["message"] = "Paramètre du festival non renseigné";
                    sendJson(400, $infos);
                }

                addFavoriteFestival($url[1], $url[2]);
                break;
        }
        

        break;

    case "DELETE":

        // Actions à effectuer pour les requêtes DELETE
        switch($url[0]) {

            case "deleteFavoriteFestival":

                CheckIsAuthentified();
                
                if (!isset($url[1])) {
                    $infos["message"] = "Paramètre de l'utilisateur non renseigné";
                    sendJson(400, $infos);
                }

                if (!isset($url[2])) {
                    $infos["message"] = "Paramètre du festival non renseigné";
                    sendJson(400, $infos);
                }
                
                deleteFavoriteFestival($url[1], $url[2]);
                break;

        }

}
