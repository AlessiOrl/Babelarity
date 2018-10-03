package it.uniroma1.lcl.babelarity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
    Float[][] pmi;

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
        HashMap<String, HashSet<Integer>> setOfWordsByDocument = new HashMap<>();
        HashMap<String, Integer>          wordsCounter         = new HashMap<>();
        for (int x = 0; x < corpusFiles.size(); x++)
        {
            try (BufferedReader br = new BufferedReader(new FileReader(corpusFiles.get(x))))
            {
                StringBuilder text = new StringBuilder();
                while (br.ready()) text.append(br.readLine());
                //  TODO: DA RIFARE
                String[] words = text.toString().replaceAll("\\W", " ").toLowerCase().split(" ");
                wordsIndexing = new HashMap<>();
                int k = 0;
                for (String s: words)
                {

                    if (stopWords.contains(s) || !(Word.exist(s))) continue;

                    String lemma = Word.fromString(s).getLemma();

                    if (setOfWordsByDocument.putIfAbsent(lemma, new HashSet<>()) != null)
                        setOfWordsByDocument.get(lemma).add(x);

                    if (wordsCounter.putIfAbsent(lemma, 1) != null) wordsCounter.put(lemma, wordsCounter.get(lemma) + 1);
                    wordsIndexing.put(lemma, k);
                    k++;

                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        } pmi = new Float[wordsIndexing.keySet().size()][wordsIndexing.keySet().size()];
        for (String p : wordsCounter.keySet())
        {
            for (String p2 : wordsCounter.keySet())
            {
                if (p.equals(p2)) pmi[wordsIndexing.get(p)][wordsIndexing.get(p2)] = 1f;
                else
                {
                    Set<Integer> intersection = new HashSet<Integer>(
                        setOfWordsByDocument.get(p)); // use the copy constructor
                    intersection.retainAll(setOfWordsByDocument.get(p2));
                    float numDoc        = (float) corpusFiles.size();
                    float numeratore    = intersection.size() / numDoc;
                    float denominatore1 = wordsCounter.get(p) / numDoc;
                    float denominatore2 = wordsCounter.get(p2) / numDoc;
                    pmi[wordsIndexing.keySet().size()][wordsIndexing.keySet().size()] =
                        numeratore / (denominatore1 * denominatore2);
                }
            }
        }
    }

    //restituisce il valore di similarità sotto   forma di double
    @Override
    public double computeSimilarity(LinguisticObject o, LinguisticObject o2)

    {
        String p  = ((Word) o).toString();
        String p2 = ((Word) o2).toString();
        if (p.equals(p2)) return 1;
        double  numeratore   = 0;
        double  denominatore = 0;
        Float[] vettore1     = pmi[wordsIndexing.get(p)];
        Float[] vettore2     = pmi[wordsIndexing.get(p2)];
        for (int x = 0; x < wordsIndexing.size(); x++)
        {
            numeratore += vettore1[x] * vettore2[x];
            denominatore += Math.pow(vettore1[x], 2.0) * Math.pow(vettore2[x], 2);
        }
        return numeratore / denominatore;
    }
}
