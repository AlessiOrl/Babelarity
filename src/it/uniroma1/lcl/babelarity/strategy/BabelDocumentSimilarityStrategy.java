package it.uniroma1.lcl.babelarity.strategy;

import it.uniroma1.lcl.babelarity.linguisticobject.Document;
import it.uniroma1.lcl.babelarity.linguisticobject.LinguisticObject;
import it.uniroma1.lcl.babelarity.linguisticobject.Synset;
import it.uniroma1.lcl.babelarity.VectorizedLinguisticObj;
import it.uniroma1.lcl.babelarity.DocumentGraph;
import it.uniroma1.lcl.babelarity.MiniBabelNet;

import java.util.Arrays;
import java.util.Random;

public class BabelDocumentSimilarityStrategy implements DocumentSimilarityStrategy {

  private static BabelDocumentSimilarityStrategy instance;
  private VectorizedLinguisticObj<Document, Integer> vectorizedDocuments;
  private final int RESTART = 25;
  private final int ITERATIONS = 5000;

  private BabelDocumentSimilarityStrategy() {
    vectorizedDocuments = new VectorizedLinguisticObj<>();
  }

  public static BabelDocumentSimilarityStrategy getInstance() {
    if (instance == null) instance = new BabelDocumentSimilarityStrategy();
    return instance;
  }

  @Override
  public double computeSimilarity(LinguisticObject o, LinguisticObject o2) {
    Document d = ((Document) o);
    Document d2 = ((Document) o2);
    if (d.equals(d2)) return 1;
    double numeratore = 0;
    double denominatore1 = 0;
    double denominatore2 = 0;

    //if (!vectorizedDocuments.containsKey(d)) vectorizedDocuments.put(d, generateRanks(d));
    //if (!vectorizedDocuments.containsKey(d2)) vectorizedDocuments.put(d2, generateRanks(d2));
    vectorizedDocuments.put(d, generateRanks(d));
    vectorizedDocuments.put(d2, generateRanks(d2));

    for (int x = 0; x < vectorizedDocuments.get(d).length; x++) {
      numeratore += vectorizedDocuments.get(d)[x] * vectorizedDocuments.get(d2)[x];
      denominatore1 += Math.pow(vectorizedDocuments.get(d)[x], 2.0);
      denominatore2 += Math.pow(vectorizedDocuments.get(d2)[x], 2.0);
    }
    return numeratore / (Math.sqrt(denominatore1) * Math.sqrt(denominatore2));
  }

  private Integer[] generateRanks(Document d) {
    long t1 = System.currentTimeMillis();
    DocumentGraph docGraph = new DocumentGraph(d);
    System.out.println("Time graph : " + (System.currentTimeMillis() - t1));

    Integer[] vector = new Integer[MiniBabelNet.getInstance().synsetSize];
    Arrays.fill(vector, 0);
    Random rand = new Random();
    int k = ITERATIONS;

    t1 = System.currentTimeMillis();
    int index = rand.nextInt(docGraph.getNodes().length);
    Synset node = docGraph.getNodes()[index];
    while (k-- > 0) {
      vector[MiniBabelNet.getAllSynsets()
                         .indexOf(node)]++;
      int randInt = rand.nextInt(100);
      if (randInt > RESTART) {
        index = rand.nextInt(docGraph.getNodes().length);
        node = docGraph.getNodes()[index];
      } else {
        Synset[] neighbor = docGraph.getNeighbors(node);
        index =rand.nextInt(neighbor.length);
        node = neighbor[index];
      }
    }
    System.out.println("Time random Walk : " + (System.currentTimeMillis() - t1));
    return vector;
  }
}