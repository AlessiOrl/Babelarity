package it.uniroma1.lcl.babelarity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.swing.event.DocumentEvent;

/**
 * classe che contiene tutti i grafi dei documenti che sto prendendo in considerazione
 */
public class DocumentGraph
{

    private Document document;
    private HashMap<String, Set<String>> docGraph;


    public DocumentGraph(Document document)
    {
        this.document = document;
        this.docGraph = this.createGraph(document);
    }

    private HashMap<String, Set<String>> createGraph(Document doc)
    {

        Set<String> lemmas = Arrays.stream(doc.getContent().replaceAll("\\W", " ").toLowerCase().split("\\s+")).filter(w -> !(CorpusManager.getStopWords().contains(w))).map(MiniBabelNet::takeWord).collect(Collectors.toSet());
        //creazione nodi
        for (String w : lemmas)
        {
            Synset wordSynset = MiniBabelNet.getInstance().getSynset(w);
            //aggiunge un nodo e lo relazione a tutti i vicini (fino a 2 unità di distanza)
            docGraph.putIfAbsent(w, this.getNeighbors(wordSynset, lemmas));
        }
        return docGraph;
    }

    private Set<String> getNeighbors(Synset nodeSynset, Set<String> documentWords)
    {
        Set<Synset> neighbors = new HashSet<>();
        HashSet<Synset> nearNeighbors = new HashSet<>();

        for (String w : documentWords)
        {

            for (Entry<String, ArrayList<Synset>> e : nodeSynset.getRelations().entrySet())
            {
                nearNeighbors.addAll(e.getValue());
                neighbors.addAll(e.getValue());
            }

            for (Synset node : nearNeighbors)
            {
                for (Entry<String, ArrayList<Synset>> e : node.getRelations().entrySet())
                { neighbors.addAll(e.getValue());}
            }

        }

        return
            neighbors.parallelStream().map(Synset::getLemmas).flatMap(Collection::parallelStream).filter(documentWords::contains).collect(Collectors.toSet());
    }


}
