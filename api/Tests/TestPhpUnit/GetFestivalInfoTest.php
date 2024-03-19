<?php

use PHPUnit\Framework\TestCase;

class GetFestivalInfoTest extends TestCase {

    // Test en cas d'erreur interne du serveur
    public function testGetInfoFestivalServerError() {
        // GIVEN: Initialisation du mock PDO avec un comportement générant une exception
        $pdoMock = $this->createMock(PDO::class);
        $pdoMock->method('prepare')->willThrowException(new Exception('Erreur interne'));

        // WHEN: Appel de la fonction à tester
        $result = getFestivalInfo($pdoMock, 1);

        // THEN: Vérification du résultat attendu
        $this->assertEquals([500, ["message" => "Erreur: Erreur interne"]], $result);
    }

    // Test avec un identifiant feestival invalide
    public function testGetAllFestivalsInvalidFestivalID() {
        // GIVEN: Initialisation du mock PDO 
        $pdoMock = $this->createMock(PDO::class);
        $statementMock = $this->createMock(PDOStatement::class);

        $pdoMock->method('prepare')->willReturn($statementMock);
        $statementMock->method('execute')->willReturn(true);
        $statementMock->method('fetchAll')->willReturn([]);

        // WHEN: Appel de la fonction à tester avec un identifiant utilisateur invalide (-1)
        $result = getAllFestivals($pdoMock, -1);

        // THEN: Vérification du résultat attendu
        // Resultat attendu = liste vide + code 200, aucun festival trouvé pour l'utilisateur -1
        $this->assertEquals([
            0 => 200, 
            1 => null,
        ], $result);
    }

    // Test lorsque aucun festival n'est trouvé pour un utilisateur donné
    public function testGetAllFestivalsAucunFestivalTrouve() {
        // GIVEN: Initialisation du mock PDO avec un comportement renvoyant une liste vide de festivals
        $pdoMock = $this->createMock(PDO::class);
        $statementMock = $this->createMock(PDOStatement::class);

        $pdoMock->method('prepare')->willReturn($statementMock);
        $statementMock->method('execute')->willReturn(true);
        $statementMock->method('fetchAll')->willReturn([]);

        // WHEN: Appel de la fonction à tester
        $result = getAllFestivals($pdoMock, 1);

        // THEN: Vérification du résultat attendu
        $this->assertEquals([
            0 => 200, 
            1 => null,
        ], $result);
    }

}