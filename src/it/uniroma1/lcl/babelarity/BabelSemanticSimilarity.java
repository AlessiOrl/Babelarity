package it.uniroma1.lcl.babelarity;

import java.util.HashSet;
import java.util.LinkedList;

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

    public int FindBestPath(Synset root,Synset dest)
    {
        if (root.equals(dest)) return 0;
        //create a set of all the nodes already visited
        HashSet<Synset> visited = new HashSet<>();

        // Create a queue for the pathFinder
        LinkedList<Synset> queue = new LinkedList<>();

        //add the root in the seen nodes and add him in to the queue
        visited.add(root);
        queue.add(root);
        int counter = 0;

        while (queue.size() != 0)
        {
            // Dequeue a vertex from queue
            root = queue.poll();

            // Get all adjacent vertices of the dequeued vertex
            // If a adjacent has not been visited, then mark it visited and enqueue it
            // If the nearbyNode is the dest node then return the counter+1 and exit the loop
            for (Synset syn : root.getNearbyNodes())
            {
                if (!visited.contains(syn))
                {
                    if (syn.equals(dest)) return ++counter;

                    visited.add(syn);
                    queue.add(syn);
                }
            }
        }
        //if no path has been found return -1
        return -1;
    }

    @Override
    public double computeSimilarity(LinguisticObject o, LinguisticObject o2)
    {
        return 0;
    }
    // computeSimilarity(it.uniroma1.lcl.babelarity.Synset s1, it.uniroma1.lcl.babelarity.Synset s2): restituisce il valore di similarità      sotto forma di double
}
