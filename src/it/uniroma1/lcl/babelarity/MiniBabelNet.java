package it.uniroma1.lcl.babelarity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MiniBabelNet implements Iterable<Synset>
{

    private static String lemmatization = "resources/lemmatization-en.txt";
    private static String dictionary = "resources/dictionary.txt";
    private static String glosses = "resources/glosses.txt";
    private static String relations = "resources/relations.txt";

    private StrategySimilarity lexicalSimilarityStrategy;
    private StrategySimilarity SemanticSimilarityStrategy;
    private StrategySimilarity DocumentSimilarityStrategy;
    private static MiniBabelNet instance;
    private HashMap<String, BabelSynset> synsetsMap = new HashMap<>();
    //TODO: USARE QUESTA MODALITA' PER LE WORD, per vedere se è un lemma o una flessa fare un OR tra il keySet e il valueSet
    private HashMap<String, String> fromInflectedToLemma = new HashMap<>();
    private List<BabelSynset> synsets = new ArrayList<>();
    private int synsetSize;

    private MiniBabelNet()
    {
        try (Stream<String> streamLemmatization = Files.lines(Paths.get(lemmatization)); Stream<String> streamDictionary = Files.lines(Paths.get(dictionary)); Stream<String> streamGlosses = Files.lines(Paths.get(glosses)); Stream<String> streamRelations = Files.lines(Paths.get(relations)))

        {
            streamLemmatization.map(line -> line.split("\t")).forEach(line -> Word.addWord(line[0], line[1]));

            streamDictionary.map(line -> line.split("\t", 2)).filter(line -> line[0].startsWith("bn"))
                .forEach(line -> synsetsMap.put(line[0], new BabelSynset(line[0], new HashSet<>(Arrays.asList(line[1].split("\t"))))));

            streamGlosses.map(line -> line.split("\t", 2)).filter(line -> line[0].startsWith("bn"))
                .forEach(line -> synsetsMap.get(line[0]).setGlosses(new HashSet<>(Arrays.asList(line[1].split("\t")))));

            synsets = List.copyOf(synsetsMap.values());
            synsetSize = synsets.size();

            streamRelations.map(line -> line.split("\t")).forEach(line -> synsetsMap.get(line[0]).addRelation(line[2], synsetsMap.get(line[1])));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    static MiniBabelNet getInstance()
    {
        if (instance == null) instance = new MiniBabelNet();
        return instance;
    }

    /**
     * restituisce l’insieme di synset che contengono tra i loro sensi la parola in input
     */
    public List<Synset> getSynsets(String word)
    {
        ArrayList<Synset> listOfSynsets = new ArrayList<>();
        for (BabelSynset bs : synsets) if (bs.getLemmas().contains(Word.fromString(word).getLemma())) listOfSynsets.add(bs);
        return listOfSynsets;
    }

    /**
     * restituisce il synset relativo all’id specificato
     */
    public Synset getSynset(String id) {return synsetsMap.get(id);}

    /**
     * restituisce uno o più lemmi associati alla parola flessa fornita in input
     */
    public List<String> getLemmas(String word)
    {
        return List.of(Word.fromString(word).getLemma());
    }

    /**
     * Restituisce le informazioni inerenti al it.uniroma1.lcl.babelarity.Synset fornito in input
     * sotto forma di stringa. Il formato della stringa è il seguente:
     * ID\tPOS\tLEMMI\tGLOSSE\tRELAZIONI Le componenti LEMMI, GLOSSE e RELAZIONI possono contenere
     * più elementi, questi sono separati dal carattere ";" Le relazioni devono essere condificate
     * nel seguente formato: TARGETSYNSET_RELNAME   es. bn:00081546n_has-kind
     *
     * es: bn:00047028n	NOUN	word;intelligence;news;tidings	Information about recent and important
     * events	bn:0000001n_has-kind;bn:0000001n_is-a
     */

    /**
     * Restituisce le informazioni inerenti al it.uniroma1.lcl.babelarity.Synset fornito in input sotto forma di stringa.
     */
    public String getSynsetSummary(Synset s)
    {
        BabelSynset obj = (BabelSynset) s;
        StringBuilder ret = new StringBuilder(obj.getID() + "\t" + obj.getPOS() + "\t");
        Iterator<String> LemmasIterator = obj.getLemmas().iterator();
        while (LemmasIterator.hasNext())
        {
            ret.append(LemmasIterator.next());
            if (LemmasIterator.hasNext()) ret.append(";");
        }

        ret.append("\t");

        Iterator<String> glosseIterator = obj.getGlosses().iterator();
        while (glosseIterator.hasNext())
        {
            ret.append(glosseIterator.next());
            if (glosseIterator.hasNext()) ret.append(";");
        }

        ret.append("\t");

        ret.append(obj.getRelations().entrySet().stream().flatMap(entry -> entry.getValue().stream().map(bs -> bs.getID() + "_" + entry.getKey())).collect(Collectors.joining(";")));

        return ret.toString();
    }

    /**
     * calcola e restituisce un double che rappresenta la similarità tra due oggetti linguistici (it.uniroma1.lcl.babelarity.Synset, Documenti o parole)
     */
    public double computeSimilarity(LinguisticObject o1, LinguisticObject o2)
    {
        if (lexicalSimilarityStrategy == null) lexicalSimilarityStrategy = BabelLexicalSimilarity.getInstance();
        System.out.println(o1);
        return lexicalSimilarityStrategy.computeSimilarity(o1, o2);
    }

    @Override
    public Iterator<Synset> iterator()
    {
        return new Iterator<>()
        {
            private int k;

            @Override
            public boolean hasNext()
            {
                return k < synsetSize;
            }

            @Override
            public Synset next()
            {
                return hasNext() ? synsets.get(k++) : null;
            }
        };
    }
    //TODO: Singleton & Iterable on synset

    // setLexicalSimilarityStrategy(): Imposta l’algoritmo di calcolo della               similarità tra parole (di default, in fase di costruzione dell’oggetto         viene impostato l’algoritmo implementato dallo studente).

    // setSemanticSimilarityStrategy(): Imposta l’algoritmo di calcolo della              similarità tra synset (di default, in fase di costruzione dell’oggetto         viene impostato l’algoritmo implementato dallo studente).

    // setDocumentSimilarityStrategy(): Imposta l’algoritmo di calcolo della              similarità tra documenti (di default, in fase di costruzione dell’oggetto      viene impostato l’algoritmo implementato dallo studente).

    public int getSynsetSize()
    {
        return synsetSize;
    }
}

