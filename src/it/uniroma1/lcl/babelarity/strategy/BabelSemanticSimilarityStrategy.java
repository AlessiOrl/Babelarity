package it.uniroma1.lcl.babelarity.strategy;

import it.uniroma1.lcl.babelarity.LinguisticObject;
import it.uniroma1.lcl.babelarity.Synset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

public class BabelSemanticSimilarityStrategy implements SemanticSimilarityStrategy {

  private static BabelSemanticSimilarityStrategy instance;
  private int maxDepth;
  private HashSet<Synset> roots;

  private BabelSemanticSimilarityStrategy() {
    roots = new HashSet<>();
  }

  /**
   * Return the instance of {@link BabelSemanticSimilarityStrategy}
   *
   * @return A {@code BabelSemanticSimilarityStrategy } object.
   */
  public static BabelSemanticSimilarityStrategy getInstance() {
    if (instance == null)
      instance = new BabelSemanticSimilarityStrategy();
    return instance;
  }

  @Override
  public double computeSimilarity(LinguisticObject o, LinguisticObject o2) {
    return (o.equals(o2)) ? 1 : 1 / ((double) findBestPathBasic((Synset) o, (Synset) o2) + 1);
  }

  /**
   * This method is the method find the best path in between two nodes {@link it.uniroma1.lcl.babelarity.MiniBabelNet MiniBabelNet}.
   * @param root
   * @param dest
   * @return The length of the path between the given nodes or -1 if no path has been found.
   */
  private int findBestPathBasic(Synset root, Synset dest) {
    LinkedList<Synset> queue = new LinkedList<>();
    HashMap<Synset, Integer> visitedNodes = new HashMap<>();
    HashSet<Synset> neighbours = new HashSet<>();
    Synset vertex;
    visitedNodes.put(root, 0);
    queue.add(root);

    while (queue.size() != 0) {
      vertex = queue.poll();
      for (Entry<String, ArrayList<Synset>> e : vertex.getRelations().entrySet()) neighbours.addAll(e.getValue());
      for (Synset syn : neighbours) {
        if (!visitedNodes.containsKey(syn)) {
          if (syn.equals(dest))
            return visitedNodes.get(vertex) + 1;
          visitedNodes.put(syn, visitedNodes.get(vertex) + 1);
          queue.add(syn);
        }
      }
      neighbours.clear();
    }
    return -1;
  }
}
