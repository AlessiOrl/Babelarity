package it.uniroma1.lcl.babelarity.strategy;

import it.uniroma1.lcl.babelarity.CorpusManager;
import it.uniroma1.lcl.babelarity.MiniBabelNet;
import it.uniroma1.lcl.babelarity.RelativePaths;
import it.uniroma1.lcl.babelarity.VectorizedLinguisticObj;
import it.uniroma1.lcl.babelarity.linguisticobject.LinguisticObject;
import it.uniroma1.lcl.babelarity.linguisticobject.Word;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class BabelLexicalSimilarityStrategy implements LexicalSimilarityStrategy {

  private static BabelLexicalSimilarityStrategy instance;
  private List<File> corpusFiles;
  private HashMap<String, HashSet<Integer>> documentByWords;
  private Map<String, Integer> wordsIndexing;
  private VectorizedLinguisticObj<Word, Float> vectorizedWords;

  private BabelLexicalSimilarityStrategy() {
    corpusFiles = List.of(Objects.requireNonNull(RelativePaths.CORPUS.toFile().listFiles()));
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

  /**
   * this method parse the Corpus given.
   */
  private void parseCorpus() {
    int k = 0;
    for (int x = 0; x < corpusFiles.size(); x++) {
      try {

        Set<String> DocumentInfo = Arrays.stream((new String(Files.readAllBytes(corpusFiles.get(x).toPath()), StandardCharsets.UTF_8).replaceAll("\\W", " ").toLowerCase().split("\\s+")))
                                         .filter(s -> !(CorpusManager.getStopWords().contains(s)))
                                         .map(MiniBabelNet::takeWord)
                                         .filter(Objects::nonNull)
                                         .collect(Collectors.toSet());

        for (String s : DocumentInfo) {
          documentByWords.putIfAbsent(s, new HashSet<>());
          documentByWords.get(s).add(x);
          if (wordsIndexing.putIfAbsent(s, k) == null)
            k++;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * This method generate the PMI vector of a given Word.
   *
   * @param s
   * @return An Array of float.
   */
  private Float[] generatePMI(String s) {
    Float[] vettore = new Float[wordsIndexing.keySet().size()];
    for (String p : wordsIndexing.keySet()) {
      if (wordsIndexing.get(s).equals(wordsIndexing.get(p)))
        vettore[wordsIndexing.get(p)] = 1f;
      else {
        Set<Integer> intersection = new HashSet<>(documentByWords.get(s)); // use the copy constructor
        intersection.retainAll(documentByWords.get(p));
        float numDoc = (float) documentByWords.get(s).size() + documentByWords.get(p).size();
        float numeratore = intersection.size() / (numDoc-intersection.size());
        float denominatore1 = documentByWords.get(s).size() / numDoc;
        float denominatore2 = documentByWords.get(p).size() / numDoc;
        vettore[wordsIndexing.get(p)] = numeratore / (denominatore1 * denominatore2);
      }
    }
    return vettore;
  }


  /**
   * This method compute the similarity between two Words
   *
   * @param o  the first Word
   * @param o2 the second Word
   * @return the value of similarity
   */
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