package it.uniroma1.lcl.babelarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

public class BabelSemanticSimilarity implements SemanticSimilarityStrategy {

  private static BabelSemanticSimilarity instance;
  private int maxDepth;
  private HashSet<Synset> roots;


  private BabelSemanticSimilarity() {
    roots = new HashSet<>();
  }

  public static BabelSemanticSimilarity getInstance() {
    if (instance == null)
      instance = new BabelSemanticSimilarity();
    return instance;
  }

  private int maxDepthAllGraph() {
    for (Synset s : MiniBabelNet.getInstance()) {
      if ((s.getIsaOpposite().size() != 0) && (s.getRelationByType("is-a") == null))
        roots.add(s);
    }
    System.out.println(roots.size());
    for (Synset s : roots)
      maxDepth = Math.max(maxDepth, FindDepth(s));
    return maxDepth;
  }

  private int FindDepth(Synset root) {
    //create a set of all the nodes already visited and the distance from the start
    HashMap<Synset, Integer> visited = new HashMap<>();

    // Create a queue for the pathFinder
    LinkedList<Synset> queue = new LinkedList<>();

    //add the root in the seen nodes and add him in to the queue
    visited.put(root, 0);
    queue.add(root);
    Synset vertex;
    int max = 0;
    while (queue.size() != 0) {
      // Dequeue a vertex from queue
      vertex = queue.poll();
      // Get all adjacent vertices of the dequeued vertex
      // If a adjacent has not been visited, then mark it visited and enqueue it
      // If the nearbyNode is the dest node then return the counter+1 and exit the loop
      for (Synset syn : vertex.getIsaOpposite()) {
        if (!visited.containsKey(syn)) {
          max = Math.max(max, visited.get(vertex) + 1);
          visited.put(syn, visited.get(vertex) + 1);
          queue.add(syn);
        }
      }
    }
    //if no path has been found return -1
    return max;
  }


  /**
   * bfs algorithm
   */
  private int FindBestPath(Synset root, Synset dest) {
    LinkedList<Synset> queue = new LinkedList<>();
    HashMap<Synset, Integer> visited = new HashMap<>();
    //create a set of all the nodes already visited and the distance from the start
    //add the root in the seen nodes and add him in to the queue
    visited.put(root, 0);
    queue.add(root);
    Synset vertex;
    while (queue.size() != 0) {
      vertex = queue.poll();
      int find = bfs(vertex, dest, visited);
      if (find != -1)
        return find + visited.get(vertex);
      if (vertex.getRelationByType("is-a") == null)
        continue;
      for (Synset syn : vertex.getRelationByType("is-a")) {
        if (!visited.containsKey(syn)) {
          visited.put(syn, visited.get(vertex) + 1);
          queue.add(syn);
        }
      }
    }
    return -1;
  }


  private int bfs(Synset root, Synset dest, HashMap<Synset, Integer> visited) {
    if (root.equals(dest))
      return 0;

    LinkedList<Synset> queue = new LinkedList<>();

    // visited.put(root, 0);
    queue.add(root);

    Synset vertex;
    while (queue.size() != 0) {
      vertex = queue.poll();
      for (Synset syn : vertex.getIsaOpposite()) {
        if (!visited.containsKey(syn)) {
          if (syn.equals(dest))
            return visited.get(vertex) + 1;
          visited.put(syn, visited.get(vertex) + 1);
          queue.add(syn);
        }
      }
    }
    return -1;
  }

  private int findBestPathBasic(Synset root, Synset dest) {
    LinkedList<Synset> queue = new LinkedList<>();
    HashMap<Synset, Integer> visitedNodes = new HashMap<>();
    HashSet<Synset> neighbours = new HashSet<>();
    Synset vertex;
    visitedNodes.put(root, 0);

    queue.add(root);
    while (queue.size() != 0) {
      vertex = queue.poll();
      for (Entry<String, ArrayList<Synset>> e : vertex.getRelations().entrySet()) {
        neighbours.addAll(e.getValue());
      }

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

  @Override
  public double computeSimilarity(LinguisticObject o, LinguisticObject o2) {

    if (o.equals(o2))
      return 1;
    return 1 / ((double) findBestPathBasic((Synset) o, (Synset) o2) + 1);

        /*System.out.println("starting compure similarity between " + ((Synset) o).getID() + " and " + ((Synset) o2).getID());
        int path = FindBestPath((Synset) o, (Synset) o2);
        if (path == -1)
        {
            System.out.println("path don't found, triying the base one");
            return 1 / ((double) findBestPathBasic((Synset) o, (Synset) o2) + 1);
        }
        return -Math.log((double) path / (2*getMaxDepth()));*/
  }

  private int getMaxDepth() {
    if (maxDepth == 0)
      return maxDepthAllGraph();
    else
      return maxDepth;
  }
}
