package it.uniroma1.lcl.babelarity;

import it.uniroma1.lcl.babelarity.exceptions.NoSuchPosException;

import java.util.*;

public class Synset implements LinguisticObject {

  private String id;
  private Pos pos;
  private HashSet<String> lemmas;
  private HashSet<String> glosses;
  private HashSet<Synset> isaOpposite;
  private HashMap<String, ArrayList<Synset>> relations;

  public Synset(String id, HashSet<String> lemmas) throws NoSuchPosException {
    this.relations = new HashMap<>();
    this.isaOpposite = new HashSet<>();
    this.id = id;
    this.lemmas = lemmas;
    switch (id.charAt(id.length() - 1)) {
      case 'n':
        pos = Pos.NOUN;
        break;
      case 'v':
        pos = Pos.VERB;
        break;
      case 'a':
        pos = Pos.ADJ;
        break;
      case 'r':
        pos = Pos.ADV;
        break;
      default:
        throw new NoSuchPosException("No such POS in the WordNet");
    }
  }

  public Synset(String id, HashSet<String> lemmas, HashSet<String> glosses) throws NoSuchPosException {
    this(id, lemmas);
    this.glosses = glosses;

  }

  public void setGlosses(HashSet<String> glosses) {
    if (this.glosses == null)
      this.glosses = glosses;
  }

  /**
   * restituisce l’id univoco del synset sotto forma di stringa. Gli identificativi dei synset seguono il formato bn:00000000n, dove l’ultimo carattere rappresenta la parte del discorso del concetto n(oun), v(erb), a(djective), (adve)r(b).
   */
  public String getID() {
    return id;
  }

  /**
   * estituisce la parte del discorso (Part-of-Speech) del synset (calcolabile a partire dall’ID del synset) scelta tra NOUN, ADV, ADJ, VERB.
   */
  public Pos getPOS() {
    return pos;
  }

  /**
   * restituisce l’insieme delle lessicalizzazioni di cui è il synset
   */
  public HashSet<String> getLemmas() {
    return lemmas;
  }

  /**
   * restituisce le definizioni del synset
   */
  public HashSet<String> getGlosses() {
    return glosses;
  }

  /**
   * aggiunge una relazione all'elenco di relazioni del synset
   *
   * @param type   il tipo di relazione
   * @param synset il synset di destinazione
   */
  public void addRelation(String type, Synset synset) {
    if (relations.containsKey(type))
      relations.get(type).add(synset);
    else
      relations.put(type, new ArrayList<>(Arrays.asList(synset)));
    if (type.equals("is-a"))
      synset.addisaOpposite(this);
  }

  /**
   * aggiunge alla lista dei figli
   */
  public void addisaOpposite(Synset son) {
    this.isaOpposite.add(son);
  }

  public HashSet<Synset> getIsaOpposite() {
    return isaOpposite;
  }

  public ArrayList<Synset> getRelationByType(String type) {
    return relations.get(type);
  }

  public HashMap<String, ArrayList<Synset>> getRelations() {
    return relations;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof Synset))
      return false;
    if (((Synset) obj).glosses.size() != this.glosses.size())
      return false;
    return ((Synset) obj).id.equals(this.id);
  }

  @Override
  public String toString() {
    return id + " || " + lemmas.toString() + " || " + glosses.toString();
  }

}
