package it.uniroma1.lcl.babelarity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BabelLexicalSimilarity implements StrategySimilarity
{

    //TODO: Probabile singoletto
    //TODO: Confronto lessicale tra due parole
    private static BabelLexicalSimilarity instance;
    private static Path corpusDir = Paths.get("resources/corpus");
    private static Path stopWordPath = Paths.get("stopWords.txt");
    private HashSet<String> stopWords;
    private Map<String, Integer> wordsIndexing;
    private static List<File> corpusFiles;
    HashMap<String, HashSet<Integer>> wordsInDocument = new HashMap<>();
    HashMap<String, Integer> wordsCounter = new HashMap<>();

    //TODO:AGGIUNGERE PARAMETRO PER TENERE SALVATI I PMI GIA' CALCOALTI

    private BabelLexicalSimilarity()
    {
        corpusFiles = List.of(corpusDir.toFile().listFiles());
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
        wordsIndexing = new HashMap<>();
        int k = 0;
        for (int x = 0; x < corpusFiles.size(); x++)
        {
            try (BufferedReader br = new BufferedReader(new FileReader(corpusFiles.get(x))))
            {
                StringBuilder text = new StringBuilder();
                while (br.ready()) text.append(br.readLine());
                String[] words = text.toString().replaceAll("\\W", " ").toLowerCase().split("\\s+");

                for (String s : words)
                {
                    if (stopWords.contains(s)) continue;

                    String lemma = MiniBabelNet.takeWord(s);

                    if (lemma == null) continue;

                    if (wordsInDocument.putIfAbsent(lemma, new HashSet<>()) != null)
                        wordsInDocument.get(lemma).add(x);

                    if (wordsCounter.putIfAbsent(lemma, 1) != null) wordsCounter.put(lemma,
                                                                                     wordsCounter
                                                                                         .get(
                                                                                             lemma) +
                                                                                     1);
                    if (wordsIndexing.putIfAbsent(lemma, k) == null) k++;

                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        long timeEnd     = System.currentTimeMillis();
        long timeTakenSc = (timeEnd - timeStart) / 1000;
        System.out.println("FINE PARSE CORPUS, time = " + timeTakenSc + " sec");
    }

    private Float[] generatePMI(String s)
    {
        Float[] vettore = new Float[wordsIndexing.keySet().size()];
        for (String p : wordsIndexing.keySet())
        {
            if (s.equals(p)) vettore[wordsIndexing.get(p)] = 1f;
            else
            {
                Set<Integer> intersection = new HashSet<Integer>(
                    wordsInDocument.get(s)); // use the copy constructor
                intersection.retainAll(wordsInDocument.get(p));
                if (wordsCounter.get(p) <= 15 || intersection.size() <= 10)
                    vettore[wordsIndexing.get(p)] = 0f;
                else
                {
                    float numDoc        = (float) corpusFiles.size();
                    float numeratore    = intersection.size() / numDoc;
                    float denominatore1 = wordsCounter.get(s) / numDoc;
                    float denominatore2 = wordsCounter.get(p) / numDoc;
                    vettore[wordsIndexing.get(p)] = numeratore / (denominatore1 * denominatore2);
                }
            }
        }
        return vettore;
    }

    //restituisce il valore di similarità sotto   forma di double
    @Override
    public double computeSimilarity(LinguisticObject o, LinguisticObject o2)
    {
        System.out.println("INIZIO COMPUTE SIMILARITY");
        long timeStart = System.currentTimeMillis();

        String p  = ((Word) o).toString();
        String p2 = ((Word) o2).toString();
        if (p.equals(p2)) return 1;
        double  numeratore    = 0;
        double  denominatore  = 0;
        double  denominatore1 = 0;
        double  denominatore2 = 0;
        Float[] vettore1      = generatePMI(p);
        Float[] vettore2      = generatePMI(p2);
        //TODO: salvare i dati generati
        for (int x = 0; x < vettore1.length; x++)
        {
            numeratore += vettore1[x] * vettore2[x];
            denominatore1 += Math.pow(vettore1[x], 2.0);
            denominatore2 += Math.pow(vettore2[x], 2.0);
        }
        denominatore = Math.sqrt(denominatore1) * Math.sqrt(denominatore2);
        long timeEnd     = System.currentTimeMillis();
        long timeTakenSc = (timeEnd - timeStart) / 1000;
        System.out.println("FINE COMPUTE SIMILARITY , time = " + timeTakenSc + " sec");

        return numeratore / denominatore;
    }

}
