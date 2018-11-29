package it.uniroma1.lcl.babelarity.strategy;

import it.uniroma1.lcl.babelarity.linguisticobject.LinguisticObject;

public interface SimilarityStrategy {

  double computeSimilarity(LinguisticObject o, LinguisticObject o2);
}
