<?php

/**
 * Fonction pour créer une clé à partir du login et du mot de passe.
 *
 * Cette fonction génère une clé (suppose unique) à partir du login et du mot de passe fournis,
 * en utilisant un algorithme de hachage HMAC avec différentes méthodes de hachage.
 *
 * @param string $login    Le login de l'utilisateur.
 * @param string $password Le mot de passe de l'utilisateur.
 *
 * @return string L'en-tête généré.
 */
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

/**
 * Fonction pour vérifier l'authentification de l'utilisateur à l'aide de l'API Key.
 *
 * Cette fonction vérifie si l'utilisateur est authentifié en vérifiant la présence et la validité de l'API Key
 * dans les en-têtes de la requête HTTP. Si l'API Key est valide, la fonction se termine normalement, sinon
 * elle renvoie un message d'erreur JSON avec le code d'état correspondant.
 *
 * @return void
 */
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