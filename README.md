# Projet IA04

## Procédure sur Git :

Sur master :
```
git pull -r (on récupère la dernière version du dépôt)
git checkout -b ma-nouvelle-branche (on crée et on va sur une nouvelle branche pour travailler)
```

Sur ma-nouvelle-branche :
```
git commit (on fait ses commits)
git commit
...
git push (si c'est le premier push il va vous afficher la commande à écrire)
git checkout master (travail terminé et qui fonctionne, on retourne sur master)
```

Sur master :
```
git pull -r (on récupère les dernières modifs)
git checkout ma-nouvelle-branche (on retourne sur la branche)
```

Sur ma-nouvelle-branche :
```
git rebase master (pour rejouer nos commits après ceux de master pour plus de lisibilité dans l'historique)
git push -f (on force le push pour écraser l'ancien historique qui n'est plus bon)
git checkout master (on retourne sur master)
```

Sur master :
```
git merge --no-ff ma-nouvelle-branche (on fusionne notre branche sans la remettre à plat)
git push (on sauvegarde le tout)
```

## Possibilités d'action des différents Agents :

### Humain :

 * Percevoir l'environnement autour de lui.
 * Se déplacer.
 * Consommer de la nourriture sur une cellule adjacente.
 * Se reproduire avec une personne du sexe opposé sur une cellule adjacente.
 * Mourir.

### Médecin :

 * A les mêmes possiblités d'action qu'un humain.
 * Ramasser des médicaments sur une cellule adjacente.
 * Tenter une opération sur un humain sur une cellule adjacente (Rendre des points de vie, soigner une maladie, vacciner).

### Nourriture :

 * Peut-être consommée.
 * Pourrir un peu plus à chaque tour.

### Virus :

 * Disparaître.
 * Se déplacer.
 * Infecter des humains.











