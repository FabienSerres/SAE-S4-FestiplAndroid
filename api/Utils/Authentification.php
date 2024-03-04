<?php

function CreateHeaderFromLoginPassword(string $login, string $password): string {

    $apiKey = "";

    for($i = 0; $i < 5; $i++) {
        $apiKey .= strval(rand(0,9));
    }

    $apiKey .= $login;

    $apiKey = hash_hmac("md5", $apiKey, $apiKey);
    $apiKey = hash_hmac("sha256", $apiKey, $password);

    $apiKey .= $password;

    for($i = 0; $i < 5; $i++) {
        $apiKey .= strval(rand(0,9));
    }

    return hash_hmac("sha512", $apiKey, $login);

}

function CheckIsAuthentified(): void {
    try {
        $ApiKeyField = "Moidoumbejleprendfacile";
        $headers = getallheaders();

        if (empty($headers[$ApiKeyField])) {
            $infos["message"] = "Vous n'avez pas d'api key.";
            sendJson(400, $infos);
        }

        $apiKey = $headers[$ApiKeyField];
        $pdo = connecteBD();
        $request = "SELECT 1 FROM Utilisateur WHERE APIKey = ?";

        $stmt = $pdo->prepare($request);
        $stmt->execute([$apiKey]);

        $data = $stmt->fetchAll();

        if (count($data) > 0) {
            if($data[0][1] == 1) {
                return;
            }
        }

        $infos["message"] = "Api key invalide.";
        sendJson(400, $infos);

    } catch(Exception $e) {
        $infos["message"] = "Erreur: " .$e->getMessage();
        sendJson(500, $infos);
    }
}