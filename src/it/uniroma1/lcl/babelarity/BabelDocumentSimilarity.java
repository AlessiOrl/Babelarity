package it.uniroma1.lcl.babelarity;

import java.util.Arrays;
import java.util.Random;

public class BabelDocumentSimilarity implements DocumentSimilarityStrategy
{

    private static BabelDocumentSimilarity instance;
    private VectorizedLinguisticObj<Document, Integer> vectorizedDocuments;
    private final int RESTART = 75;     //65  , 75  ,
    private final int ITERATIONS = 600; //5000, 1000,

    private BabelDocumentSimilarity()
    {
        vectorizedDocuments = new VectorizedLinguisticObj<>();
    }

    public static BabelDocumentSimilarity getInstance()
    {
        if (instance == null) instance = new BabelDocumentSimilarity();
        return instance;
    }
//todo: da scaricare

    @Override
    public double computeSimilarity(LinguisticObject o, LinguisticObject o2)
    {
        Document d = ((Document) o);
        Document d2 = ((Document) o2);
        if (d.equals(d2)) return 1;
        double numeratore = 0;
        double denominatore1 = 0;
        double denominatore2 = 0;

        if (!vectorizedDocuments.containsKey(d)) vectorizedDocuments.put(d, generateRanks(d));
        if (!vectorizedDocuments.containsKey(d2)) vectorizedDocuments.put(d2, generateRanks(d2));
        for (int x = 0; x < vectorizedDocuments.get(d).length; x++)
        {
            numeratore += vectorizedDocuments.get(d)[x] * vectorizedDocuments.get(d2)[x];
            denominatore1 += Math.pow(vectorizedDocuments.get(d)[x], 2.0);
            denominatore2 += Math.pow(vectorizedDocuments.get(d2)[x], 2.0);
        }
        return numeratore / (Math.sqrt(denominatore1) * Math.sqrt(denominatore2));
    }

    private Integer[] generateRanks(Document d)
    {
        DocumentGraph docGraph = new DocumentGraph(d);
        Integer[] vector = new Integer[MiniBabelNet.getInstance().synsetSize];
        Arrays.fill(vector, 0);
        Random rand = new Random();
        int k = ITERATIONS;

        int index = rand.nextInt(docGraph.getNodes().length);
        Synset node = docGraph.getNodes()[index];
        while (--k > 0)
        {
            vector[MiniBabelNet.getAllSynsets().indexOf(node)]++;
            int randInt = rand.nextInt(100);
            if (randInt > RESTART)
            {
                index = rand.nextInt(docGraph.getNodes().length);
                node = docGraph.getNodes()[index];
            } else
            {
                Synset[] neighbor = docGraph.getNeighbors(node);
                index = neighbor.length == 0 ? rand.nextInt(docGraph.getNodes().length) : rand.nextInt(neighbor.length);
                node = neighbor.length > 0 ? neighbor[index] : docGraph.getNodes()[index];
            }
        }
        return vector;
    }
}