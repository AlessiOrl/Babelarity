package it.uniroma1.lcl.babelarity.strategy;

import it.uniroma1.lcl.babelarity.*;
import it.uniroma1.lcl.babelarity.linguisticobject.LinguisticObject;
import it.uniroma1.lcl.babelarity.linguisticobject.VectorizedLinguisticObj;
import it.uniroma1.lcl.babelarity.linguisticobject.Word;
import it.uniroma1.lcl.babelarity.CorpusManager;
import it.uniroma1.lcl.babelarity.MiniBabelNet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BabelLexicalSimilarityStrategy implements LexicalSimilarityStrategy {

  //TODO: PROVARE A CONTARE LE OCCORRENZE SOLO UNA VOLTA PER DOCUMENTO
  private static BabelLexicalSimilarityStrategy instance;
  private List<File> corpusFiles;
  private HashMap<String, HashSet<Integer>> documentByWords;
  private Map<String, Integer> wordsIndexing;
  private HashMap<String, Integer> wordsCounter;
  private VectorizedLinguisticObj<Word, Float> vectorizedWords;

  private BabelLexicalSimilarityStrategy() {
    corpusFiles = List.of(Objects.requireNonNull(RelativePaths.CORPUS.toFile().listFiles()));
    wordsCounter = new HashMap<>();
    wordsIndexing = new HashMap<>();
    documentByWords = new HashMap<>();
    vectorizedWords = new VectorizedLinguisticObj<>();
    this.parseCorpus();
  }

  public static BabelLexicalSimilarityStrategy getInstance() {
    if (instance == null)
      instance = new BabelLexicalSimilarityStrategy();
    return instance;
  }

  private void parseCorpus() {
    int k = 0;
    for (int x = 0; x < corpusFiles.size(); x++) {
      try {
        String[] words = new String(Files.readAllBytes(corpusFiles.get(x).toPath()), StandardCharsets.UTF_8).replaceAll("\\W", " ").toLowerCase().split("\\s+");

        Map<String, Long> DocumentInfo = Arrays.stream(words)
                                               .filter(s -> !(CorpusManager.getStopWords().contains(s)))
                                               .map(MiniBabelNet::takeWord)
                                               .filter(Objects::nonNull)
                                               .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        for (String s : DocumentInfo.keySet()) {
          if (DocumentInfo.get(s) < 2 || DocumentInfo.get(s) > 25)
            continue;
          if (documentByWords.putIfAbsent(s, new HashSet<>(x)) != null)
            documentByWords.get(s).add(x);
          if (wordsIndexing.putIfAbsent(s, k) == null)
            k++;
          if (wordsCounter.putIfAbsent(s, DocumentInfo.get(s).intValue()) != null)
            wordsCounter.put(s, wordsCounter.get(s) + DocumentInfo.get(s).intValue());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private Float[] generatePMI(String s) {
    Float[] vettore = new Float[wordsIndexing.keySet().size()];
    for (String p : wordsIndexing.keySet()) {
      if (wordsIndexing.get(s).equals(wordsIndexing.get(p)))
        vettore[wordsIndexing.get(p)] = 1f;
      else {
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

  //restituisce il valore di similarità sottoforma di double
  @Override
  public double computeSimilarity(LinguisticObject o, LinguisticObject o2) {
    Word p = (Word) o;
    Word p2 = (Word) o2;
    if (p.equals(p2))
      return 1;
    double numeratore = 0;
    double denominatore1 = 0;
    double denominatore2 = 0;

    if (!vectorizedWords.containsKey(p))
      vectorizedWords.put((Word) o, generatePMI(p.toString()));
    if (!vectorizedWords.containsKey(p2))
      vectorizedWords.put((Word) o2, generatePMI(p2.toString()));

    for (int x = 0; x < vectorizedWords.get(p).length; x++) {
      numeratore += vectorizedWords.get(p)[x] * vectorizedWords.get(p2)[x];
      denominatore1 += Math.pow(vectorizedWords.get(p)[x], 2.0);
      denominatore2 += Math.pow(vectorizedWords.get(p2)[x], 2.0);
    }
    return numeratore / (Math.sqrt(denominatore1) * Math.sqrt(denominatore2));
  }

}