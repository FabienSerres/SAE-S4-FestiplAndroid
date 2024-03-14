<?php

require_once("UtilsPourTest.php");

function testGetAllFestivalsSansApiKey(): void {
    $url = "http://localhost:63342/SAE-S4-FestiplAndroid/api/authentification/test/";

    $resultat = sendGetRequest($url);
    echo $resultat["response_data"];
}

testGetAllFestivalsSansApiKey();