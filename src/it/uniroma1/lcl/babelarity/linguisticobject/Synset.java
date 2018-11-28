package it.uniroma1.lcl.babelarity.linguisticobject;

import it.uniroma1.lcl.babelarity.exceptions.NoSuchPosException;

import java.util.*;

/**
 * The class that define a typology of a @LinguisticObject.
 * A Synset (synonym set) is a set of words with the same {@link Pos POS} that can be used and substituted in a certain context. TODO: ??
 * The Synsets are made up by an ID, a {@link Pos POS}, some lemmas, glosses and relations.
 * The lemma is the base form of the words  -> ex. the lemma of the word prettier is pretty
 * A gloss is the meaning of the word, every word can have multiple meaning
 * The relations of a Synset are the arc of the graph, those arcs are used to create the wordnet called {@link it.uniroma1.lcl.babelarity.MiniBabelNet MiniBabelNet}
 */

public class Synset implements LinguisticObject {

  private String id;
  private Pos pos;
  private HashSet<String> lemmas;
  private HashSet<String> glosses;
  private HashSet<Synset> isaOpposite;
  private HashMap<String, ArrayList<Synset>> relations;

  /**
   * The constructor of the class;
   * @param id the id of the Synset
   * @param lemmas the lemmas of the synset
   * @throws NoSuchPosException in case of a POS not present in the standard graph
   */
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
   * Return the String of the unique ID of the Synset.
   * The form of the ID is "bn:00000000n" where the last char rapresent the Part-of-speech of the synset : n(oun), v(erb), a(djective), (adve)r(b).
   */
  public String getID() {
    return id;
  }

  /**
   * Return the part-of-speech of the Synset taken from the Synset's ID
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
