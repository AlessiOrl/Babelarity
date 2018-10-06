package it.uniroma1.lcl.babelarity;

public class BabelDocumentSimilarity implements DocumentSimilarityStrategy
{

    private static BabelDocumentSimilarity instance;


    private BabelDocumentSimilarity()
    {

    }

    public static BabelDocumentSimilarity getInstance()
    {
        if (instance == null) instance = new BabelDocumentSimilarity();
        return instance;
    }


    @Override
    public double computeSimilarity(LinguisticObject o, LinguisticObject o2)
    {
        return 0;
    }
}
