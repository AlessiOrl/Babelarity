package it.uniroma1.lcl.babelarity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Word implements LinguisticObject
{
    private String word;

    private Word(String word)
    {
        this.word=word;
    }

    public static Word fromString(String s)
    {
        Word word = new Word(MiniBabelNet.takeWord(s));
        return word;
    }
    public String toString(){ return word;}

}
