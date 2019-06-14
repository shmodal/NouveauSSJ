# SSJ Library

Packages being currently developped :
- Networks (implements graphs and links)
- Networks.staticreliability (sub package with specific graph structures and algorithms for static reliability)
- Splitting (implements Generalized Splitting)

Splitting implements a general version of Generalized Splitting and AdamSplitting, which can be applied to other problems than networks. It uses a subclass of MarkovChain, MarkovChainWithImportance, adapted to splitting algorithms.
We have two examples of splitting aglos : a simple bivariate uniform example, and the computation of networks static reliability.

Networks

Networks.staticreliability has GraphWithForest(Forest structure for connectivity) classic algorithms (PMC,Turnip,etc.), MarkovChains structures (MarkovChainConnectivityState et al, with an importance function used for networks)




A faire :
 
 - exemples basiques splitting(aire)     DONE
 - mise à jour du code pour static reliability avec les nouvelles structures de graphes
 - mise à jour PMC Turnip
 
 - finir re coder PMC flow reliability et tester 
 - finir coder Edmond Karps pour max flow
 - coder GS pour flow
 
 
 - exemples de richard à intégrer
 - overview et documentation à compléter

