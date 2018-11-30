package it.uniroma1.lcl.babelarity;

import it.uniroma1.lcl.babelarity.linguisticobject.Document;
import it.uniroma1.lcl.babelarity.linguisticobject.Synset;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * This class create the graph of a Document.
 * The nodes of the graph are all the Synset present in the document and those nodes are linked via relations.
 * A node is linked with another only if the Synset is al most two levels away from the parent node.
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
                               .flatMap(w -> MiniBabelNet.getInstance().getSynsets(MiniBabelNet.takeWord(w)).parallelStream())
                               .collect(Collectors.toSet());

    for (Synset s : lemmas) {
      docGraph.putIfAbsent(s, this.generateNeighbors(s, lemmas));
    }
    return docGraph;
  }

  /**
   * This class generate the Neighbors of a given node
   *
   * @param nodeSynset    the Node
   * @param documentWords the Synset of the Document
   * @return
   */
  private Set<Synset> generateNeighbors(Synset nodeSynset, Set<Synset> documentWords) {
    Set<Synset> neighbors = new HashSet<>();
    Set<Synset> nearNeighbors = new HashSet<>();

    for (Entry<String, ArrayList<Synset>> e : nodeSynset.getRelations().entrySet()) {
      nearNeighbors.addAll(e.getValue()
                            .parallelStream()
                            .filter(documentWords::contains)
                            .collect(Collectors.toSet()));
    }

    for (Synset node : nearNeighbors) {
      neighbors.add(node);
      for (Entry<String, ArrayList<Synset>> e : node.getRelations().entrySet())
        neighbors.addAll(e.getValue()
                          .parallelStream()
                          .filter(documentWords::contains)
                          .collect(Collectors.toSet()));
    }

    return neighbors;
  }

  /**
   * Return the Array with all the nodes of the graph
   */
  public Synset[] getNodes() {
    return docGraph.keySet().toArray(new Synset[0]);
  }

  /**
   * @param key the Node
   * @return all the neighbors of the given node
   */
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
