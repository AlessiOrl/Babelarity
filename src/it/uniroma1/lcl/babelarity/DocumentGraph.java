package it.uniroma1.lcl.babelarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * classe che contiene tutti i grafi dei documenti che sto prendendo in considerazione
 */
public class DocumentGraph
{

    private Document document;
    private HashMap<Synset, Set<Synset>> docGraph;

    public DocumentGraph(Document document)
    {
        this.document = document;
        this.docGraph = this.createGraph(document);
    }

    private HashMap<Synset, Set<Synset>> createGraph(Document doc)
    {
        docGraph = new HashMap<>();
        Set<Synset> lemmas = Arrays.stream(doc.getContent().replaceAll("\\W", " ").toLowerCase().split("\\s+")).filter(w -> !(CorpusManager.getStopWords().contains(w))).flatMap(w -> MiniBabelNet.getInstance().getSynsets(MiniBabelNet.takeWord(w)).stream()).collect(Collectors.toSet());

        for (Synset s : lemmas)
        {
            //aggiunge un nodo e lo relazione a tutti i vicini (fino a 2 unità di distanza)
            docGraph.putIfAbsent(s, this.getNeighbors(s, lemmas));
        }
        return docGraph;
    }

    private Set<Synset> getNeighbors(Synset nodeSynset, Set<Synset> documentWords)
    {
        Set<Synset> neighbors = new HashSet<>();
        Set<Synset> nearNeighbors = new HashSet<>();

        for (Entry<String, ArrayList<Synset>> e : nodeSynset.getRelations().entrySet()) nearNeighbors.addAll(e.getValue());

        for (Synset node : nearNeighbors)
        {
            neighbors.add(node);
            for (Entry<String, ArrayList<Synset>> e : node.getRelations().entrySet()) neighbors.addAll(e.getValue());
        }
        return neighbors.parallelStream().filter(documentWords::contains).collect(Collectors.toSet());


    }

    public Synset[] getNodes() { return docGraph.keySet().toArray(new Synset[0]);}

    public Synset[] getNeighbors(Synset key) {return docGraph.get(key).toArray(new Synset[0]);}

    @Override
    public int hashCode()
    {
        return Objects.hash(document);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof DocumentGraph)) return false;
        return ((DocumentGraph) obj).document.equals(this.document);
    }
}
