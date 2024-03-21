<?php

use PHPUnit\Framework\TestCase;

class AuthentificationTest extends TestCase {

    /**
     * Test lorsque l'authentification réussit avec les informations correctes.
     */
    public function testAuthenticationSuccess(): void {
        // GIVEN: Initialisation du mock PDO avec un comportement attendu
        $pdoMock = $this->createMock(PDO::class);
        $pdoStatementMock = $this->createMock(PDOStatement::class);
    
        $pdoMock->expects($this->exactly(2))
                ->method('prepare')
                ->willReturn($pdoStatementMock);
    
        $pdoStatementMock->expects($this->exactly(2))
                        ->method('execute')
                        ->willReturn(true);
    
        $pdoStatementMock->expects($this->once())
                        ->method('fetchAll')
                        ->willReturn([['idUtilisateur' => 1]]);
                   
        // Fonction utilisé dans authentification temporairement réecrite pour test
        // Inner function non supporté par phpstan mais ici nécessaires pour 
        // pouvoir l'utilisé par la suite dans la fonction authentification
        function CreateHeaderFromLoginPassword(): string {
            return "fake_api_key";
        }

        // WHEN: Appel de la fonction authentification
        $result = authentification($pdoMock, 'user', 'password');
    
        // THEN: Vérification du résultat attendu
        $this->assertEquals([200, ["apiKey" => "fake_api_key", "id" => 1]], $result);
    }

    /**
     * Test lorsque le login est vide.
     */
    public function testAuthenticationEmptyLogin(): void {
        // GIVEN: Initialisation du mock PDO avec un comportement attendu
        $pdoMock = $this->createMock(PDO::class);

        // WHEN: Appel de la fonction authentification avec un login vide
        $result = authentification($pdoMock, '', 'password');

        // THEN: Vérification du résultat attendu
        $this->assertEquals([400, ["message" => "Login vide."]], $result);
    }

    /**
     * Test lorsque le mot de passe est vide.
     */
    public function testAuthenticationEmptyPassword(): void {
        // GIVEN: Initialisation du mock PDO avec un comportement attendu
        $pdoMock = $this->createMock(PDO::class);

        // WHEN: Appel de la fonction authentification avec un mot de passe vide
        $result = authentification($pdoMock, 'user', '');

        // THEN: Vérification du résultat attendu
        $this->assertEquals([400, ["message" => "Password vide."]], $result);
    }

    /**
     * Test lorsque le login et le mot de passe sont incorrects.
     */
    public function testAuthenticationInvalidCredentials(): void {
        // GIVEN: Initialisation du mock PDO avec un comportement attendu
        $pdoMock = $this->createMock(PDO::class);
        $pdoStatementMock = $this->createMock(PDOStatement::class);

        $pdoMock->expects($this->once())
                ->method('prepare')
                ->willReturn($pdoStatementMock);

        $pdoStatementMock->expects($this->once())
                        ->method('execute')
                        ->willReturn(true);

        $pdoStatementMock->expects($this->once())
                        ->method('fetchAll')
                        ->willReturn([]);

        // WHEN: Appel de la fonction authentification avec des identifiants invalides
        $result = authentification($pdoMock, 'invalid_user', 'invalid_password');

        // THEN: Vérification du résultat attendu
        $this->assertEquals([400, ["message" => "Login et password invalide."]], $result);
    }

    /**
     * Teste la fonction authentification en cas d'erreur interne du serveur.
     */
    public function testAuthenticationServerError(): void {
        // GIVEN: Initialisation du mock PDO avec un comportement générant une exception
        $pdoMock = $this->createMock(PDO::class);
        $pdoMock->method('prepare')->willThrowException(new Exception('Erreur interne'));

        // WHEN: Appel de la fonction à tester
        $result = authentification($pdoMock, 'user', 'password');

        // THEN: Vérification du résultat attendu
        $this->assertEquals([500, ["message" => "Erreur: Erreur interne"]], $result);
    }

}