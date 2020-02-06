# API Compte

## API

POST /api/v1/compte/depense/:montant/:categorie

Permet de creer une nouvelle depense pour le mois courant dans une categorie designee


GET /api/v1/compte/depense/:categorie

Permet de recuperer les depenses de la categorie indique pour le mois courant

GET /api/v1/compte/depense/:categorie/:month/:year

Permet de recuperer les depenses de la categorie indique pour le mois indique

GET /api/v1/compte/categories

Permet de recuperer toutes les infos sur les categories( nom, budget..)


## Docker

Construire :

docker build -t compte .

Lancer

docker run -p 8877:8877  -v /c/Users/<USER WINDOWS>/.aws:/credentials/:ro compte