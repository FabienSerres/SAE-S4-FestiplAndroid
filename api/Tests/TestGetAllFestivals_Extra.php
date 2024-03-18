<?php

require_once("UtilsPourTest.php");

function testGetAllFestivalsSansApiKey(): void {
    $url = "http://localhost/SAE-S4-FestiplAndroid/api/getAllFestivals/1/";

    $resultat = sendGetRequest($url);
    
    // Vérification du code HTTP et du message
    $estHttpCodeBon = $resultat["http_code"] == 401;
    $estMessageBon = strcmp(json_decode($resultat["response_data"])->message,"Vous n'avez pas d'api key.") == 0;
    $messageErreur = "";

    if (!$estHttpCodeBon && !$estMessageBon) {
        $messageErreur = "Le message et le http code sont invalide.";
    } else if (!$estHttpCodeBon) {
        $messageErreur = "Le http code est invalide.";
    } else if (!$estMessageBon) {
        $messageErreur = "Le message est invalide.";
    }

    afficherResultatTest(
        "api/getAllFestivals sans api key", 
        $url, 
        $estHttpCodeBon,
        $estMessageBon,
        $messageErreur,
        $resultat["http_code"],
        $resultat["response_data"],
    );
}

function testGetAllFestivalsAvecApiKeyVide(): void {
    $url = "http://localhost/SAE-S4-FestiplAndroid/api/getAllFestivals/1/";

    $resultat = sendGetRequest($url, "''");
    
    // Vérification du code HTTP et du message
    $estHttpCodeBon = $resultat["http_code"] == 401;
    $estMessageBon = strcmp(json_decode($resultat["response_data"])->message,"Api key invalide.") == 0;
    $messageErreur = "";

    if (!$estHttpCodeBon && !$estMessageBon) {
        $messageErreur = "Le message et le http code sont invalide.";
    } else if (!$estHttpCodeBon) {
        $messageErreur = "Le http code est invalide.";
    } else if (!$estMessageBon) {
        $messageErreur = "Le message est invalide.";
    }

    afficherResultatTest(
        "api/getAllFestivals avec api key vide", 
        $url, 
        $estHttpCodeBon,
        $estMessageBon,
        $messageErreur,
        $resultat["http_code"],
        $resultat["response_data"],
    );
}

function testGetAllFestivalsAvecApiKeyInvalide(): void {
    $url = "http://localhost/SAE-S4-FestiplAndroid/api/getAllFestivals/1/";

    $resultat = sendGetRequest($url, "c'estpasunebonneapikey");
    
    // Vérification du code HTTP et du message
    $estHttpCodeBon = $resultat["http_code"] == 401;
    $estMessageBon = strcmp(json_decode($resultat["response_data"])->message,"Api key invalide.") == 0;
    $messageErreur = "";

    if (!$estHttpCodeBon && !$estMessageBon) {
        $messageErreur = "Le message et le http code sont invalide.";
    } else if (!$estHttpCodeBon) {
        $messageErreur = "Le http code est invalide.";
    } else if (!$estMessageBon) {
        $messageErreur = "Le message est invalide.";
    }

    afficherResultatTest(
        "api/getAllFestivals avec api key invalide", 
        $url, 
        $estHttpCodeBon,
        $estMessageBon,
        $messageErreur,
        $resultat["http_code"],
        $resultat["response_data"],
    );
}

// TODO faire d'autre tests

// Call de tout les test
testGetAllFestivalsSansApiKey();
testGetAllFestivalsAvecApiKeyVide();
testGetAllFestivalsAvecApiKeyInvalide();