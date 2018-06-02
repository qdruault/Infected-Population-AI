# Simulation de propagation de virus

Ce projet a été développé au sein de mon cursus d'ingénieur à l'UTC. Le but de ce projet est d'utiliser un système multi-agents pour modéliser une intelligence artificielle. Nous avons choisit de simuler une population au sein de laquelle un virus pourrait apparaître. Certains humains sont docteurs et peuvent donc soigner les malades. 

## Agents

### Humain

L'humain a pour objectif la survie. Il vieillit à chaque "step" et a également besoin de manger régulièrement. Sa stratégie est la suivante (par ordre décroissant d'importance) :
 * Se diriger vers un docteur s'il en a appelé un.
 * Chercher à manger en cas de faim importante
 * Appelle un docteur s'il est malade
 * Chercher à manger en cas de faim
 * Chercher à se reproduire sinon
 
Un humain peut mourir de vieillesse, de faim ou de maladie. Il est représenté par un cercle bleu pour les hommes et mauve pour les femmes.

### Docteur

Le docteur est un humain et a donc les mêmes besoins vitaux que lui. En revanche, si un humain malade se trouve à côté de lui, il le soigne immédiatement. Un docteur peut également se soigner lui-même. 

Il est représenté par un carré.

### Nourriture

La nourriture apparaît aléatoirement sur la carte. Chaque emplacement de nourriture contient un nombre de portions qui lui est propre. La nourriture pourrit avec le temps jusqu'à disparaître complétement. Les humains doivent être à coté de la nourriture pour la manger et ainsi réduire leur sensation de faim.

Le taux d'apparition de la nourriture est aléatoire ce qui peut provoquer des périodes de disette.

La nourriture est représentée par un cercle vert.

### Virus

Des virus peuvent apparaître sur la carte. Un virus a une durée de vie et un rayon de contamination. Il contamine les humains aux alentours qui deviennent alors malades et perdent de la santé. 

Le virus est représenté par un hexagone rouge et les humains contaminés deviennent orange.

## Démonstration

Voilà un aperçu de l'évolution de l'environnement avec un graphique présentant différentes statistiques.

![Demo](https://github.com/qdruault/Infected-Population-AI/blob/master/ia04.gif "Demo")

## Notes

Ceci reste un projet scolaire répondant à un cadre et à des délais précis. Beaucoup d'améliorations peuvent être apportées à cette application (ajout de cours d'eau, agrandissement de la carte, système de saisons, meilleure gestion du temps, différents types de virus, etc.). 

## Auteurs

Projet développé dans le cadre de l'UV IA04 à l'UTC par Baptiste DE FILIPPIS, Estelle DE MAGONDEAUX, Quentin DRUAULT-AUBIN, Louis GROISNE et Louise NAUDIN, 
