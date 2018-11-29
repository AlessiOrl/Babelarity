package it.uniroma1.lcl.babelarity;

import it.uniroma1.lcl.babelarity.linguisticobject.Document;
import it.uniroma1.lcl.babelarity.linguisticobject.Synset;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * classe che contiene tutti i grafi dei documenti che sto prendendo in considerazione
 */
public class DocumentGraph {

    private Document document;
    private HashMap<Synset, Set<Synset>> docGraph;

    public DocumentGraph(Document document) {
        this.document = document;
        this.docGraph = this.createGraph(document);
    }

    private HashMap<Synset, Set<Synset>> createGraph(Document doc) {
        docGraph = new HashMap<>();
        Set<Synset> lemmas = Arrays.stream(doc.getContent().replaceAll("\\W", " ").toLowerCase().split("\\s+"))
                .filter(w -> !(CorpusManager.getStopWords().contains(w)))
                .flatMap(w -> MiniBabelNet.getInstance().getSynsets(MiniBabelNet.takeWord(w)).stream())
                .collect(Collectors.toSet());

        for (Synset s : lemmas) {
            if (docGraph.containsKey(s)) continue;
            Set<Synset> neighbors = this.getNeighbors(s, lemmas);
            if (neighbors.size() == 0) continue;
            docGraph.put(s, neighbors);
        }
        return docGraph;
    }

    private Set<Synset> getNeighbors(Synset nodeSynset, Set<Synset> documentWords) {
        Set<Synset> neighbors = new HashSet<>();
        Set<Synset> nearNeighbors = new HashSet<>();

        nearNeighbors = nodeSynset.getRelations().entrySet().parallelStream().map(Entry::getValue).flatMap(Collection::parallelStream).collect(Collectors.toSet());

        for (Synset node : nearNeighbors) {
            neighbors.add(node);
            neighbors.addAll(node.getRelations().entrySet().parallelStream().map(Entry::getValue).flatMap(Collection::parallelStream).collect(Collectors.toSet()));
        }

        return neighbors.parallelStream()
                .filter(documentWords::contains)
                .collect(Collectors.toSet());
    }

    public Synset[] getNodes() {
        return docGraph.keySet().toArray(new Synset[0]);
    }

    public Synset[] getNeighbors(Synset key) {
        return docGraph.get(key).toArray(new Synset[0]);
    }

    @Override
    public int hashCode() {
        return Objects.hash(document);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DocumentGraph))
            return false;
        return ((DocumentGraph) obj).document.equals(this.document);
    }
}
