package it.uniroma1.lcl.babelarity;

import it.uniroma1.lcl.babelarity.exceptions.DifferentLinguisticObjectException;
import it.uniroma1.lcl.babelarity.exceptions.NoSuchLinguisticObjectException;
import it.uniroma1.lcl.babelarity.exceptions.NoSuchPosException;
import it.uniroma1.lcl.babelarity.strategy.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MiniBabelNet implements Iterable<Synset> {

  private SimilarityStrategy lexicalSimilarityStrategy;
  private SimilarityStrategy semanticSimilarityStrategy;
  private SimilarityStrategy documentSimilarityStrategy;

  private static MiniBabelNet instance;
  private static HashMap<String, String> fromInflectedToLemma = new HashMap<>();
  private static HashSet<String> lemmas = new HashSet<>();

  private HashMap<String, Synset> synsetsMap = new HashMap<>();
  private static List<Synset> synsets = new ArrayList<>();
  public int synsetSize;

  /**
   * {@link MiniBabelNet} is a reduced and simplified english version of <i>{@code BabelNet}</i>.
   * <p>
   * The nodes (or {@link Synset}) are taken from the <i>{@code WordNet}</i> and the relations between them are taken from other resources in <i>{@code BabelNet}</i></p>
   */
  private MiniBabelNet() {
    try (Stream<String> streamLemmatization = Files.lines(RelativePaths.LEMMATIZATIONS);
         Stream<String> streamDictionary = Files.lines(RelativePaths.DICTIONARY);
         Stream<String> streamGlosses = Files.lines(RelativePaths.GLOSSES);
         Stream<String> streamRelations = Files.lines(RelativePaths.RELATIONS)) {
      streamLemmatization
              .map(line -> line.split("\t"))
              .forEach(line -> {
                fromInflectedToLemma.put(line[0], line[1]);
                lemmas.add(line[1]);
              });

      streamDictionary
              .map(line -> line.split("\t", 2))
              .filter(line -> line[0].startsWith("bn"))
              .forEach(line -> {
                try {
                  synsetsMap.put(line[0], new Synset(line[0], new HashSet<>(Arrays.asList(line[1].split("\t")))));
                } catch (NoSuchPosException e) {
                  throw new RuntimeException(e);
                }
              });

      streamGlosses
              .map(line -> line.split("\t", 2))
              .filter(line -> line[0].startsWith("bn"))
              .forEach(line -> synsetsMap.get(line[0]).setGlosses(new HashSet<>(Arrays.asList(line[1].split("\t")))));

      streamRelations
              .map(line -> line.split("\t"))
              .forEach(line -> synsetsMap.get(line[0]).addRelation(line[2], synsetsMap.get(line[1])));

      synsets = List.copyOf(synsetsMap.values());
      synsetSize = synsets.size();

    } catch (IOException | RuntimeException e) {
      e.printStackTrace();

    }
  }

  public static MiniBabelNet getInstance() {
    if (instance == null)
      instance = new MiniBabelNet();
    return instance;
  }

  /**
   * Check if the given word is in the dictionary or not.
   *
   * @return If is present the lemma ({@link String}) of the word or <i>null</i> if not.
   * @param s the String to check. 
   */

  public static String takeWord(String s) {
    return fromInflectedToLemma.containsKey(s) ? fromInflectedToLemma.get(s) : lemmas.contains(s) ? s : null;
  }

  /**
   * Return a list of all synset that contains the meaning of the given word.
   *
   * @return A {@link List} of {@link Synset}.
   * 
   */
  public List<Synset> getSynsets(String word) {
    return synsets.stream()
                  .filter(x -> x.getLemmas().contains(takeWord(word)))
                  .collect(Collectors.toList());
  }

  public static List<Synset> getAllSynsets() {
    return synsets;
  }

  /**
   * Return the Synset associated at the given ID.
   *
   * @return A {@link Synset}.
   */
  public Synset getSynset(String id) {
    return synsetsMap.get(id);
  }

  /**
   * Return one or more lemmas linked with the inflectedform of the given Word.
   *
   * @return A {@link List} of {@link String}.
   */
  public List<String> getLemmas(String word) {
    return List.of(fromInflectedToLemma.get(word));
  }

  /**
   * Return all the info about the {@link Synset Synset} given in input.
   * <p>The output is generated in this form:<br>ID\tPOS\tLEMMAS/tGLOSSES\tRELATIONS<br>
   * LEMMAS, GLOSSES e RELATIONS can contains more than oen element, those need to be separated by the character ";". </p>
   * <p>Relations need to be codificated in the following format:</p>
   * <pre>TARGETSYNSET_RELNAME</pre> <i>es.</i><pre> bn:00081546n_has-kind</pre>
   * <p><i>example of output:</i></p>
   * <pre>bn:00047028n	NOUN	word;intelligence;news;tidings	Information about recent and important events	bn:0000001n_has-kind;bn:0000001n_is-a</pre>
   */
  public String getSynsetSummary(Synset s) {
    StringBuilder ret = new StringBuilder(s.getID() + "\t" + s.getPOS() + "\t");
    Iterator<String> LemmasIterator = s.getLemmas().iterator();

    while (LemmasIterator.hasNext()) {
      ret.append(LemmasIterator.next());
      if (LemmasIterator.hasNext())
        ret.append(";");
    }

    ret.append("\t");
    Iterator<String> glosseIterator = s.getGlosses().iterator();

    while (glosseIterator.hasNext()) {
      ret.append(glosseIterator.next());
      if (glosseIterator.hasNext())
        ret.append(";");
    }

    ret.append("\t");

    ret.append(s.getRelations().entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(bs -> bs.getID() + "_" + entry.getKey()))
                .collect(Collectors.joining(";")));

    return ret.toString();
  }

  /**
   * * Compute and return a {@link Double} that rapresent the similarity between two {@link LinguisticObject}({@link Synset}, {@link Document}, {@link Word})
   *
   * @param o1 First {@code LinguisticObject}
   * @param o2 Second {@code LinguisticObject}
   * @return A {@link Double}
   */
  public double computeSimilarity(LinguisticObject o1, LinguisticObject o2) {
    try {
      if (o1.getClass() != o2.getClass())
        throw new DifferentLinguisticObjectException("The two Linguistic Object have different type");

      if (o1 instanceof Word) {
        if (lexicalSimilarityStrategy == null)
          lexicalSimilarityStrategy = BabelLexicalSimilarityStrategy.getInstance();
        return lexicalSimilarityStrategy.computeSimilarity(o1, o2);
      }
      if (o1 instanceof Synset) {
        if (semanticSimilarityStrategy == null)
          semanticSimilarityStrategy = BabelSemanticSimilarityStrategy.getInstance();
        return semanticSimilarityStrategy.computeSimilarity(o1, o2);
      }
      if (o1 instanceof Document) {
        if (documentSimilarityStrategy == null)
          documentSimilarityStrategy = BabelDocumentSimilarityStrategy.getInstance();
        return documentSimilarityStrategy.computeSimilarity(o1, o2);
      }
      throw new NoSuchLinguisticObjectException("No such similarity use those linguisticObjects");
    } catch (DifferentLinguisticObjectException | NoSuchLinguisticObjectException e) {
      e.printStackTrace();
    }
    return -1;
  }

  /**
   This method sets the algorithm to calculate similarities between {@link Word Words}. By default, during the creation of the class, the strategy is set up with the implemented algorithm created by the student.
   */
  public void setLexicalSimilarityStrategy(LexicalSimilarityStrategy strategy) {
    this.lexicalSimilarityStrategy = strategy;
  }

  /**
   This method sets the algorithm to calculate similarities between {@link Synset Synsets}. By default, during the creation of the class, the strategy is set up with the implemented algorithm created by the student.
   */
  public void setSemanticSimilarityStrategy(SemanticSimilarityStrategy strategy) {
    this.semanticSimilarityStrategy = strategy;
  }

  /**
   This method sets the algorithm to calculate similarities between {@link Document Documents}. By default, during the creation of the class, the strategy is set up with the implemented algorithm created by the student.
   */
  public void setDocumentSimilarityStrategy(DocumentSimilarityStrategy strategy) {
    this.documentSimilarityStrategy = strategy;
  }

  public int getSynsetSize() {
    return synsetSize;
  }

  @Override
  public Iterator<Synset> iterator() {
    return new Iterator<>() {
      private int k;

      @Override
      public boolean hasNext() {
        return k < synsetSize;
      }

      @Override
      public Synset next() {
        return hasNext() ? synsets.get(k++) : null;
      }
    };
  }
}

