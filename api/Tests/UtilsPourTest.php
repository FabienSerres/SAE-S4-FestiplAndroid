<?php

/**
 * Effectue une requête HTTP GET à l'URL spécifiée avec des en-têtes personnalisés et renvoie le code de retour de la requête
 * ainsi que le contenu de la réponse.
 *
 * Cette fonction utilise cURL pour effectuer une requête HTTP GET à l'URL spécifiée avec des en-têtes personnalisés.
 * Elle récupère ensuite le code de retour de la requête HTTP ainsi que le contenu de la réponse.
 * En cas d'erreur, elle renvoie le code d'erreur cURL et un message d'erreur.
 * Dans le cas contraire, elle renvoie le code de retour HTTP et le contenu de la réponse.
 *
 * @param string $url L'URL à laquelle envoyer la requête HTTP GET.
 * @param array $headers Les en-têtes HTTP personnalisés à inclure dans la requête.
 *                       Chaque élément du tableau doit être une chaîne de caractères de la forme 'NomHeader: ValeurHeader'.
 *                       Par exemple : ['Authorization: Bearer xxxxxxx', 'Content-Type: application/json']
 *
 * @return array Un tableau associatif contenant le code de retour de la requête et le contenu de la réponse.
 *               Le tableau contient les clés suivantes :
 *               - 'http_code' : Le code de retour HTTP de la requête.
 *               - 'response_data' : Le contenu de la réponse de la requête.
 */
function sendGetRequest(string $url, array $headers = []): array {
    // Configuration de la requête cURL
    $curl = curl_init();
    curl_setopt($curl, CURLOPT_URL, $url);
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);

    // Configuration des en-têtes personnalisés
    if (!empty($headers)) {
        curl_setopt($curl, CURLOPT_HTTPHEADER, $headers);
    }

    // Exécution de la requête cURL
    $response = curl_exec($curl);

    // Vérification des erreurs
    if(curl_errno($curl)) {
        $httpCode = curl_errno($curl);
        $responseData = 'Erreur cURL : ' . curl_error($curl);
    } else {
        // Obtenir le code d'état HTTP de la réponse
        $httpCode = curl_getinfo($curl, CURLINFO_HTTP_CODE);
        $responseData = $response;
    }

    // Fermeture de la session cURL
    curl_close($curl);

    // Retourner le code de retour de la requête et le contenu de la réponse
    return array('http_code' => $httpCode, 'response_data' => $responseData);
}