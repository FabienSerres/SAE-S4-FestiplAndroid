# Aide pour phpunit (coverage)

## Installation et configuration de Xdebug pour PHPUnit
 
Pour exécuter les tests avec couverture de code, Xdebug doit être installé et configuré correctement sur votre système. Voici les étapes pour installer et configurer Xdebug :
 
### Étape 1 : Installation de Xdebug
 
1. **Vérifier si Xdebug est déjà installé** en exécutant `php -v` dans votre terminal. Si Xdebug est installé, vous devriez voir une mention d'Xdebug dans la sortie.
 
2. **Installer Xdebug** si ce n'est pas déjà fait. Vous pouvez utiliser l'[assistant d'installation de Xdebug](https://xdebug.org/wizard) pour obtenir des instructions spécifiques à votre configuration. Copiez la sortie de `php -i` ou les informations de votre page `phpinfo()` et collez-les dans le formulaire de l'assistant pour recevoir des instructions personnalisées.
 
### Étape 2 : Configuration de Xdebug pour la couverture de code
 
Après l'installation d'Xdebug, vous devez le configurer pour activer la couverture de code :
 
1. **Ouvrez votre fichier `php.ini`.** Vous pouvez trouver le chemin de ce fichier en exécutant `php --ini`.
 
2. **Ajoutez ou modifiez les lignes suivantes** dans le fichier `php.ini` pour configurer Xdebug :
 
   ```ini
   zend_extension=xdebug
   xdebug.mode=coverage
   xdebug.start_with_request=yes
   ```

### Étape 3 : Executer les tests

1. Ouvrez un terminal et éxecuté la commande suivante:
    ```cmd
    tests.bat
    ```

### Étape 4 : Visualisez les resultats

**Vous trouverez les resultats des test et du coverage dans** ```Tests/TestPhpUnit/Rapport```