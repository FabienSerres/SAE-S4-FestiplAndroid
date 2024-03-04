<?php


function getAllFestivals($idUtilisateur) {
    try {
        $pdo = connecteBD();

        $sql = "SELECT idFestival
                FROM FestivalFavoris
                WHERE idUtilisateur = :idUtilisateur";
        $stmt = $pdo->prepare($sql);
        $stmt->bindParam("idUtilisateur", $idUtilisateur);
        $stmt->execute();
        $fav = [];
        while($row = $stmt->fetch()) {
            $fav[] = $row["idFestival"];
        }

        $sql = "SELECT idFestival, titre
                FROM Festival";
        $stmt = $pdo->prepare($sql);
        $stmt->execute();

        $i=1;
        while($row = $stmt->fetch()) {
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

function getFestivalInfo($id) {
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

function getFavoriteFestivals($id) {
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

function deleteFavoritreFestival($idFestival, $idUtilisateur) {
    try {
        $pdo = connecteBD();

        $sql = "DELETE FROM FestivalFavoris
                WHERE idFestival = :idF
                ANd idUtilisateur = :idU";

        $stmt = $pdo->prepare($sql);
        $stmt->bindParam(':id', $id);
        $stmt->bindParam(':idU', $idU);
        $stmt->execute();

        $stmt->closeCursor();

        sendJson(200, "Festival supprimé des favoris");

    } catch (PDOException $e) {
        $infos["message"] = "Erreur: " .$e->getMessage();
        sendJson(500, $infos);
    }
}

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

        $request = "SELECT 1 FROM Utilisateur WHERE login = ? AND mdp = ?";
        $pdo = connecteBD();

        $stmt = $pdo->prepare($request);
        $stmt->execute([$login, $password]);

        $data = $stmt->fetchAll();

        if (count($data) > 0) {
            if ($data[0][1] == 1) {
                $key = CreateHeaderFromLoginPassword($login, $password);

                $request2 = "UPDATE Utilisateur SET APIKey = ? WHERE login = ? AND mdp = ?";
                $stmt2 = $pdo->prepare($request2);

                $stmt2->execute([$key, $login, $password]);

                $infos["apiKey"] = $key;
                sendJson(200, $infos);
            }
        }

        $infos["message"] = "Login et password invalide.";
        sendJson(400, $infos);

    } catch(Exception $e) {
        $infos["message"] = "Erreur: " .$e->getMessage();
        sendJson(500, $infos);
    }
}