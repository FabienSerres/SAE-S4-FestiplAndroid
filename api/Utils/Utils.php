<?php

require("View.php");

/**
 * Envoie les données au format JSON avec un code de statut HTTP spécifié.
 *
 * Cette fonction crée un objet de vue, y définit les variables "status" et "json" avec les valeurs fournies,
 * puis rend la vue au chemin spécifié. Enfin, elle termine le script en cours.
 *
 * @param int $code Le code de statut HTTP à envoyer.
 * @param mixed $data Les données à envoyer au format JSON.
 * @return void
 */
function sendJson(int $code, mixed $data): void {
    $view = new View("view");

    $view->setVar("status", $code);
    $view->setVar("json", $data);

    $view->render("/SAE-S4-FestiplAndroid");
    die();
}

/**
 * Connecte à la base de données MySQL et renvoie un objet PDO.
 *
 * Cette fonction essaie de se connecter à la base de données MySQL en utilisant les informations d'identification
 * fournies. Si la connexion réussit, elle renvoie un objet PDO configuré avec les options spécifiées.
 * En cas d'échec de la connexion, elle envoie une réponse JSON avec un code d'erreur 500.
 *
 * @throws PDOException Si une erreur survient lors de la connexion à la base de données.
 * @return PDO Un objet PDO représentant la connexion à la base de données.
 */
function connecteBD(): PDO {
    try {
        $user = "root";
        $password = "root";
        $options=[
            PDO::ATTR_ERRMODE=>PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE=>PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES=>false];

        $pdo = new PDO("mysql:host=localhost;dbname=festiplandroid;charset=utf8", $user, $password, $options);

        return $pdo;
    } catch (PDOException $e) {
        $infos["message"] = "Problème de connexion à la base de données";
        sendJson(500, $infos);
        die();
    }
}

/**
 * Fonction pour appeler une fonction et envoyer ses résultats au format JSON.
 *
 * Cette fonction prend en paramètre une fonction callable et exécute cette fonction en lui passant
 * un objet PDO représentant la connexion à la base de données. Elle envoie ensuite les résultats
 * de la fonction au format JSON en utilisant la fonction sendJson.
 *
 * @param callable $func La fonction à appeler. Cette fonction doit accepter un objet PDO comme paramètre et retourner un tableau.
 *                       Le premier élément du tableau doit être le code HTTP de réponse et le deuxième élément doit être les données à envoyer.
 *                       Cette fonction doit retourner un tableau.
 *
 * @return void
 */
function CallFunctionAndSendResults(callable $func): void {
    $pdo = connecteBD();
    $data = $func($pdo);
    sendJson($data[0], $data[1]);
}