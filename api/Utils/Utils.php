<?php

require("View.php");

function sendJson(int $code, mixed $data): void {
    $view = new View("view");

    $view->setVar("status", $code);
    $view->setVar("json", $data);

    $view->render("/SAE-S4-FestiplAndroid");
    die();
}

function connecteBD() {
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
        sendJson($infos, 500);
        die();
    }
}