<?php

/**
 * Effectue une requête HTTP GET à l'URL spécifiée avec une éventuelle clé API et renvoie le code de retour de la requête et le contenu de la réponse.
 *
 * Cette fonction utilise cURL pour effectuer une requête HTTP GET à l'URL spécifiée. Elle peut également inclure une clé API dans les en-têtes de la requête si celle-ci est fournie. Elle récupère ensuite le code de retour de la requête HTTP ainsi que le contenu de la réponse. En cas d'erreur, elle renvoie le code d'erreur cURL et un message d'erreur. Dans le cas contraire, elle renvoie le code de retour HTTP et le contenu de la réponse.
 *
 * @param string $url L'URL à laquelle envoyer la requête HTTP GET.
 * @param string $apiKey (Optionnel) La clé API à inclure dans les en-têtes de la requête HTTP.
 * @return array Un tableau associatif contenant le code de retour de la requête et le contenu de la réponse.
 *               Le tableau contient les clés suivantes :
 *               - 'http_code' : Le code de retour HTTP de la requête.
 *               - 'response_data' : Le contenu de la réponse de la requête.
 */
function sendGetRequest(string $url, string $apiKey = ""): array {
    // Configuration de la requête cURL
    $curl = curl_init();
    curl_setopt($curl, CURLOPT_URL, $url);
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);

    // Configuration des en-têtes personnalisés
    if (!empty($apiKey)) {
        curl_setopt($curl, CURLOPT_HTTPHEADER, [
            "Moidoumbejleprendfacile: $apiKey"
        ]);
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

/**
 * Affiche les résultats d'un test pour un endpoint donné.
 *
 * Cette fonction affiche les résultats d'un test pour un endpoint spécifié, comprenant l'URL de la requête,
 * le code HTTP reçu, le statut du code HTTP attendu, le message reçu, le statut du message attendu,
 * et un message indiquant si le test a réussi ou échoué.
 *
 * @param string $testName Le nom du test effectué.
 * @param string $url L'URL de la requête effectuée.
 * @param bool $isHttpCodeCorrect Un booléen indiquant si le code HTTP reçu correspond à celui attendu.
 * @param bool $isMessageCorrect Un booléen indiquant si le message reçu correspond à celui attendu.
 * @param string $errorMessage Le message d'erreur personnalisé à afficher en cas de résultat incorrect.
 * @param string $httpCodeReceived Le code HTTP reçu lors de la requête.
 * @param string $messageReceived Le message reçu lors de la requête.
 * @return void
 */
function afficherResultatTest(string $testName, string $url, bool $isHttpCodeCorrect, bool $isMessageCorrect, string $errorMessage = '', string $httpCodeReceived = '', string $messageReceived = ''): void {
    // Affichage des résultats du test
    echo "<div style='background-color: #f0f0f0; padding: 10px; margin-bottom: 20px;'>";
    echo "<strong>Test de l'endpoint $testName :</strong><br>";
    echo " - URL de la requête : $url<br>";
    
    echo " - Code HTTP reçu : $httpCodeReceived<br>"; // Affichage du code HTTP reçu

    // Affichage du statut du code HTTP
    if ($isHttpCodeCorrect) {
        echo " - Code HTTP attendu : <span style='color: green;'>Correct</span><br>";
    } else {
        echo " - Code HTTP attendu : <span style='color: red;'>Incorrect</span><br>";
    }

    echo " - Message reçu : $messageReceived<br>"; // Affichage du message reçu

    // Affichage du statut du message
    if ($isMessageCorrect) {
        echo " - Message attendu : <span style='color: green;'>Correct</span><br>";
    } else {
        echo " - Message attendu : <span style='color: red;'>Incorrect</span><br>";
    }
    
    // Affichage du résultat global du test
    if ($isHttpCodeCorrect && $isMessageCorrect) {
        echo "<span style='color: green;'> => Test réussi : le code HTTP et le message sont conformes aux attentes.</span><br>";
    } else {
        echo "<span style='color: red;'> => Test échoué : vérifiez le code HTTP et/ou le message reçus.</span><br>";
    }

    echo "</div><br>";
}