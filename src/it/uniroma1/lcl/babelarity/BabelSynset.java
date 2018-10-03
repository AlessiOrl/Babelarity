package it.uniroma1.lcl.babelarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class BabelSynset implements Synset
{

    private String id;
    private Pos pos;
    private HashSet<String> lemmas;
    private HashSet<String> glosses;
    private HashSet<BabelSynset> fathers;
    private HashMap<String, ArrayList<BabelSynset>> relations;

    public BabelSynset(String id, HashSet<String> lemmas)
    {
        this.relations = new HashMap<>();
        this.fathers = new HashSet<>();
        this.id = id;
        this.lemmas = lemmas;
        switch (id.charAt(id.length() - 1))
        {
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
        }
    }

    public BabelSynset(String id, HashSet<String> lemmas, HashSet<String> glosses)
    {
        this(id, lemmas);
        this.glosses = glosses;

    }

    public void setGlosses(HashSet<String> glosses)
    {
        if (this.glosses == null) this.glosses = glosses;
    }

    /**
     * restituisce l’id univoco del synset sotto forma di stringa. Gli identificativi dei synset
     * seguono il formato bn:00000000n, dove l’ultimo carattere rappresenta la parte del discorso
     * del concetto n(oun), v(erb), a(djective), (adve)r(b).
     */
    public String getID()
    {
        return id;
    }

    /**
     * estituisce la parte del discorso (Part-of-Speech) del synset (calcolabile a partire dall’ID
     * del synset) scelta tra NOUN, ADV, ADJ, VERB.
     */
    public Pos getPOS()
    {
        return pos;
    }

    /**
     * restituisce l’insieme delle lessicalizzazioni di cui è il synset
     */
    public HashSet<String> getLemmas()
    {
        return lemmas;
    }

    /**
     * restituisce le definizioni del synset
     */
    public HashSet<String> getGlosses()
    {
        return glosses;
    }

    /**
     * aggiunge una relazione all'elenco di relazioni del synset
     *
     * @param type il tipo di relazione
     * @param synset il synset di destinazione
     */
    public void addRelation(String type, BabelSynset synset)
    {
        if (relations.containsKey(type)) relations.get(type).add(synset);
        else relations.put(type, new ArrayList<>(Arrays.asList(synset)));
        synset.addFather(this);
    }
    /**
     * aggiunge alla lista dei padri il proprio padre
     * @param father
     */
    public void addFather(BabelSynset father)
    {
        this.fathers.add(father);
    }

    public ArrayList<BabelSynset> getRelationByType(String type)
    {
        return relations.get(type);
    }

    public HashMap<String,ArrayList<BabelSynset>> getRelations(){
        return relations;
    }

    @Override
    public String toString()
    {
        return id + " || " + lemmas.toString() + " || " + glosses.toString();
    }
}
