package it.uniroma1.lcl.babelarity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class BabelSemanticSimilarity implements SemanticSimilarityStrategy
{

    private static BabelSemanticSimilarity instance;
    private int mediumDepth;
    private HashSet<Synset> roots;


    private BabelSemanticSimilarity()
    {
        roots = new HashSet<>();
    }

    public static BabelSemanticSimilarity getInstance()
    {
        if (instance == null) instance = new BabelSemanticSimilarity();
        return instance;
    }

//todo: in teoria basta solo scendere, ma non funziona
    private int maxDepthAllGraph()
    {
        for (Synset s : MiniBabelNet.getInstance())
            if ((s.getRelationByType("has-kind") != null) && (s.getRelationByType("is-a") == null)) roots.add(s);
        System.out.println(roots.size());
        for (Synset s : roots) mediumDepth += FindDepth(s);
        mediumDepth = mediumDepth / roots.size();
        return mediumDepth;
    }
//todo: salire con is-a e poi fare la bfs sui figli, questo finchè non si trova il path
    private int FindDepth(Synset root)
    {
        //create a set of all the nodes already visited and the distance from the start
        HashMap<Synset, Integer> visited = new HashMap<>();

        // Create a queue for the pathFinder
        LinkedList<Synset> queue = new LinkedList<>();

        //add the root in the seen nodes and add him in to the queue
        visited.put(root, 0);
        queue.add(root);
        Synset vertex;
        int max = 0;
        while (queue.size() != 0)
        {
            // Dequeue a vertex from queue
            vertex = queue.poll();
            // Get all adjacent vertices of the dequeued vertex
            // If a adjacent has not been visited, then mark it visited and enqueue it
            // If the nearbyNode is the dest node then return the counter+1 and exit the loop
            if(vertex.getRelationByType("has-kind") == null) continue;
            for (Synset syn : vertex.getRelationByType("has-kind"))
            {
                if (!visited.containsKey(syn))
                {
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
    private int FindBestPath(Synset root, Synset dest)
    {
        if (root.equals(dest)) return 0;
        //create a set of all the nodes already visited and the distance from the start
        HashMap<Synset, Integer> visited = new HashMap<>();

        // Create a queue for the pathFinder
        LinkedList<Synset> queue = new LinkedList<>();

        //add the root in the seen nodes and add him in to the queue
        visited.put(root, 0);
        queue.add(root);

        Synset vertex;
        while (queue.size() != 0)
        {
            // Dequeue a vertex from queue
            vertex = queue.poll();

            // Get all adjacent vertices of the dequeued vertex
            // If a adjacent has not been visited, then mark it visited and enqueue it
            // If the nearbyNode is the dest node then return the counter+1 and exit the loop
            for (Synset syn : vertex.getNearbyNodes())
            {
                if (!visited.containsKey(syn))
                {
                    if (syn.equals(dest)) return visited.get(vertex) + 1;
                    visited.put(syn, visited.get(vertex) + 1);
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
        if (o.equals(o2)) return 1;
        System.out.println(o.equals(o2));
        return Math.log((double) FindBestPath((Synset) o, (Synset) o2) / getMediumDepth());
    }

    public int getMediumDepth()
    {
        if (mediumDepth == 0) return maxDepthAllGraph();
        else return mediumDepth;
    }
}
