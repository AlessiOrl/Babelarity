package it.uniroma1.lcl.babelarity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BabelLexicalSimilarity implements LexicalSimilarityStrategy
{

    private static Path corpusDir = Paths.get("resources/corpus");
    private static Path stopWordPath = Paths.get("stopWords.txt");
    private static BabelLexicalSimilarity instance;
    private HashMap<String, HashSet<Integer>> documentByWords;
    private Map<String, Integer> wordsIndexing;
    private HashMap<String, Integer> wordsCounter;
    private static List<File> corpusFiles;
    private HashSet<String> stopWords;
    //TODO:AGGIUNGERE PARAMETRO PER TENERE SALVATI I PMI GIA' CALCOALTI

    private BabelLexicalSimilarity()
    {
        corpusFiles = List.of(corpusDir.toFile().listFiles());
        wordsCounter = new HashMap<>();
        wordsIndexing = new HashMap<>();
        documentByWords = new HashMap<>();
        this.parseStopWords();
        this.parseCorpus();
    }

    private void parseStopWords()
    {
        try (Stream<String> streamStopWords = Files.lines(stopWordPath))
        {
            stopWords = streamStopWords.collect(Collectors.toCollection(HashSet::new));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static BabelLexicalSimilarity getInstance()
    {
        if (instance == null) instance = new BabelLexicalSimilarity();
        return instance;

    }

    private void parseCorpus()
    {
        System.out.println("INIZIO PARSE CORPUS");
        long timeStart = System.currentTimeMillis();

        int k = 0;
        for (int x = 0; x < corpusFiles.size(); x++)
        {
            HashMap<String, Integer> DocumentInfo = new HashMap<>();
            try
            {
                String text = new String(Files.readAllBytes(corpusFiles.get(x).toPath()), "utf-8");
                String[] words = text.replaceAll("\\W", " ").toLowerCase().split("\\s+");
                for (String s : words)
                {
                    if (stopWords.contains(s)) continue;

                    String lemma = MiniBabelNet.takeWord(s);

                    if (lemma == null) continue;
                    if (DocumentInfo.putIfAbsent(lemma, 1) != null) DocumentInfo.put(lemma, DocumentInfo.get(lemma) + 1);

                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            for (String s : DocumentInfo.keySet())
            {
                if (DocumentInfo.get(s) < 2 || DocumentInfo.get(s) > 25) continue;
                if (documentByWords.putIfAbsent(s, new HashSet<>(x)) != null) documentByWords.get(s).add(x);
                if (wordsIndexing.putIfAbsent(s, k) == null) k++;
                if (wordsCounter.putIfAbsent(s, DocumentInfo.get(s)) != null) wordsCounter.put(s, wordsCounter.get(s) + DocumentInfo.get(s));
            }
        }
        long timeEnd = System.currentTimeMillis();
        long timeTakenSc = (timeEnd - timeStart) / 1000;
    }

    private Float[] generatePMI(String s)
    {
        Float[] vettore = new Float[wordsIndexing.keySet().size()];
        for (String p : wordsIndexing.keySet())
        {
            if (wordsIndexing.get(s).equals(wordsIndexing.get(p))) vettore[wordsIndexing.get(p)] = 1f;
            else
            {
                Set<Integer> intersection = new HashSet<>(documentByWords.get(s)); // use the copy constructor
                intersection.retainAll(documentByWords.get(p));
                float numDoc = (float) documentByWords.get(s).size() + documentByWords.get(p).size();
                float numeratore = intersection.size() / numDoc - intersection.size();
                float denominatore1 = wordsCounter.get(s) / numDoc;
                float denominatore2 = wordsCounter.get(p) / numDoc;
                vettore[wordsIndexing.get(p)] = numeratore / (denominatore1 * denominatore2);
            }
        }
        return vettore;
    }

    //restituisce il valore di similarità sotto   forma di double
    @Override
    public double computeSimilarity(LinguisticObject o, LinguisticObject o2)
    {

        String p = ((Word) o).toString();
        String p2 = ((Word) o2).toString();
        if (p.equals(p2)) return 1;
        double numeratore = 0;
        double denominatore1 = 0;
        double denominatore2 = 0;
        Float[] vettore1 = generatePMI(p);
        Float[] vettore2 = generatePMI(p2);
        //TODO: salvare i dati generati
        for (int x = 0; x < vettore1.length; x++)
        {
            numeratore += vettore1[x] * vettore2[x];
            denominatore1 += Math.pow(vettore1[x], 2.0);
            denominatore2 += Math.pow(vettore2[x], 2.0);
        }
        return numeratore / (Math.sqrt(denominatore1) * Math.sqrt(denominatore2));
    }

}

        /* VERSIONE DIVISIONE BY DOCUMENT
         for (int x = 0; x < corpusFiles.size(); x++)
        {
            try
            {

                String text = new String(Files.readAllBytes(corpusFiles.get(x).toPath()), "utf-8");
                String[] words = text.replaceAll("\\W", " ").toLowerCase().split("\\s+");
                for (String s : words)
                {
                    if (stopWords.contains(s)) continue;

                    String lemma = MiniBabelNet.takeWord(s);

                    if (lemma == null) continue;

                    if (documentByWords.putIfAbsent(lemma, new HashSet<>(x)) != null) documentByWords.get(lemma).add(x);

                    if (wordsCounter.putIfAbsent(lemma, 1) != null) wordsCounter.put(lemma, wordsCounter.get(lemma) + 1);
                    if (wordsIndexing.putIfAbsent(lemma, k) == null) k++;

                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
                                    "Take everything but (except) the period. All the other like , or [] or something like 123 should remain the same"
*/