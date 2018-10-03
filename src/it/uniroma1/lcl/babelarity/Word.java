package it.uniroma1.lcl.babelarity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Word implements LinguisticObject
{

    private static HashMap<String, Word> instances = new HashMap<>();
    private static HashMap<String, Word> instancesFromInflected = new HashMap<>();
    //avere una mappa inflecrted lemma in mini babel net
    private Set<String> inflectedForm;
    private String lemma;

    private Word(String inflectedForm, String lemma)
    {
        this.lemma = lemma;
        this.inflectedForm = new HashSet<>(List.of(inflectedForm));
    }

    public Set<String> getInflectedForm()
    {
        return inflectedForm;
    }

    public String getLemma()
    {
        return lemma;
    }

    public static void addWord(String inflectedForm, String lemma)
    {
        if (instances.putIfAbsent(lemma, new Word(lemma,inflectedForm))!= null)
            instances.get(lemma).inflectedForm.add(inflectedForm);
        instancesFromInflected.put(inflectedForm,instances.get(lemma));

    }

    public static HashMap<String, Word> getInstance()
    {
        return instances;
    }

    public static Word fromString(String s)
    {
        return instances.containsKey(s) ? instances.get(s) : instancesFromInflected.get(s);
    }
    public static boolean exist(String s)
    {
        return instancesFromInflected.containsKey(s) || instances.containsKey(s);
    }
    @Override
    public String toString()
    {
        return lemma;
    }
}
