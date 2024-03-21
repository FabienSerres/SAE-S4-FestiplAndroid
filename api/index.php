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

// filter_var renvoie false ou string
$filterVar = filter_var($_GET["demande"]);

// Php stan detecte quand meme une erreur malgrès le test suivant
if ($filterVar === false) {
   $data["message"] = "Erreur filtre var invalide";
    sendJson(404, $data);
}

// Découpe de l'URL en segments
$url = explode("/", $filterVar, FILTER_SANITIZE_URL);

// Récupération du type de requête (GET, POST, ...)
switch ($_SERVER["REQUEST_METHOD"]) {

    // Si c'est une requête GET
    case "GET":

        switch($url[0]) {

            case "getAllFestivals":

                // Vérification de l'authentification de l'utilisateur
                CheckIsAuthentified();

                if (isset($url[1])) {
                    CallFunctionAndSendResults(function(PDO $pdo): array {
                        global $url;
                        return getAllFestivals($pdo, $url[1]);
                    });
                } else {
                    $infos["message"] = "Paramètre id non renseigné";
                    sendJson(400, $infos);
                }

                break;

            case "getFavoriteFestivals":

                // Vérification de l'authentification de l'utilisateur
                CheckIsAuthentified();

                if (isset($url[1])) {
                    CallFunctionAndSendResults(function(PDO $pdo): array {
                        global $url;
                        return getFavoriteFestivals($pdo, $url[1]);
                    });
                } else {
                    $infos["message"] = "Paramètre id non renseigné";
                    sendJson(400, $infos);
                }

                break;

            case "getFestivalInfo":

                // Vérification de l'authentification de l'utilisateur
                // CheckIsAuthentified();

                if (isset($url[1])) {
                    CallFunctionAndSendResults(function(PDO $pdo): array {
                        global $url;
                        return getFestivalInfo($pdo, $url[1]);
                    });
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

                CallFunctionAndSendResults(function(PDO $pdo): array {
                    global $url;
                    return authentification($pdo, $url[1], $url[2]);
                });

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

                CallFunctionAndSendResults(function(PDO $pdo): array {
                    global $url;
                    return addFavoriteFestival($pdo, $url[1], $url[2]);
                });

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
                
                CallFunctionAndSendResults(function(PDO $pdo): array {
                    global $url;
                    return deleteFavoriteFestival($pdo, $url[1], $url[2]);
                });

                break;

        }

}
