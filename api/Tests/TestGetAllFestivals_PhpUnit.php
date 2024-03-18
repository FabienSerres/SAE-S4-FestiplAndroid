<?php

require("../Services/ApiService.php");
use PHPUnit\Framework\TestCase;

class TestGetAllFestivals_PhpUnit extends TestCase {

    // Test lorsque tout se passe bien
    public function testGetAllFestivalsSuccess() {
        // GIVEN: Initialisation du mock PDO avec un comportement attendu
        $pdoMock = $this->createMock(PDO::class);
        $pdoStatementMockl = $this->createMock(PDOStatement::class);

        $pdoMock->method('prepare')->willReturn($pdoStatementMockl);
        
        $pdoStatementMockl->method('execute')->willReturn(true);
        $pdoStatementMockl->method('fetchAll')->willReturn([
            ["idFestival" => 1, "titre" => "Festival A"],
            ["idFestival" => 2, "titre" => "Festival B"]
        ]);

        // WHEN: Appel de la fonction à getAllFestivals
        $result = getAllFestivals($pdoMock, 1);

        // THEN: Vérification du résultat attendu
        $this->assertEquals([200, [
            1 => ["idFestival" => 1, "titre" => "Festival A", "favoris" => false],
            2 => ["idFestival" => 2, "titre" => "Festival B", "favoris" => false]
        ]], $result);
    }

    // Test en cas d'erreur interne du serveur
    public function testGetAllFestivalsServerError() {
        // GIVEN: Initialisation du mock PDO avec un comportement générant une exception
        $pdoMock = $this->createMock(PDO::class);
        $pdoMock->method('prepare')->willThrowException(new Exception('Erreur interne'));

        // WHEN: Appel de la fonction à tester
        $result = getAllFestivals($pdoMock, 1);

        // THEN: Vérification du résultat attendu
        $this->assertEquals([500, ["message" => "Erreur: Erreur interne"]], $result);
    }

    // Test avec un identifiant utilisateur invalide
    public function testGetAllFestivalsInvalidUserID() {
        // GIVEN: Initialisation du mock PDO (non utilisé dans ce cas)
        $pdoMock = $this->createMock(PDO::class);
        $pdoMock->method('prepare')->willReturn($this->createMock(PDOStatement::class));
        $pdoMock->method('execute')->willReturn(true);
        $pdoMock->method('fetchAll')->willReturn([]);

        // WHEN: Appel de la fonction à tester avec un identifiant utilisateur invalide (-1)
        $result = getAllFestivals($pdoMock, -1);

        // THEN: Vérification du résultat attendu
        // Resultat attendu = liste vide + code 200, aucun festival trouvé pour l'utilisateur -1
        $this->assertEquals([200, []], $result);
    }

    // Test lorsque aucun festival n'est trouvé pour un utilisateur donné
    public function testGetAllFestivalsAucunFestivalTrouve() {
        // GIVEN: Initialisation du mock PDO avec un comportement renvoyant une liste vide de festivals
        $pdoMock = $this->createMock(PDO::class);
        $pdoMock->method('prepare')->willReturn($this->createMock(PDOStatement::class));
        $pdoMock->method('execute')->willReturn(true);
        $pdoMock->method('fetchAll')->willReturn([]);

        // WHEN: Appel de la fonction à tester
        $result = getAllFestivals($pdoMock, 1);

        // THEN: Vérification du résultat attendu
        $this->assertEquals([200, []], $result);
    }

}