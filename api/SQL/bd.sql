USE festiplandroid;
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

CREATE TABLE Utilisateur (
    idUtilisateur INT PRIMARY KEY AUTO_INCREMENT,
    prenom VARCHAR(30) NOT NULL,
    nom VARCHAR(35) NOT NULL,
    mail VARCHAR(50) NOT NULL UNIQUE,
    login VARCHAR(35) NOT NULL UNIQUE,
    mdp VARCHAR(30) NOT NULL,
    APIKey VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Création de la table CategorieFestival
CREATE TABLE CategorieFestival (
    idCategorie INT(11) NOT NULL AUTO_INCREMENT,
    nom VARCHAR(35) NULL,
    PRIMARY KEY (idCategorie)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Festival (
    idFestival INT(11) NOT NULL AUTO_INCREMENT,
    categorie INT(11) NOT NULL,
    titre VARCHAR(35) NULL,
    description VARCHAR(1000) NULL,
    dateDebut DATE NOT NULL,
    dateFin DATE NOT NULL,
    illustration VARCHAR(50) NULL,
    PRIMARY KEY (idFestival)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
ALTER TABLE Festival
ADD FOREIGN KEY (categorie) REFERENCES CategorieFestival(idCategorie);

CREATE TABLE EquipeOrganisatrice (
    idUtilisateur INT(11) NOT NULL,
    idFestival INT(11) NOT NULL,
    responsable BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (idUtilisateur, idFestival)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
ALTER TABLE EquipeOrganisatrice
ADD FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur);
ALTER TABLE EquipeOrganisatrice
ADD FOREIGN KEY (idFestival) REFERENCES Festival(idFestival);

CREATE TABLE Spectacle (
    idSpectacle INT(11) NOT NULL AUTO_INCREMENT,
    titre VARCHAR(50) NOT NULL,
    description VARCHAR(1000) NULL,
    duree TIME NOT NULL,
    illustration VARCHAR(50) NULL,
    categorie INT(11),
    tailleSceneRequise INT(11),
    PRIMARY KEY (idSpectacle)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE SpectacleOrganisateur (
    idUtilisateur INT(11) NOT NULL,
    idSpectacle INT(11) NOT NULL,
    PRIMARY KEY (idUtilisateur, idSpectacle)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
ALTER TABLE SpectacleOrganisateur
ADD FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur);
ALTER TABLE SpectacleOrganisateur
ADD FOREIGN KEY (idSpectacle) REFERENCES Spectacle(idSpectacle);

CREATE TABLE SpectacleDeFestival (
    idSpectacle INT(11) NOT NULL,
    idFestival INT(11) NOT NULL,
    PRIMARY KEY (idSpectacle, idFestival)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
ALTER TABLE SpectacleDeFestival
ADD FOREIGN KEY (idSpectacle) REFERENCES Spectacle(idSpectacle);
ALTER TABLE SpectacleDeFestival
ADD FOREIGN KEY (idFestival) REFERENCES Festival(idFestival);

CREATE TABLE CategorieSpectacle (
    idCategorie INT(11) NOT NULL AUTO_INCREMENT,
    nomCategorie VARCHAR(35) NOT NULL,
    PRIMARY KEY (idCategorie)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE Spectacle
ADD FOREIGN KEY (categorie) REFERENCES CategorieSpectacle(idCategorie);



-- Création de la table Intervenant
CREATE TABLE Intervenant (
  idIntervenant INT(11) NOT NULL AUTO_INCREMENT,
  idSpectacle INT(11) NOT NULL,
  nom VARCHAR(35) NOT NULL,
  prenom VARCHAR(35) NOT NULL,
  mail VARCHAR(50) NOT NULL,
  surScene INT(1) NOT NULL,
  typeIntervenant INT(11) NOT NULL,
  PRIMARY KEY (idIntervenant)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE MetierIntervenant (
    idMetierIntervenant INT(11) NOT NULL AUTO_INCREMENT,
    metier VARCHAR(50) NOT NULL,
    PRIMARY KEY (idMetierIntervenant)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE Intervenant
ADD FOREIGN KEY (typeIntervenant) REFERENCES MetierIntervenant(idMetierIntervenant);


CREATE TABLE Scene (
    idScene INT(11) NOT NULL AUTO_INCREMENT,
    taille INT(11) NOT NULL,
    nombreSpectateurs INT(6) NULL,
    longitude NUMERIC(8,5) NULL,
    latitude NUMERIC(8,5) NULL,
    nom VARCHAR(35) NULL,
    PRIMARY KEY (idScene)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Taille (
    idTaille INT(11) NOT NULL AUTO_INCREMENT,
    nom VARCHAR(35) NULL,
    PRIMARY KEY (idTaille)
);


ALTER TABLE Scene
ADD FOREIGN KEY (taille) REFERENCES Taille(idTaille);
ALTER TABLE Spectacle
ADD FOREIGN KEY (tailleSceneRequise) REFERENCES Taille(idTaille);

CREATE TABLE Grij (
    idGrij INT(11) NOT NULL AUTO_INCREMENT,
    heureDebut TIME NULL,
    heureFin TIME NULL,
    tempsEntreSpectacle TIME NULL,
    PRIMARY KEY (idGrij),
    FOREIGN KEY (idGrij) REFERENCES Festival(idFestival)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE Jour (
    idJour INT(11) NOT NULL AUTO_INCREMENT,
    idGrij INT(11) NOT NULL,
    dateDuJour DATE NOT NULL,
    PRIMARY KEY (idJour),
    FOREIGN KEY (idGrij) REFERENCES Grij(idGrij)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE CauseSpectacleNonPlace (
    idCause INT(11) NOT NULL AUTO_INCREMENT,
    intitule VARCHAR(35) NULL,
    PRIMARY KEY (idCause)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE SpectaclesJour (
    idFestival INT(11) NOT NULL,
    idJour INT(11) NULL,
    idSpectacle INT(11) NOT NULL,
    ordre INT(3) NOT NULL DEFAULT 0,
    place TINYINT NOT NULL DEFAULT 0,
    heureDebut TIME NULL,
    heureFin TIME NULL,
    idCauseNonPlace INT(11) NULL,
    PRIMARY KEY (idFestival, idSpectacle),
    FOREIGN KEY (idJour) REFERENCES Jour(idJour),
    FOREIGN KEY (idSpectacle) REFERENCES Spectacle(idSpectacle),
    FOREIGN KEY (idFestival) REFERENCES Festival(idFestival),
    FOREIGN KEY (idCauseNonPlace) REFERENCES CauseSpectacleNonPlace(idCause)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE SpectacleScenes (
    idFestival INT(11) NOT NULL,
    idSpectacle INT(11) NOT NULL,
    idScene INT(11) NOT NULL,
    PRIMARY KEY (idFestival, idSpectacle, idScene),
    FOREIGN KEY (idScene) REFERENCES Scene(idScene),
    FOREIGN KEY (idSpectacle) REFERENCES Spectacle(idSpectacle),
    FOREIGN KEY (idFestival) REFERENCES Festival(idFestival)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE FestivalFavoris
(
    idFestival    INT(11) NOT NULL,
    idUtilisateur INT(11) NOT NULL,
    PRIMARY KEY (idFestival, idUtilisateur),
    FOREIGN KEY (idFestival) REFERENCES Festival (idFestival),
    FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur (idUtilisateur)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



-- Données insérées
INSERT INTO CategorieFestival (nom)
VALUES
('Musique'),
('Théatre'),
('Cirque'),
('Danse'),
('Projection de film');

INSERT INTO CategorieSpectacle (nomCategorie)
VALUES
('Concert'),
('Piece de theatre'),
('Cirque'),
('Danse'),
('Projection de film');


INSERT INTO Taille (nom)
VALUES  
('Petite'),
('Moyenne'),
('Grande');

INSERT INTO CauseSpectacleNonPlace (intitule)
VALUES
('Durée trop longue'),
('Plus de jour disponible'),
('Pas de scène adéquate');

INSERT INTO MetierIntervenant (metier)
VALUES
('Acteur'),
('Danseur'),
('Chanteur'),
('Régisseur'),
('Comédien'),
('Maquilleur'),
('Habilleur'),
('Scènographe'),
('Eclairagiste');

INSERT INTO Scene (taille, nom, nombreSpectateurs, longitude, latitude)
VALUES (1, 'scene1', 30, 12.12121, 12.12121),
(1, 'scene2', 33, 12.12121, 12.12121),
(2, 'scene3', 120, 12.12121, 12.12121),
(3, 'scene4', 500, 12.12121, 12.12121),
(3, 'scene5', 503, 12.12121, 12.12121);

INSERT INTO Festival (categorie, titre, description, dateDebut, dateFin, illustration)
VALUES
    (1, 'Festival 1', 'Lorem ipsum', '2024-02-03', '2024-03-03', ''),
    (2, 'Festival 2', 'Lorem ipsum', '2024-03-10', '2024-07-10', ''),
    (3, 'Festival 3', 'Lorem ipsum', '2024-09-18', '2024-10-15', '');


INSERT INTO Utilisateur (prenom, nom, mail, login, mdp)
VALUES
    ('Jean', 'Dupont', 'dupontJean@gmail.com', 'jean','123'),
    ('Paul', 'Durand', 'durandPaul@gmail.com', 'paul','123'),
    ('Jacques', 'Martin', 'martinJaques@gmail.com', 'jacques','123'),
    ('Marie', 'Dupont', 'dupontMarie@gmail.com', 'marie','123'),
    ('Jeanne', 'Durand', 'durandJeanne@gmail.com', 'jeanne','123');

INSERT INTO EquipeOrganisatrice (idUtilisateur, idFestival, responsable)
VALUES
    (2,1,0),
    (3,1,0),
    (4,2,0),
    (5,3,0),
    (6,1,1);

INSERT INTO Spectacle (titre, description, duree, illustration, categorie, tailleSceneRequise)
VALUES
    ('Concert de rock', 'Lorem ipsum', '01:30:00', '', 1, 3),
    ('La vie est belle', 'Lorem ipsum', '02:00:00', '', 2, 2),
    ('Cirque du soleil', 'Lorem ipsum', '01:30:00', '', 3, 3),
    ('Danse avec les stars', 'Lorem ipsum', '01:30:00', '', 4, 2),
    ('Projection de film', 'Lorem ipsum', '02:00:00', '', 5, 1);


INSERT INTO SpectacleScenes (idFestival, idSpectacle, idScene)
VALUES
    (1, 1, 1),
    (1, 2, 2),
    (1, 3, 3),
    (1, 4, 4),
    (1, 5, 5);

INSERT INTO FestivalFavoris (idFestival, idUtilisateur)
VALUES
    (2, 2),
    (3, 3),
    (2, 3),
    (1, 4),
    (2, 4),
    (3, 4),
    (1, 5);