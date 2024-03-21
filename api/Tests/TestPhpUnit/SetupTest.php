<?php

// Dans ce fichier lorsque on execute tests.bat
// On appelle phpunit avec tout les dossiers 
// On doit donc require une seule fois chaque require
// Et non 1 fois par fichier
// Ici donc vous retrouverez tout les require nécessaire
require __DIR__ . '/../../Services/ApiService.php';

// Pour phpunit
use PHPUnit\Framework\TestCase;

// Classe pour ne pas afficher d'erreur
class SetupTest extends TestCase {

    // Fonction juste pour ne pas afficher d'erreur dans la console
    public function testPourAfficherAucuneErreurSurLaConsole(): void {
        $this->assertEquals(true, 0 == 0);
    }

}