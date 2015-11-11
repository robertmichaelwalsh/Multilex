package uk.ac.rhul.csle.tooling.lexer;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;

import uk.ac.rhul.csle.tooling.lexer.disambiguation.EqualRelationLexStrategy;
import uk.ac.rhul.csle.tooling.lexer.disambiguation.LeftLongestLexStrategy;
import uk.ac.rhul.csle.tooling.lexer.disambiguation.LeftShortestLexStrategy;
import uk.ac.rhul.csle.tooling.lexer.disambiguation.LexStrategy;
import uk.ac.rhul.csle.tooling.lexer.disambiguation.RightLongestLexStrategy;
import uk.ac.rhul.csle.tooling.lexer.disambiguation.RightShortestLexStrategy;
import uk.ac.rhul.csle.tooling.parsing.InvalidParseException;

/**
 * 
 * An abstract class containing the methods and fields for processing TWE sets.
 * <p>
 * This class contains only implementations of methods that are applicable for
 * all implementations of a Multilexer. It does not implement the methods (lex,
 * lexSegmented, lexWithoutSideEffects, lexSegmentedWithoutSideEffects) for
 * constructing the initial set. <code>RegularLexer</code> is an extension that
 * implements a DFA approach for constructing the TWE set, whilst
 * <code>GLLLexer</code> is an extension that implements a GLL approach.
 * 
 * @author Robert Michael Walsh
 *
 */
@SuppressWarnings("deprecation")
public abstract class MultiLexer {

  /**
   * A copy of the TWE set after lexical disambiguation
   */
  protected Set<TokenTriple> disambiguated;

  /**
   * The original input string
   */
  private String input;

  /**
   * The length of the original input string
   */
  private int inputLength;

  /**
   * The highest index in the TWE set
   */
  private int lastIndex;

  /**
   * The set of tokens that are considered to be layout/whitespace
   */
  private String[] layoutTokens;

  /**
   * A LexStrategy storing the class 2 disambiguation rules
   */
  private LexStrategy llongestMatch;

  /**
   * A LexStrategy storing the class 2a disambiguation rules
   */
  private LexStrategy rlongestMatch;

  /**
   * Stores the mode (UNIT or CHARACTER) used for determining the triple extents
   */
  protected ExtentMode mode;

  /**
   * The directory to place any output
   */
  private final String output_directory;

  /**
   * A LexStrategy storing the class 1 disambiguation rules
   */
  private LexStrategy rpriority;

  /**
   * A LexStrategy storing the class 3 disambiguation rules
   */
  private LexStrategy lshortestMatch;

  /**
   * A LexStrategy storing the class 3a disambiguation rules
   */
  private LexStrategy rshortestMatch;

  /**
   * A set keeping track of all tokens that are associated with a lexical
   * disambiguation rule
   */
  private Set<String> tokensWithAssociatedRule;

  /**
   * The set of triples initially constructed (prior to lexical disambiguation
   */
  protected Set<TokenTriple> triples;

  /**
   * Constructs a new instance of a MultiLexer with CHARACTER mode used as the
   * mode for determining extents
   * 
   * @param output_directory
   *          The directory that output should be placed
   * @param tokens
   *          The set of tokens for this lexer
   * @param layoutTokens
   *          The set of tokens to be considered layout/whitespace tokens
   */
  public MultiLexer(String output_directory, String[] tokens, String[] layoutTokens) {
    initialise(tokens, layoutTokens);
    this.output_directory = output_directory;
    mode = ExtentMode.CHARACTER;
  }

  /**
   * Constructs a new instance of a MultiLexer with the given mode used as the
   * mode for determining extents
   * 
   * @param output_directory
   *          The directory that output should be placed
   * @param tokens
   *          The set of tokens for this lexer
   * @param layoutTokens
   *          The set of tokens to be considered layout/whitespace tokens
   * @param mode
   *          The mode to be used (CHARACTER or UNIT) for determining the
   *          extents
   */
  public MultiLexer(String output_directory, String[] tokens, String[] layoutTokens, ExtentMode mode) {
    initialise(tokens, layoutTokens);
    this.output_directory = output_directory;
    this.mode = mode;
  }

  /**
   * Constructs a new instance of a MultiLexer with CHARACTER mode used as the
   * mode for determining extents and a default output directory of "output".
   * 
   * @param tokens
   *          The set of tokens for this lexer
   * @param layoutTokens
   *          The set of tokens to be considered layout/whitespace tokens
   */
  public MultiLexer(String[] tokens, String[] layoutTokens) {
    initialise(tokens, layoutTokens);
    output_directory = "output";
    mode = ExtentMode.CHARACTER;
  }

  /**
   * Constructs a new instance of a MultiLexer with the given mode used as the
   * mode for determining extents and a default output directory of "output".
   * 
   * @param tokens
   *          The set of tokens for this lexer
   * @param layoutTokens
   *          The set of tokens to be considered layout/whitespace tokens
   * @param mode
   *          The mode to be used (CHARACTER or UNIT) for determining the
   *          extents
   */
  public MultiLexer(String[] tokens, String[] layoutTokens, ExtentMode mode) {
    initialise(tokens, layoutTokens);
    output_directory = "output";
    this.mode = mode;
  }

  /**
   * Adds a rule stating that <code>tokenA</code> relates to itself for class 2
   * lexical disambiguation
   * 
   * @param tokenA
   *          The token that should relate to itself
   */
  public void addLeftLongestGrouping(String tokenA) {
    llongestMatch.addRule(tokenA, tokenA);
    tokensWithAssociatedRule.add(tokenA);
  }

  /**
   * For <code>tokens[0]</code>,...,<code>tokens[n]</code>, adds rules relating
   * each <code>tokens[i]</code> for every <code>tokens[j]</code> for every 0 <=
   * i <= j <= n for class 2 lexical disambiguation
   * 
   * @param tokens
   *          The list of tokens to relate
   */
  public void addLeftLongestGrouping(String... tokens) {
    for (String tokenA : tokens) {
      for (String tokenB : tokens) {
        llongestMatch.addRule(tokenA, tokenB);
      }
    }
    tokensWithAssociatedRule.addAll(Arrays.asList(tokens));
  }

  /**
   * Adds a rule that <code>tokenA</code> relates to <code>tokenB</code> for
   * class 2 lexical disambiguation.
   * 
   * @param tokenA
   *          The first token to relate
   * @param tokenB
   *          The second token to relate
   */
  public void addLeftLongestGrouping(String tokenA, String tokenB) {
    llongestMatch.addRule(tokenA, tokenB);
    tokensWithAssociatedRule.add(tokenA);
    tokensWithAssociatedRule.add(tokenB);
  }

  /**
   * Adds a rule that <code>tokenA</code> relates to <code>tokenB</code> for
   * class 2 lexical disambiguation.
   * <p>
   * If <code>omnidirectional</code> is true then also adds a rule that
   * <code>tokenB</code> relates to <code>tokenA</code>, a rule that
   * <code>tokenA</code> relates to <code>tokenA</code>, and a rule that
   * <code>tokenB</code> relates to <code>tokenB</code> for class 2 lexical
   * disambiguation.
   * 
   * @param tokenA
   *          The first token to relate
   * @param tokenB
   *          The second token to relate
   * @param omnidirectional
   *          True if the relation should be bidirectional and reflexive, false
   *          otherwise
   */
  public void addLeftLongestGrouping(String tokenA, String tokenB, boolean omnidirectional) {
    if (omnidirectional) {
      addLeftLongestGrouping(tokenA);
      addLeftLongestGrouping(tokenB);
      addLeftLongestGrouping(tokenB, tokenA);
    }
    addLeftLongestGrouping(tokenA, tokenB);
  }

  /**
   * Adds a rule stating that <code>tokenA</code> relates to itself for class 2a
   * lexical disambiguation
   * 
   * @param tokenA
   *          The token that should relate to itself
   */
  public void addRightLongestGrouping(String tokenA) {
    rlongestMatch.addRule(tokenA, tokenA);
    tokensWithAssociatedRule.add(tokenA);
  }

  /**
   * For <code>tokens[0]</code>,...,<code>tokens[n]</code>, adds rules relating
   * each <code>tokens[i]</code> for every <code>tokens[j]</code> for every 0 <=
   * i <= j <= n for class 2a lexical disambiguation
   * 
   * @param tokens
   *          The list of tokens to relate
   */
  public void addRightLongestGrouping(String... tokens) {
    for (String tokenA : tokens) {
      for (String tokenB : tokens) {
        rlongestMatch.addRule(tokenA, tokenB);
      }
    }
    tokensWithAssociatedRule.addAll(Arrays.asList(tokens));
  }

  /**
   * Adds a rule that <code>tokenA</code> relates to <code>tokenB</code> for
   * class 2a lexical disambiguation.
   * 
   * @param tokenA
   *          The first token to relate
   * @param tokenB
   *          The second token to relate
   */
  public void addRightLongestGrouping(String tokenA, String tokenB) {
    rlongestMatch.addRule(tokenA, tokenB);
    tokensWithAssociatedRule.add(tokenA);
    tokensWithAssociatedRule.add(tokenB);
  }

  /**
   * Adds a rule that <code>tokenA</code> relates to <code>tokenB</code> for
   * class 2a lexical disambiguation.
   * <p>
   * If <code>omnidirectional</code> is true then also adds a rule that
   * <code>tokenB</code> relates to <code>tokenA</code>, a rule that
   * <code>tokenA</code> relates to <code>tokenA</code>, and a rule that
   * <code>tokenB</code> relates to <code>tokenB</code> for class 2a lexical
   * disambiguation.
   * 
   * @param tokenA
   *          The first token to relate
   * @param tokenB
   *          The second token to relate
   * @param omnidirectional
   *          True if the relation should be bidirectional and reflexive, false
   *          otherwise
   */
  public void addRightLongestGrouping(String tokenA, String tokenB, boolean omnidirectional) {
    if (omnidirectional) {
      addRightLongestGrouping(tokenA);
      addRightLongestGrouping(tokenB);
      addRightLongestGrouping(tokenB, tokenA);
    }
    addRightLongestGrouping(tokenA, tokenB);
  }

  /**
   * Adds a rule that <code>tokenA</code> relates to <code>tokenB</code> for
   * class 1 lexical disambiguation.
   * 
   * @param tokenA
   *          The first token to relate
   * @param tokenB
   *          The second token to relate
   */
  public void addRestrictedPriorityGrouping(String tokenA, String tokenB) {
    rpriority.addRule(tokenA, tokenB);
    tokensWithAssociatedRule.add(tokenA);
    tokensWithAssociatedRule.add(tokenB);
  }

  /**
   * Adds a rule stating that <code>tokenA</code> relates to itself for class 3
   * lexical disambiguation
   * 
   * @param tokenA
   *          The token that should relate to itself
   */
  public void addLeftShortestGrouping(String tokenA) {
    lshortestMatch.addRule(tokenA, tokenA);
    tokensWithAssociatedRule.add(tokenA);
  }

  /**
   * For <code>tokens[0]</code>,...,<code>tokens[n]</code>, adds rules relating
   * each <code>tokens[i]</code> for every <code>tokens[j]</code> for every 0 <=
   * i <= j <= n for class 3 lexical disambiguation
   * 
   * @param tokens
   *          The list of tokens to relate
   */
  public void addLeftShortestGrouping(String... tokens) {
    for (String tokenA : tokens) {
      for (String tokenB : tokens) {
        lshortestMatch.addRule(tokenA, tokenB);
      }
    }
    tokensWithAssociatedRule.addAll(Arrays.asList(tokens));
  }

  /**
   * Adds a rule that <code>tokenA</code> relates to <code>tokenB</code> for
   * class 3 lexical disambiguation.
   * 
   * @param tokenA
   *          The first token to relate
   * @param tokenB
   *          The second token to relate
   */
  public void addLeftShortestGrouping(String tokenA, String tokenB) {
    lshortestMatch.addRule(tokenA, tokenB);
    tokensWithAssociatedRule.add(tokenA);
    tokensWithAssociatedRule.add(tokenB);
  }

  /**
   * Adds a rule stating that <code>tokenA</code> relates to itself for class 3a
   * lexical disambiguation
   * 
   * @param tokenA
   *          The token that should relate to itself
   */
  public void addRightShortestGrouping(String tokenA) {
    rshortestMatch.addRule(tokenA, tokenA);
    tokensWithAssociatedRule.add(tokenA);
  }

  /**
   * For <code>tokens[0]</code>,...,<code>tokens[n]</code>, adds rules relating
   * each <code>tokens[i]</code> for every <code>tokens[j]</code> for every 0 <=
   * i <= j <= n for class 3a lexical disambiguation
   * 
   * @param tokens
   *          The list of tokens to relate
   */
  public void addRightShortestGrouping(String... tokens) {
    for (String tokenA : tokens) {
      for (String tokenB : tokens) {
        rshortestMatch.addRule(tokenA, tokenB);
      }
    }
    tokensWithAssociatedRule.addAll(Arrays.asList(tokens));
  }

  /**
   * Adds a rule that <code>tokenA</code> relates to <code>tokenB</code> for
   * class 3a lexical disambiguation.
   * 
   * @param tokenA
   *          The first token to relate
   * @param tokenB
   *          The second token to relate
   */
  public void addRightShortestGrouping(String tokenA, String tokenB) {
    rshortestMatch.addRule(tokenA, tokenB);
    tokensWithAssociatedRule.add(tokenA);
    tokensWithAssociatedRule.add(tokenB);
  }

  /**
   * Adds a rule that <code>tokenA</code> relates to <code>tokenB</code> for
   * class 3 lexical disambiguation.
   * <p>
   * If <code>omnidirectional</code> is true then also adds a rule that
   * <code>tokenB</code> relates to <code>tokenA</code>, a rule that
   * <code>tokenA</code> relates to <code>tokenA</code>, and a rule that
   * <code>tokenB</code> relates to <code>tokenB</code> for class 3 lexical
   * disambiguation.
   * 
   * @param tokenA
   *          The first token to relate
   * @param tokenB
   *          The second token to relate
   * @param omnidirectional
   *          True if the relation should bidirectional and reflexive, false
   *          otherwise
   */
  public void addLeftShortestGrouping(String tokenA, String tokenB, boolean omnidirectional) {
    if (omnidirectional) {
      addLeftShortestGrouping(tokenA);
      addLeftShortestGrouping(tokenB);
      addLeftShortestGrouping(tokenB, tokenA);
    }
    addLeftShortestGrouping(tokenA, tokenB);
  }

  /**
   * Adds a rule that <code>tokenA</code> relates to <code>tokenB</code> for
   * class 3a lexical disambiguation.
   * <p>
   * If <code>omnidirectional</code> is true then also adds a rule that
   * <code>tokenB</code> relates to <code>tokenA</code>, a rule that
   * <code>tokenA</code> relates to <code>tokenA</code>, and a rule that
   * <code>tokenB</code> relates to <code>tokenB</code> for class 3 lexical
   * disambiguation.
   * 
   * @param tokenA
   *          The first token to relate
   * @param tokenB
   *          The second token to relate
   * @param omnidirectional
   *          True if the relation should bidirectional and reflexive, false
   *          otherwise
   */
  public void addRightShortestGrouping(String tokenA, String tokenB, boolean omnidirectional) {
    if (omnidirectional) {
      addRightShortestGrouping(tokenA);
      addRightShortestGrouping(tokenB);
      addRightShortestGrouping(tokenB, tokenA);
    }
    addRightShortestGrouping(tokenA, tokenB);
  }

  /**
   * Remove all lexical disambiguation rule relations from this lexer
   */
  public void clearStrategies() {
    lshortestMatch.clearStrategy();
    llongestMatch.clearStrategy();
    rshortestMatch.clearStrategy();
    rlongestMatch.clearStrategy();
    rpriority.clearStrategy();
    tokensWithAssociatedRule.clear();
  }

  /**
   * Initially prunes the given TWE set and then applies all applicable lexical
   * disambiguation rules as defined by the schema, and returns the pruned
   * lexically disambiguated set
   * 
   * @param triples
   *          The TWE set to lexically disambiguate
   * @return A lexically disambiguated and pruned TWE set
   */
  public Set<TokenTriple> disambiguate(Set<TokenTriple> triples) {
    return disambiguate(triples, false);
  }

  /**
   * Applies all applicable lexical disambiguation rules as defined by the
   * schema, and returns the pruned lexically disambiguated set
   * <p>
   * If <code>assumeIsTight</code> is false, then the given TWE set is initially
   * pruned then disambiguated. Otherwise, the TWE set is disambiguated without
   * an additional pruning step (WARNING: this can lead to erroneous results if
   * the TWE set is not tight and not consistent)
   * 
   * @param triples
   *          The TWE set to lexically disambiguate
   * @param assumeIsTight
   *          True, if the TWE set should be treated as if it is already tight
   *          and consistent, false in order to perform an additional pruning
   *          step
   * @return A lexically disambiguated and pruned TWE set
   */
  protected Set<TokenTriple> disambiguate(Set<TokenTriple> triples, boolean assumeIsTight) {
    Set<TokenTriple> sigma1;
    if (assumeIsTight) {
      sigma1 = triples;
    } else {
      sigma1 = prune(triples);
    }

    // We only need to look at triples that have an associated rule with them.
    // As lexical disambiguation is performed on common left extents, group the
    // triples by left extent
    Set<TokenTriple> relevantSet = sigma1.stream().filter(t -> tokensWithAssociatedRule.contains(t.getTokenName()))
            .collect(Collectors.toSet());
    Map<Integer, Set<TokenTriple>> leftMapping = getLeftIndexedMapping(relevantSet);
    for (Integer i : leftMapping.keySet()) {
      Set<TokenTriple> list = leftMapping.get(i);
      for (TokenTriple t1 : list) {
        list.stream().filter(t2 -> t1 != t2).forEach(t2 -> {
          llongestMatch.apply(t1, t2);
          lshortestMatch.apply(t1, t2);
          rpriority.apply(t1, t2);
        });
      }
    }
    Map<Integer, Set<TokenTriple>> rightMapping = getRightIndexedMapping(relevantSet);
    for (Integer i : rightMapping.keySet()) {
      Set<TokenTriple> list = rightMapping.get(i);
      for (TokenTriple t1 : list) {
        list.stream().filter(t2 -> t1 != t2).forEach(t2 -> {
          rlongestMatch.apply(t1, t2);
          rshortestMatch.apply(t1, t2);
        });
      }
    }
    Set<TokenTriple> sigmaP = new HashSet<>();
    // If the result of lexical disambiguation does not result in any changes,
    // this extra pruning step is not needed
    boolean changed = false;
    for (TokenTriple t : sigma1) {
      if (t.isMarkedForDeletion()) {
        changed = true;
      } else {
        sigmaP.add(t);
      }
    }
    if (changed) {
      Set<TokenTriple> newSet = prune(sigmaP);
      // If the set is empty then this may be because the user has an
      // ill-defined disambiguation scheme, so give a warning
      if (newSet.isEmpty()) {
        System.out.println("WARNING: Lexical disambiguation scheme application resulted in an empty TWE set.");
      }
      return newSet;
    } else {
      return sigmaP;
    }
  }

  /**
   * 
   * Returns the pruned and disambiguated TWE set
   * 
   * @return The pruned and disambiguated TWE set
   */
  public Set<TokenTriple> getDisambiguated() {
    return disambiguated;
  }

  /**
   * Returns the original character string used
   * 
   * @return The original character string used
   */
  protected String getInput() {
    return input;
  }

  /**
   * Returns the length of the input character string
   * 
   * @return The length of the input character string
   */
  protected int getInputLength() {
    return inputLength;
  }

  /**
   * Returns the set of layout/whitespace tokens
   * 
   * @return The set of layout/whitespace tokens
   */
  protected String[] getLayoutTokens() {
    return layoutTokens;
  }

  /**
   * Returns a HashMap that maps triples using their left extents as their key
   * values. The keys in this HashMap are indexes. The values of this HashMap
   * are the set of triples that have this index as their left-extent
   * 
   * @param tripleSet
   *          The set of triples to sort into the HashMap
   * @return A HashMap containing mappings from indexes to sets of triples with
   *         the index as their left extent
   */
  protected Map<Integer, Set<TokenTriple>> getLeftIndexedMapping(Set<TokenTriple> tripleSet) {
    Map<Integer, Set<TokenTriple>> mapping = new HashMap<>();

    for (TokenTriple t : tripleSet) {
      if (!mapping.containsKey(t.getLeftExtent())) {
        mapping.put(t.getLeftExtent(), new HashSet<>());
      }
      mapping.get(t.getLeftExtent()).add(t);
    }

    // Add a last mapping with an empty list for sanity
    mapping.put(getInputLength() - 1, new HashSet<>());

    return mapping;
  }

  /**
   * 
   * Returns the output directory that was specified for this lexer
   * 
   * @return the output directory specified for this lexer
   */
  public String getOutput_directory() {
    return output_directory;
  }

  /**
   * Returns a HashMap that maps triples using their right extents as their key
   * values. The keys in this HashMap are indexes. The values of this HashMap
   * are the set of triples that have this index as their right-extent
   * 
   * @param tripleSet
   *          The set of triples to sort into the HashMap
   * @return A HashMap containing mappings from indexes to sets of triples with
   *         the index as their right extent
   */
  protected Map<Integer, Set<TokenTriple>> getRightIndexedMapping(Set<TokenTriple> tripleSet) {
    Map<Integer, Set<TokenTriple>> mapping = new HashMap<>();

    for (TokenTriple t : tripleSet) {
      if (!mapping.containsKey(t.getRightExtent())) {
        mapping.put(t.getRightExtent(), new HashSet<>());
      }
      mapping.get(t.getRightExtent()).add(t);
    }

    // Add a last mapping with an empty list for sanity
    mapping.put(0, new HashSet<>());

    return mapping;
  }

  /**
   * Returns the number of indexed token strings embedded in the given set. As
   * this number grows exponentially, a BigInteger is used to represent this
   * value.
   * 
   * @param triples
   *          The TWE set to count the embedded tokenisations on
   * @return The number of embedded indexed token strings
   */
  public BigInteger getTokenisationCount(Set<TokenTriple> triples) {
    Map<Integer, Set<TokenTriple>> tripleRightMapping = getRightIndexedMapping(triples);
    Map<Integer, BigInteger> counter = new HashMap<>();
    Stack<Integer> stack = new Stack<>();
    stack.push(0);
    counter.put(0, new BigInteger("1"));

    for (int i = 1; i < getInputLength(); ++i) {
      counter.put(i, new BigInteger("0"));
      if (tripleRightMapping.containsKey(i)) {
        for (TokenTriple triple : tripleRightMapping.get(i)) {
          counter.put(i, counter.get(i).add(counter.get(triple.getLeftExtent())));
        }
      }
    }

    return counter.get(getInputLength() - 1);
  }

  /**
   * Returns the set of triples that were initially constructed
   * 
   * @return The initial TWE set
   */
  public Set<TokenTriple> getTriples() {
    return triples;
  }

  /**
   * Initialises the fields in the Multilexer, using the given tokens and
   * layoutTokens
   * 
   * @param tokens
   *          The set of tokens for this lexer
   * @param layoutTokens
   *          The set of tokens that are layout or whitespace
   */
  private void initialise(String[] tokens, String[] layoutTokens) {
    triples = new HashSet<>();
    this.layoutTokens = layoutTokens;
    final HashMap<String, Integer> mappings = new HashMap<>();
    int index = 0;
    for (String token : tokens) {
      mappings.put(token, index++);
    }
    llongestMatch = new LeftLongestLexStrategy(tokens, mappings);
    rlongestMatch = new RightLongestLexStrategy(tokens, mappings);
    lshortestMatch = new LeftShortestLexStrategy(tokens, mappings);
    rshortestMatch = new RightShortestLexStrategy(tokens, mappings);
    rpriority = new EqualRelationLexStrategy(tokens, mappings);
    tokensWithAssociatedRule = new HashSet<>();
  }

  /**
   * Performs lexical analysis on the given character string. Specifically, it
   * constructs a TWE set for the given character input string, ensures it is
   * tight and consistent, then applies lexical disambiguation (followed by
   * pruning) on this set.
   * 
   * @param filename
   *          The filename of the original character string
   * @param input
   *          The input character string
   * @throws InvalidParseException
   *           Occurs if it is not possible to construct a tight, consistent TWE
   *           set for the given character string
   */
  public abstract void lex(String filename, String input) throws InvalidParseException;

  /**
   * Performs lexical analysis on the given character string. Specifically, it
   * constructs a TWE set for the given character input string, ensures it is
   * tight and consistent, then applies lexical disambiguation (followed by
   * pruning) on this set.
   * <p>
   * Returns the number of elements that needed to be pruned to give the initial
   * set (prior to disambiguation) if <code>statisticsRun</code> is true.
   * 
   * @param filename
   *          The filename of the original character string
   * @param input
   *          The input character string
   * @param statisticsRun
   *          True if prune statistics should be returned, false otherwise
   * @return 0 if <code>statisticsRuns</code> is false, otherwise the number of
   *         triples that were pruned
   * @throws InvalidParseException
   *           Occurs if it is not possible to construct a tight, consistent TWE
   *           set for the given character string
   */
  public abstract int lex(String filename, String input, boolean statisticsRun) throws InvalidParseException;

  /**
   * Performs lexical analysis on the given character string. Specifically, it
   * constructs a TWE set for the given character input string, ensures it is
   * tight and consistent, then applies lexical disambiguation (followed by
   * pruning) on this set.
   * <p>
   * In this case, the string is assumed to be a list of string sequences
   * separated by a new line, with this new line character being absorbed by the
   * token that follows.
   * 
   * @param filename
   *          The filename of the original character string
   * @param input
   *          The new line separated input character string
   * @throws InvalidParseException
   *           Occurs if it is not possible to construct a tight, consistent TWE
   *           set for the given character string
   */
  public abstract void lexSegmented(String filename, String input) throws InvalidParseException;

  /**
   * Performs lexical analysis on the given character string. Specifically, it
   * constructs a TWE set for the given character input string, ensures it is
   * tight and consistent, then applies lexical disambiguation (followed by
   * pruning) on this set.
   * <p>
   * In this case, the string is assumed to be a list of string sequences
   * separated by a new line, with this new line character being absorbed by the
   * token that follows.
   * <p>
   * Returns the number of elements that needed to be pruned to give the initial
   * set (prior to disambiguation) if <code>statisticsRun</code> is true.
   * 
   * @param filename
   *          The filename of the original character string
   * @param input
   *          The new line separated input character string
   * @param statisticsRun
   *          True if prune statistics should be returned, false otherwise
   * @return 0 if <code>statisticsRuns</code> is false, otherwise the number of
   *         triples that were pruned
   * @throws InvalidParseException
   *           Occurs if it is not possible to construct a tight, consistent TWE
   *           set for the given character string
   */
  public abstract int lexSegmented(String filename, String input, boolean statisticsRun) throws InvalidParseException;

  /**
   * Constructs a TWE set for the given character input string without
   * additional processing.
   * <p>
   * In this case, the string is assumed to be a list of string sequences
   * separated by a new line, with this new line character being absorbed by the
   * token that follows.
   * 
   * @param filename
   *          The filename of the original character string
   * @param input
   *          The new line separated input character string
   * @throws InvalidParseException
   *           Occurs if it is not possible to construct a tight, consistent TWE
   *           set for the given character string
   */
  public abstract void lexSegmentedWithoutSideEffects(String filename, String input) throws InvalidParseException;

  /**
   * Constructs a TWE set for the given character input string without
   * additional processing.
   * 
   * @param filename
   *          The filename of the original character string
   * @param input
   *          The input character string
   * @throws InvalidParseException
   *           Occurs if it is not possible to construct a tight, consistent TWE
   *           set for the given character string
   */
  public abstract void lexWithoutSideEffects(String filename, String input) throws InvalidParseException;

  /**
   * Takes a TWE set and returns the largest tight and consistent subset of this
   * TWE set.
   * 
   * @param purgeSet
   *          The TWE set to prune
   * @return The largest tight and consistent subset of the input TWE set
   */
  public Set<TokenTriple> prune(Set<TokenTriple> purgeSet) {
    if (purgeSet.isEmpty()) {
      System.err.println("Set of triples is empty.");
      return purgeSet;
    }

    Set<TokenTriple> sigma1 = new HashSet<>();
    Map<Integer, Set<TokenTriple>> leftMapping = getLeftIndexedMapping(purgeSet);

    HashSet<Integer> sigma1RightExtents = new HashSet<>();
    ArrayDeque<Integer> sigma1ProcessQueue = new ArrayDeque<>();
    int highestIndex = 0;

    // Construct \Sigma^1 by adding all elements of \Sigma of form (t,n_m,j) for
    // any t,j
    for (TokenTriple t : leftMapping.get(0)) {
      sigma1.add(t);
      final int nextRightExtent = t.getRightExtent();
      if (!sigma1RightExtents.contains(nextRightExtent)) {
        sigma1RightExtents.add(nextRightExtent);
        sigma1ProcessQueue.add(nextRightExtent);
        if (nextRightExtent > highestIndex) {
          highestIndex = nextRightExtent;
        }
      }
    }

    // Form the closure of \Sigma^1 under the property that if (t',i,j) \in
    // \Sigma^1 then (s,j,k) \in \Sigma^1 for all (s,j,k) \in \Sigma
    while (!sigma1ProcessQueue.isEmpty()) {
      int next = sigma1ProcessQueue.poll();
      if (leftMapping.containsKey(next)) {
        for (TokenTriple t : leftMapping.get(next)) {
          sigma1.add(t);
          final int nextRightExtent = t.getRightExtent();
          if (!sigma1RightExtents.contains(nextRightExtent)) {
            sigma1RightExtents.add(nextRightExtent);
            sigma1ProcessQueue.add(nextRightExtent);
            if (nextRightExtent > highestIndex) {
              highestIndex = nextRightExtent;
            }
          }
        }
      }
    }

    Set<TokenTriple> sigmaP = new HashSet<>();

    if (highestIndex < lastIndex) {
      System.err.printf("Lexical analysis failed at input position %d.%n", highestIndex);
      System.err.printf("Failure occurred at:%n%s%n^%n",
              input.substring(highestIndex, (highestIndex + 20 > input.length()) ? input.length() : highestIndex + 20));

      return sigmaP;
    }

    Map<Integer, Set<TokenTriple>> rightMapping = getRightIndexedMapping(sigma1);

    HashSet<Integer> sigmaPLeftExtents = new HashSet<>();
    ArrayDeque<Integer> sigmaPProcessQueue = new ArrayDeque<>();

    // Construct \Sigma' by adding all elements of \Sigma^1 of form (t,i,n_n)
    // for any t,i
    for (TokenTriple t : rightMapping.get(lastIndex)) {
      sigmaP.add(t);
      final int nextLeftExtent = t.getLeftExtent();
      if (!sigmaPLeftExtents.contains(nextLeftExtent)) {
        sigmaPLeftExtents.add(nextLeftExtent);
        sigmaPProcessQueue.add(nextLeftExtent);
      }
    }

    // Form the closure of \Sigma' under the property that if (t',i,j) \in
    // \Sigma' then (s,k,i) \in \Sigma' for all (s,k,i) \in \Sigma^1
    while (!sigmaPProcessQueue.isEmpty()) {
      int next = sigmaPProcessQueue.poll();
      if (rightMapping.containsKey(next)) {
        for (TokenTriple t : rightMapping.get(next)) {
          sigmaP.add(t);
          final int nextLeftExtent = t.getLeftExtent();
          if (!sigmaPLeftExtents.contains(nextLeftExtent)) {
            sigmaPLeftExtents.add(nextLeftExtent);
            sigmaPProcessQueue.add(nextLeftExtent);
          }
        }
      }
    }

    return sigmaP;

  }

  /**
   * Returns the lexer to its pre-lex state (allowing it to cleanly accept a new
   * string.
   */
  public void resetLexer() {
    triples = new HashSet<>();
    disambiguated = null;
  }

  /**
   * Sets the input character string
   * 
   * @param input
   *          The input character string
   */
  protected void setInput(String input) {
    this.input = input;
  }

  /**
   * Sets the input character string length
   * 
   * @param inputLength
   *          The input character string length
   */
  protected void setInputLength(int inputLength) {
    this.inputLength = inputLength;
    this.lastIndex = inputLength - 1;
  }

  /**
   * Sets the set of tokens that are layout/whitespace
   * 
   * @param layoutTokens
   *          The set of layout/whitespace tokens
   */
  protected void setLayoutTokens(String[] layoutTokens) {
    this.layoutTokens = layoutTokens;
  }

  /**
   * Returns a string representing the given set of triples in an ART
   * appropriate format. This format has the character string length as the
   * first line. Each triple is then printed on subsequent lines (separated by a
   * new line), before finishing with a final new line
   * 
   * @param triples
   *          The set of triples to convert to the string format
   * @return The formatted string
   */
  public String toTok(Set<TokenTriple> triples) {
    StringBuilder str = new StringBuilder();
    str.append(inputLength);
    str.append('\n');
    // str.append(triples.get(i).size() + "\n");
    for (TokenTriple t : triples) {
      str.append(t);
      str.append('\n');
    }
    return str.toString();
  }

  /**
   * Returns a string representing the given set of triples in a user-readable
   * format. This format has the character string length as the first line.
   * Every triple is grouped according to their left-extents. For each left
   * extent, the number of triples with the same left extent is printed,
   * followed by the triples sharing this left-extent (separated by a new line).
   * The final line consists of a new line character.
   * 
   * @param triples
   *          The set of triples to convert to the string format
   * @return The formatted string
   */
  public String toTokOrdered(Set<TokenTriple> triples) {
    StringBuilder str = new StringBuilder();
    str.append(inputLength);
    str.append('\n');
    Map<Integer, Set<TokenTriple>> mapping = new TreeMap<>();

    for (TokenTriple t : triples) {
      if (!mapping.containsKey(t.getLeftExtent())) {
        mapping.put(t.getLeftExtent(), new HashSet<>());
      }
      mapping.get(t.getLeftExtent()).add(t);
    }

    // str.append(triples.get(i).size() + "\n");
    for (Integer i : mapping.keySet()) {
      str.append(mapping.get(i).size());
      str.append('\n');
      for (TokenTriple t : mapping.get(i)) {
        str.append(t);
        str.append('\n');
      }
    }
    return str.toString();
  }

  /**
   * Provides the same result as
   * <code>this.toTok(this.getDisambiguated())</code> but using the Object
   * framework
   * 
   * @see MultiLexer#toTok(Set)
   */
  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return toTok(disambiguated);
  }

  /**
   * Provides a GraphViz/DOT format string representing the given set of triples
   * 
   * @param triples
   *          The set of triples to convert to the string format
   * @return The GraphViz/DOT formatted string
   */
  public String toDot(Set<TokenTriple> triples) {
    StringBuilder dot = new StringBuilder();
    dot.append("digraph {\n\trankdir=LR\n" + "\tnode [shape=circle]\n");
    for (TokenTriple triple : triples) {
      dot.append("\t" + triple.getLeftExtent() + " -> " + triple.getRightExtent() + " [label=\"" + triple.getTokenName()
              + "\"]\n");
    }
    dot.append("}");
    return dot.toString();
  }
}
