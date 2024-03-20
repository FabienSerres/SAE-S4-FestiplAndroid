<?php

use PHPUnit\Framework\TestCase;

class AddFavoriteFestivalTest extends TestCase {

    /**
     * Test lorsque le festival est ajouté avec succès aux favoris de l'utilisateur.
     */
    public function testAddFavoriteFestivalSuccess() {
        // GIVEN: Initialisation du mock PDO avec un comportement attendu
        $pdoMock = $this->createMock(PDO::class);
        $pdoStatementMock = $this->createMock(PDOStatement::class);

        $pdoMock->expects($this->exactly(2)) // Attend deux exécutions
                ->method('prepare')
                ->willReturn($pdoStatementMock);

        $pdoStatementMock->expects($this->exactly(2))
                        ->method('execute')
                        ->willReturn(true);

        // Exécution pour tester si le festival est déjà ajouté aux favoris de l'utilisateur
        $pdoStatementMock->expects($this->once())
                        ->method('rowCount')
                        ->willReturn(0); // Le festival n'est pas déjà ajouté aux favoris de l'utilisateur

        // WHEN: Appel de la fonction addFavoriteFestival
        $result = addFavoriteFestival($pdoMock, 1, 1);

        // THEN: Vérification du résultat attendu
        $this->assertEquals([200, ["message" => "Festival ajouté aux favoris"]], $result);
    }

    /**
     * Test lorsque le festival est déjà présent dans les favoris de l'utilisateur.
     */
    public function testAddFavoriteFestivalAlreadyAdded() {
        // GIVEN: Initialisation du mock PDO avec un comportement attendu
        $pdoMock = $this->createMock(PDO::class);
        $pdoStatementMock = $this->createMock(PDOStatement::class);

        $pdoMock->expects($this->once())
                ->method('prepare')
                ->willReturn($pdoStatementMock);

        // Exécution pour tester si le festival est déjà ajouté aux favoris de l'utilisateur
        $pdoStatementMock->expects($this->once())
                        ->method('execute')
                        ->willReturn(true); // Le festival est déjà ajouté aux favoris de l'utilisateur

        $pdoStatementMock->expects($this->once())
                        ->method('rowCount')
                        ->willReturn(1);

        // WHEN: Appel de la fonction addFavoriteFestival
        $result = addFavoriteFestival($pdoMock, 1, 1);

        // THEN: Vérification du résultat attendu
        $this->assertEquals([400, ["message" => "Festival déjà ajouté en favoris"]], $result);
    }

    /**
     * Teste la fonction addFavoriteFestival en cas d'erreur interne du serveur.
     */
    public function testAddFavoriteFestivalServerError() {
        // GIVEN: Initialisation du mock PDO avec un comportement générant une exception
        $pdoMock = $this->createMock(PDO::class);
        $pdoMock->method('prepare')->willThrowException(new Exception('Erreur interne'));

        // WHEN: Appel de la fonction à tester
        $result = addFavoriteFestival($pdoMock, 1, 1);

        // THEN: Vérification du résultat attendu
        $this->assertEquals([500, ["message" => "Erreur: Erreur interne"]], $result);
    }

}