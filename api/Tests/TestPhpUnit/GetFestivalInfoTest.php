<?php

use PHPUnit\Framework\TestCase;

class GetFestivalInfoTest extends TestCase {

    /**
     * Test lorsque tout se passe bien
     */
    public function testGetFestivalInfoSuccess(): void {
        // GIVEN: Initialisation du mock PDO avec un comportement attendu
        $pdoMock = $this->createMock(PDO::class);
        $pdoStatementMock1 = $this->createMock(PDOStatement::class);
        $pdoStatementMock2 = $this->createMock(PDOStatement::class);
        $pdoStatementMock3 = $this->createMock(PDOStatement::class);
        $pdoStatementMock4 = $this->createMock(PDOStatement::class);
    
        $pdoMock->expects($this->exactly(4)) // Attend 4 exécutions
                ->method('prepare')
                ->willReturnOnConsecutiveCalls($pdoStatementMock1, $pdoStatementMock2, $pdoStatementMock3, $pdoStatementMock4);
    
        $pdoStatementMock1->expects($this->once())
                           ->method('execute')
                           ->willReturn(true);
        $pdoStatementMock1->expects($this->once())
                           ->method('fetchAll')
                           ->willReturn(["titre" => "Festival Test", "description" => "Description du festival", "nom" => "Catégorie Test", "dateDebut" => "2024-03-19", "dateFin" => "2024-03-21"]);
    
        $pdoStatementMock2->expects($this->once())
                           ->method('execute')
                           ->willReturn(true);
        $pdoStatementMock2->expects($this->once())
                           ->method('fetchAll')
                           ->willReturn([["nom" => "Organisateur 1"], ["nom" => "Organisateur 2"]]);
    
        $pdoStatementMock3->expects($this->once())
                           ->method('execute')
                           ->willReturn(true);
        $pdoStatementMock3->expects($this->once())
                           ->method('fetchAll')
                           ->willReturn([["nom" => "Scene 1"], ["nom" => "Scene 2"]]);
    
        $pdoStatementMock4->expects($this->once())
                           ->method('execute')
                           ->willReturn(true);
        $pdoStatementMock4->expects($this->once())
                           ->method('fetchAll')
                           ->willReturn([["titre" => "Spectacle 1", "duree" => "2 heures", "categorie" => "Categorie 1"], ["titre" => "Spectacle 2", "duree" => "1 heure", "categorie" => "Categorie 2"]]);
    
        // WHEN: Appel de la fonction getFestivalInfo
        $result = getFestivalInfo($pdoMock, 1);
    
        // THEN: Vérification du résultat attendu
        $this->assertEquals([200, [
            "festival" => ["titre" => "Festival Test", "description" => "Description du festival", "nom" => "Catégorie Test", "dateDebut" => "2024-03-19", "dateFin" => "2024-03-21"],
            "organisateurs" => [["nom" => "Organisateur 1"], ["nom" => "Organisateur 2"]],
            "scenes" => [["nom" => "Scene 1"], ["nom" => "Scene 2"]],
            "spectacles" => [["titre" => "Spectacle 1", "duree" => "2 heures", "categorie" => "Categorie 1"], ["titre" => "Spectacle 2", "duree" => "1 heure", "categorie" => "Categorie 2"]]
        ]], $result);
    }

    /**
     * Teste la fonction getFestivalInfo avec un identifiant festival invalide.
     */
    public function testGetFestivalInfoInvalidFestivalID(): void {
        // GIVEN: Initialisation du mock PDO 
        $pdoMock = $this->createMock(PDO::class);
        $statementMock = $this->createMock(PDOStatement::class);

        $pdoMock->method('prepare')->willReturn($statementMock);
        $statementMock->method('execute')->willReturn(true);
        $statementMock->method('fetchAll')->willReturn([]);
        $statementMock->method('fetch')->willReturn([]);

        // WHEN: Appel de la fonction à tester avec un identifiant utilisateur invalide (-1)
        $result = getFestivalInfo($pdoMock, -1);

        // THEN: Vérification du résultat attendu
        // Resultat attendu = message d'erreur + message avec erreur
        $this->assertEquals([
            0 => 500, 
            1 => [
                'message' => 'Erreur: Festival not found'
            ],
        ], $result);
    }

    /**
     * Teste la fonction getFestivalInfo lorsqu'aucun festival n'est trouvé pour un utilisateur donné.
     */
    public function testGetFestivalInfoAucunFestivalTrouve(): void {
        // GIVEN: Initialisation du mock PDO avec un comportement renvoyant une liste vide de festivals
        $pdoMock = $this->createMock(PDO::class);
        $statementMock = $this->createMock(PDOStatement::class);

        $pdoMock->method('prepare')->willReturn($statementMock);
        $statementMock->method('execute')->willReturn(true);
        $statementMock->method('fetchAll')->willReturn([]);
        $statementMock->method('fetch')->willReturn([]);

        // WHEN: Appel de la fonction à tester
        $result = getFestivalInfo($pdoMock, 1);

        // THEN: Vérification du résultat attendu
        $this->assertEquals([
            0 => 500, 
            1 => [
                'message' => 'Erreur: Festival not found'
            ],
        ], $result);
    }

    /**
     * Teste la fonction getFestivalInfo en cas d'erreur interne du serveur.
     */
    public function testGetInfoFestivalServerError(): void {
        // GIVEN: Initialisation du mock PDO avec un comportement générant une exception
        $pdoMock = $this->createMock(PDO::class);
        $pdoMock->method('prepare')->willThrowException(new Exception('Erreur interne'));

        // WHEN: Appel de la fonction à tester
        $result = getFestivalInfo($pdoMock, 1);

        // THEN: Vérification du résultat attendu
        $this->assertEquals([500, ["message" => "Erreur: Erreur interne"]], $result);
    }

}