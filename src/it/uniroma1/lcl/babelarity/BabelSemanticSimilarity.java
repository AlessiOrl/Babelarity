package it.uniroma1.lcl.babelarity;

public class BabelSemanticSimilarity implements SemanticSimilarityStrategy
{
    private static BabelSemanticSimilarity instance;


    private BabelSemanticSimilarity(){}

    public static BabelSemanticSimilarity getInstance(){
        if (instance == null) instance = new BabelSemanticSimilarity();
        return instance;
    }

    @Override
    public double computeSimilarity(LinguisticObject o, LinguisticObject o2)
    {
        return 0;
    }
    // computeSimilarity(it.uniroma1.lcl.babelarity.Synset s1, it.uniroma1.lcl.babelarity.Synset s2): restituisce il valore di similarità      sotto forma di double
}
