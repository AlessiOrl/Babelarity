package it.uniroma1.lcl.babelarity;

import it.uniroma1.lcl.babelarity.linguisticobject.Document;
import it.uniroma1.lcl.babelarity.linguisticobject.LinguisticObject;
import it.uniroma1.lcl.babelarity.linguisticobject.Synset;
import it.uniroma1.lcl.babelarity.linguisticobject.Word;
import it.uniroma1.lcl.babelarity.strategy.*;
import it.uniroma1.lcl.babelarity.exceptions.DifferentLinguisticObjectException;
import it.uniroma1.lcl.babelarity.exceptions.NoSuchLinguisticObjectException;
import it.uniroma1.lcl.babelarity.exceptions.NoSuchPosException;

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
   * controlla se nel dizionario è presente la parola. se si restituisce il suo lemma (o se stessa se è essa stessa il lemma) o null nel caso in cui non sia presente nel dizionario
   */

  public static String takeWord(String s) {
    return fromInflectedToLemma.containsKey(s) ? fromInflectedToLemma.get(s) : lemmas.contains(s) ? s : null;
  }

  /**
   * restituisce l’insieme di synset che contengono tra i loro sensi la parola in input
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
   * restituisce il synset relativo all’id specificato
   */
  public Synset getSynset(String id) {
    return synsetsMap.get(id);
  }

  /**
   * restituisce uno o più lemmi associati alla parola flessa fornita in input
   */
  public List<String> getLemmas(String word) {
    return List.of(fromInflectedToLemma.get(word));
  }

  /**
   * Restituisce le informazioni inerenti al it.uniroma1.lcl.babelarity.linguisticobject.Synset fornito in input sotto forma di stringa. Il formato della stringa è il seguente: ID\tPOS\tLEMMI\tGLOSSE\tRELAZIONI Le componenti LEMMI, GLOSSE e RELAZIONI possono contenere più elementi, questi sono separati dal carattere ";" Le relazioni devono essere condificate nel seguente formato: TARGETSYNSET_RELNAME   es.
   * bn:00081546n_has-kind
   * <p>
   * es: bn:00047028n	NOUN	word;intelligence;news;tidings	Information about recent and important events	bn:0000001n_has-kind;bn:0000001n_is-a
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
   * calcola e restituisce un double che rappresenta la similarità tra due oggetti linguistici (it.uniroma1.lcl.babelarity.linguisticobject.Synset, Documenti o parole)
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
   * Imposta l’algoritmo di calcolo della similarità tra parole (di default, in fase di costruzione dell’oggetto viene impostato l’algoritmo implementato dallo studente).
   */
  public void setLexicalSimilarityStrategy(LexicalSimilarityStrategy strategy) {
    this.lexicalSimilarityStrategy = strategy;
  }

  /**
   * Imposta l’algoritmo di calcolo della similarità tra synset (di default, in fase di costruzione dell’oggetto viene impostato l’algoritmo implementato dallo studente).
   */
  public void setSemanticSimilarityStrategy(SemanticSimilarityStrategy strategy) {
    this.semanticSimilarityStrategy = strategy;
  }

  /**
   * Imposta l’algoritmo di calcolo della similarità tra documenti (di default, in fase di costruzione dell’oggetto viene impostato l’algoritmo implementato dallo studente).
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

