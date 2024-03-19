<?php

use PHPUnit\Framework\TestCase;

class GetFavoriteFestivalsTest extends TestCase {

    /**
     * Test lorsque tout se passe bien
     */
    public function testGetFavoriteFestivalsSuccess() {
        // GIVEN: Initialisation du mock PDO avec un comportement attendu
        $pdoMock = $this->createMock(PDO::class);
        $pdoStatementMock = $this->createMock(PDOStatement::class);
    
        $pdoMock->expects($this->once())
                ->method('prepare')
                ->willReturn($pdoStatementMock);
    
        $pdoStatementMock->expects($this->once())
                        ->method('bindParam')
                        ->with(':id', 1); // Vérification du paramètre passé à bindParam

        $pdoStatementMock->expects($this->once())
                        ->method('execute')
                        ->willReturn(true);

        $pdoStatementMock->expects($this->once())
                        ->method('fetchAll')
                        ->willReturn([["titre" => "Festival 1", "idFestival" => 1], ["titre" => "Festival 2", "idFestival" => 2]]);
    
        // WHEN: Appel de la fonction getFavoriteFestivals
        $result = getFavoriteFestivals($pdoMock, 1);
    
        // THEN: Vérification du résultat attendu
        $this->assertEquals([200, [
            ["titre" => "Festival 1", "idFestival" => 1],
            ["titre" => "Festival 2", "idFestival" => 2]
        ]], $result);
    }

    /**
     * Teste la fonction getFavoriteFestivals avec un identifiant utilisateur invalide.
     */
    public function testGetFavoriteFestivalsInvalidUserID() {
        // GIVEN: Initialisation du mock PDO 
        $pdoMock = $this->createMock(PDO::class);
        $statementMock = $this->createMock(PDOStatement::class);

        $pdoMock->method('prepare')->willReturn($statementMock);
        $statementMock->method('execute')->willReturn(true);
        $statementMock->method('fetchAll')->willReturn([]);

        // WHEN: Appel de la fonction à tester avec un identifiant utilisateur invalide (-1)
        $result = getFavoriteFestivals($pdoMock, -1);

        // THEN: Vérification du résultat attendu
        // Resultat attendu = liste vide + code 200, aucun festival trouvé pour l'utilisateur -1
        $this->assertEquals([200, []], $result);
    }

    /**
     * Teste la fonction getFavoriteFestivals mais aucun favoris est trouvé
     */
    public function testGetFavoriteFestivalsAucunFavorisTrouve() {
        // GIVEN: Initialisation du mock PDO 
        $pdoMock = $this->createMock(PDO::class);
        $statementMock = $this->createMock(PDOStatement::class);

        $pdoMock->method('prepare')->willReturn($statementMock);
        $statementMock->method('execute')->willReturn(true);
        $statementMock->method('fetchAll')->willReturn([]);

        // WHEN: Appel de la fonction à tester 
        $result = getFavoriteFestivals($pdoMock, 1);

        // THEN: Vérification du résultat attendu
        // Resultat attendu = liste vide + code 200, aucun festival trouvé 
        $this->assertEquals([200, []], $result);
    }

    /**
     * Teste la fonction getFavoriteFestivals en cas d'erreur interne du serveur.
     */
    public function testGetFavoriteFestivalsServerError() {
        // GIVEN: Initialisation du mock PDO avec un comportement générant une exception
        $pdoMock = $this->createMock(PDO::class);
        $pdoMock->method('prepare')->willThrowException(new Exception('Erreur interne'));

        // WHEN: Appel de la fonction à tester
        $result = getFavoriteFestivals($pdoMock, 1);

        // THEN: Vérification du résultat attendu
        $this->assertEquals([500, ["message" => "Erreur: Erreur interne"]], $result);
    }

}