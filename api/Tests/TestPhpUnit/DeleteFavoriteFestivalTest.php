<?php

use PHPUnit\Framework\TestCase;

class DeleteFavoriteFestivalTest extends TestCase {

    /**
     * Test lorsque le festival est supprimé avec succès des favoris de l'utilisateur.
     */
    public function testDeleteFavoriteFestivalSuccess(): void {
        // GIVEN: Initialisation du mock PDO avec un comportement attendu
        $pdoMock = $this->createMock(PDO::class);
        $pdoStatementMock1 = $this->createMock(PDOStatement::class);
        $pdoStatementMock2 = $this->createMock(PDOStatement::class);
    
        $pdoMock->expects($this->exactly(2)) // Attend une exécution
                ->method('prepare')
                ->willReturn($pdoStatementMock1, $pdoStatementMock2);
    
        $pdoStatementMock1->expects($this->once())
                           ->method('execute')
                           ->willReturn(true);
        $pdoStatementMock1->expects($this->once())
                           ->method('fetchAll')
                           ->willReturn([[1 => 1]]);

        $pdoStatementMock2->expects($this->once())
                           ->method('execute')
                           ->willReturn(true);

        // WHEN: Appel de la fonction à deleteFavoriteFestival
        $result = deleteFavoriteFestival($pdoMock, 1, 1);
    
        // THEN: Vérification du résultat attendu
        $this->assertEquals([200, ["message" => "Festival supprimé des favoris"]], $result);
    }
    
    public function testDeleteFavoriteFestivalNotFound(): void {
        // GIVEN: Initialisation du mock PDO avec un comportement attendu
        $pdoMock = $this->createMock(PDO::class);
        $pdoStatementMock1 = $this->createMock(PDOStatement::class);
        $pdoStatementMock2 = $this->createMock(PDOStatement::class);
    
        $pdoMock->expects($this->exactly(1)) // Attend deux exécutions
                ->method('prepare')
                ->willReturnOnConsecutiveCalls($pdoStatementMock1);
    
        $pdoStatementMock1->expects($this->once())
                           ->method('execute')
                           ->willReturn(true);
        $pdoStatementMock1->expects($this->once())
                           ->method('fetchAll')
                           ->willReturn([]);
    
        // WHEN: Appel de la fonction à deleteFavoriteFestival
        $result = deleteFavoriteFestival($pdoMock, 1, 1);
    
        // THEN: Vérification du résultat attendu
        $this->assertEquals([400, ["message" => "Le festival 1 n'est pas en favoris pour l'utilisateur 1"]], $result);
    }
    
    public function testDeleteFavoriteFestivalException(): void {
        // GIVEN: Initialisation du mock PDO avec un comportement attendu
        $pdoMock = $this->createMock(PDO::class);
    
        $pdoMock->expects($this->once()) // Attend une exécution
                ->method('prepare')
                ->willThrowException(new Exception("Erreur de préparation de la requête"));
    
        // WHEN: Appel de la fonction à deleteFavoriteFestival
        $result = deleteFavoriteFestival($pdoMock, 1, 1);
    
        // THEN: Vérification du résultat attendu
        $this->assertEquals([500, ["message" => "Erreur: Erreur de préparation de la requête"]], $result);
    }

    /**
     * Test lorsque le festival n'est pas présent dans les favoris de l'utilisateur.
     */
    public function testDeleteFavoriteFestivalNotPresent(): void {
        // GIVEN: Initialisation du mock PDO avec un comportement attendu
        $pdoMock = $this->createMock(PDO::class);
        $pdoStatementMock = $this->createMock(PDOStatement::class);
    
        $pdoMock->expects($this->once())
                ->method('prepare')
                ->willReturn($pdoStatementMock);
    
        // Exécution pour tester si le festival est présent dans les favoris de l'utilisateur
        $pdoStatementMock->expects($this->once())
                        ->method('execute')
                        ->willReturn(true); // Le festival n'est pas présent dans les favoris de l'utilisateur
    
        $pdoStatementMock->expects($this->once())
                        ->method("fetchAll")
                        ->willReturn([]);

        // WHEN: Appel de la fonction deleteFavoriteFestival
        $result = deleteFavoriteFestival($pdoMock, 1, 1);
    
        // THEN: Vérification du résultat attendu
        $this->assertEquals([400, ["message" => "Le festival 1 n'est pas en favoris pour l'utilisateur 1"]], $result);
    }

    /**
     * Teste la fonction deleteFavoriteFestival en cas d'erreur interne du serveur.
     */
    public function testDeleteFavoriteFestivalServerError(): void {
        // GIVEN: Initialisation du mock PDO avec un comportement générant une exception
        $pdoMock = $this->createMock(PDO::class);
        $pdoMock->method('prepare')->willThrowException(new Exception('Erreur interne'));

        // WHEN: Appel de la fonction à tester
        $result = deleteFavoriteFestival($pdoMock, 1, 1);

        // THEN: Vérification du résultat attendu
        $this->assertEquals([500, ["message" => "Erreur: Erreur interne"]], $result);
    }

}