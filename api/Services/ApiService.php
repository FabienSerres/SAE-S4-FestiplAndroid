<?php

/**
 * Fonction pour obtenir la liste de tous les festivals avec leur statut de favoris pour un utilisateur donné.
 *
 * Cette fonction permet de récupérer la liste de tous les festivals ainsi que leur statut de favoris pour un
 * utilisateur donné.
 *
 * @param int $idUtilisateur L'identifiant de l'utilisateur pour lequel on veut obtenir la liste des festivals.
 *
 * @return void
 */
function getAllFestivals(int $idUtilisateur): void {
    try {
        $pdo = connecteBD();

        $sql = "SELECT idFestival
                FROM FestivalFavoris
                WHERE idUtilisateur = :idUtilisateur";
        $stmt = $pdo->prepare($sql);
        $stmt->bindParam("idUtilisateur", $idUtilisateur);
        $stmt->execute();
        $fav = [];
        $data1 = $stmt->fetchAll();

        foreach($data1 as $row) {
            $fav[] = $row["idFestival"];
        }

        $sql = "SELECT idFestival, titre
                FROM Festival";
        $stmt = $pdo->prepare($sql);
        $stmt->execute();
        $data2 = $stmt->fetchAll();

        $festivals = null;

        $i=1;
        foreach($data2 as $row) {
            $festivals[$i]["idFestival"] = $row["idFestival"];
            $festivals[$i]["titre"] = $row["titre"];
            if(in_array($row["idFestival"], $fav)) {
                $festivals[$i]["favoris"] = true;
            } else {
                $festivals[$i]["favoris"] = false;
            }
            $i++;
        }

        $stmt->closeCursor();
        $stmt=null;
        $pdo=null;

        sendJson(200, $festivals);

    } catch(Exception $e) {
        $infos["message"] = "Erreur: " .$e->getMessage();
        sendJson(500, $infos);
    }
}

/**
 * Fonction pour obtenir les informations détaillées sur un festival.
 *
 * Cette fonction permet de récupérer les informations détaillées sur un festival
 * à partir de son identifiant, y compris le titre, la description, les organisateurs,
 * la catégorie, les dates de début et de fin, ainsi que les scènes où se déroulent
 * les spectacles du festival.
 *
 * @param int $id L'identifiant du festival dont on veut obtenir les informations.
 *
 * @return void
 */
function getFestivalInfo(int $id): void {
    try{
        $pdo = connecteBD();

        $sql = "SELECT Festival.titre, Festival.description, Utilisateur.nom, CategorieFestival.nom, Festival.dateDebut, Festival.dateFin
                FROM Festival
                JOIN EquipeOrganisatrice
                ON Festival.idFestival = EquipeOrganisatrice.idFestival
                JOIN Utilisateur
                ON EquipeOrganisatrice.idUtilisateur = Utilisateur.idUtilisateur
                JOIN CategorieFestival
                ON Festival.categorie = CategorieFestival.idCategorie
                WHERE Festival.idFestival = :id
                AND EquipeOrganisatrice.responsable = 1";

        $stmt = $pdo->prepare($sql);
        $stmt->bindParam(':id', $id);
        $stmt->execute();

        $result["festival"] = $stmt->fetch();

        $sql = "SELECT nom, prenom
                FROM Utilisateur
                JOIN EquipeOrganisatrice
                ON EquipeOrganisatrice.idUtilisateur = Utilisateur.idUtilisateur
                WHERE EquipeOrganisatrice.idFestival = :id";

        $stmt = $pdo->prepare($sql);
        $stmt->bindParam(':id', $id);
        $stmt->execute();

        $result["organisateurs"] = $stmt->fetchAll();

        $sql = "SELECT Scene.nom
                FROM Scene
                JOIN SpectacleScenes
                ON Scene.idScene = SpectacleScenes.idScene
                WHERE SpectacleScenes.idFestival = :id";

        $stmt = $pdo->prepare($sql);
        $stmt->bindParam(':id', $id);
        $stmt->execute();

        $result["scenes"] = $stmt->fetchAll();

        $stmt->closeCursor();

        sendJson(200, $result);

    } catch (PDOException $e) {
        $infos["message"] = "Erreur: " .$e->getMessage();
        sendJson(500, $infos);
    }
}

/**
 * Fonction pour obtenir la liste des festivals favoris d'un utilisateur.
 *
 * Cette fonction permet de récupérer la liste des festivals favoris d'un utilisateur
 * à partir de la base de données en fonction de son identifiant.
 *
 * @param int $id L'identifiant de l'utilisateur dont on veut obtenir les festivals favoris.
 *
 * @return void
 */
function getFavoriteFestivals(int $id): void {
    try {
        $pdo = connecteBD();

        $sql = "SELECT Festival.titre, Festival.idFestival
                FROM Festival
                JOIN FestivalFavoris
                ON Festival.idFestival = FestivalFavoris.idFestival
                WHERE FestivalFavoris.idUtilisateur = :id";

        $stmt = $pdo->prepare($sql);
        $stmt->bindParam(':id', $id);
        $stmt->execute();

        $result = $stmt->fetchAll();

        $stmt->closeCursor();

        sendJson(200, $result);
    } catch (PDOException $e) {
        $infos["message"] = "Erreur: " .$e->getMessage();
        sendJson(500, $infos);
    }
}

/**
 * Fonction pour supprimer un festival des favoris d'un utilisateur.
 *
 * Cette fonction permet de supprimer un festival des favoris d'un utilisateur
 * en supprimant l'entrée correspondante dans la table FestivalFavoris de la base de données.
 *
 * @param int $idFestival    L'identifiant du festival à supprimer des favoris.
 * @param int $idUtilisateur L'identifiant de l'utilisateur dont le festival doit être supprimé des favoris.
 *
 * @return void
 */
function deleteFavoriteFestival(int $idUtilisateur, int $idFestival): void{
    try {
        $pdo = connecteBD();

        $sqlEstPresent = "SELECT 1 FROM FestivalFavoris
                          WHERE idFestival = ?
                          AND idUtilisateur = ?";

        $stmtTest = $pdo->prepare($sqlEstPresent);
        $stmtTest->execute([$idFestival, $idUtilisateur]);
        $data = $stmtTest->fetchAll();

        if (!count($data) > 0) {
            $infos["message"] = "Le festvial " . $idFestival . " n'est pas en favoris pour l'utilisateur " . $idUtilisateur;
            sendJson(400, $infos);
        } else if ($data[0][1] != 1) {
            $infos["message"] = "Le festvial " . $idFestival . " n'est pas en favoris pour l'utilisateur " . $idUtilisateur;
            sendJson(400, $infos);
        }

        $sql = "DELETE FROM FestivalFavoris
                WHERE idFestival = :idF
                ANd idUtilisateur = :idU";

        $stmt = $pdo->prepare($sql);
        $stmt->bindParam(':idF', $idFestival);
        $stmt->bindParam(':idU', $idUtilisateur);
        $stmt->execute();

        $stmt->closeCursor();

        $infos["message"] = "Festival supprimé des favoris";
        sendJson(200, $infos);

    } catch (PDOException $e) {
        $infos["message"] = "Erreur: " .$e->getMessage();
        sendJson(500, $infos);
    }
}

/**
 * Fonction pour ajouter un festival aux favoris d'un utilisateur.
 *
 * Cette fonction permet d'ajouter un festival aux favoris d'un utilisateur
 * en insérant une nouvelle entrée dans la table FestivalFavoris de la base de données.
 *
 * @param int $idFestival    L'identifiant du festival à ajouter aux favoris.
 * @param int $idUtilisateur L'identifiant de l'utilisateur auquel le festival doit être ajouté aux favoris.
 *
 * @return void
 */
function addFavoriteFestival(int $idUtilisateur, int $idFestival): void {
    try {
        $pdo = connecteBD();
        
        $sqlDejaFav = "SELECT idFestival, idUtilisateur FROM FestivalFavoris WHERE idFestival = :idF AND idUtilisateur = :idU";
        $stmt = $pdo->prepare($sqlDejaFav);
        $stmt->bindParam(':idF', $idFestival);
        $stmt->bindParam(':idU', $idUtilisateur);
        $stmt-> execute();
        
        if ($stmt->rowCount() == 0) {
            $sql = "INSERT INTO FestivalFavoris (idFestival, idUtilisateur)
                VALUES (:idF, :idU)";

            $stmt = $pdo->prepare($sql);
            $stmt->bindParam(':idF', $idFestival);
            $stmt->bindParam(':idU', $idUtilisateur);
            $stmt->execute();

            $stmt->closeCursor();

            $infos["message"] = "Festival ajouté aux favoris";
            sendJson(200, $infos);
        } else {
            $infos["message"] = "Festival déjà ajouté en favoris";
            sendJson(400, $infos);
        }

        

    } catch (PDOException $e) {
        $infos["message"] = "Erreur: " .$e->getMessage();
        sendJson(500, $infos);
    }
}

/**
 * Fonction d'authentification utilisateur.
 *
 * Cette fonction permet de vérifier les informations de connexion d'un utilisateur
 * en comparant le login et le mot de passe fournis avec ceux enregistrés dans la base de données.
 *
 * @param string $login    Le login de l'utilisateur.
 * @param string $password Le mot de passe de l'utilisateur.
 *
 * @return void
 */
function authentification(string $login, string $password): void {
    try {

        if (empty($login)) {
            $infos["message"] = "Login vide.";
            sendJson(400, $infos);
        }

        if (empty($login)) {
            $infos["message"] = "Password vide.";
            sendJson(400, $infos);
        }

        $login = htmlspecialchars($login);
        $password = htmlspecialchars($password);

        $request = "SELECT idUtilisateur FROM Utilisateur WHERE login = ? AND mdp = ?";
        $pdo = connecteBD();

        $stmt = $pdo->prepare($request);
        $stmt->execute([$login, $password]);

        $data = $stmt->fetchAll();

        if (count($data) > 0) {
            $key = CreateHeaderFromLoginPassword($login, $password);

            $request2 = "UPDATE Utilisateur SET APIKey = ? WHERE login = ? AND mdp = ?";
            $stmt2 = $pdo->prepare($request2);

            $stmt2->execute([$key, $login, $password]);

            $infos["apiKey"] = $key;
            $infos["id"] = $data[0]["idUtilisateur"];
            sendJson(200, $infos);
        }

        $infos["message"] = "Login et password invalide.";
        sendJson(400, $infos);

    } catch(Exception $e) {
        $infos["message"] = "Erreur: " .$e->getMessage();
        sendJson(500, $infos);
    }
}