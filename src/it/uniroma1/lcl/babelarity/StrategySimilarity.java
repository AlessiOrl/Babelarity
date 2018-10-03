package it.uniroma1.lcl.babelarity;

import it.uniroma1.lcl.babelarity.LinguisticObject;
import it.uniroma1.lcl.babelarity.Word;

public interface StrategySimilarity
{
    double computeSimilarity(LinguisticObject o, LinguisticObject o2);
}
