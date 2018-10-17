package it.uniroma1.lcl.babelarity;

import java.util.HashSet;

public class BabelSemanticSimilarity implements SemanticSimilarityStrategy
{

    private static BabelSemanticSimilarity instance;
    private Synset isARoot;


    private BabelSemanticSimilarity() {
        isARoot = MiniBabelNet.getInstance().getSynset("entity");
    }

    public static BabelSemanticSimilarity getInstance()
    {
        if (instance == null) instance = new BabelSemanticSimilarity();
        return instance;
    }


    /**
     * dijkstra algorithm
     * @return
     */
    private int LowestArc()
    {
        HashSet<Synset> visitatedNodes = new HashSet<>();
        return 0;
    }

    @Override
    public double computeSimilarity(LinguisticObject o, LinguisticObject o2)
    {
        return 0;
    }
    // computeSimilarity(it.uniroma1.lcl.babelarity.Synset s1, it.uniroma1.lcl.babelarity.Synset s2): restituisce il valore di similarità      sotto forma di double
}
