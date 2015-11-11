package uk.ac.rhul.csle.tooling.lexer.disambiguation;

import java.util.HashMap;

import uk.ac.rhul.csle.tooling.lexer.TokenTriple;

/**
 * This is a strategy that stores lexical disambiguation rules for class 3a
 * disambiguation operations
 * 
 * @author Robert Michael Walsh
 *
 */
public class RightShortestLexStrategy extends LexStrategy {

  /**
   * Constructs a new class 3a disambiguation strategy with the given set of
   * tokens and a mapping of tokens to integers
   * 
   * @param tokens
   *          The set of tokens to use in this strategy
   * @param tokenIntegerMappings
   *          The mapping of tokens to integer values
   * 
   * @see uk.ac.rhul.csle.tooling.lexer.disambiguation.LexStrategy#LexStrategy(String[],
   *      HashMap)
   */
  public RightShortestLexStrategy(String[] tokens, HashMap<String, Integer> tokenIntegerMappings) {
    super(tokens, tokenIntegerMappings);
  }

  /**
   * Marks <code>t1</code> for deletion if the strategy contains a relations for
   * <code>t2.getTokenName()</code> under <code>t1.getTokenName()</code> and
   * <code>t1.getLeftExtent() > t2.getLeftExtent()</code>
   * 
   * @param t1
   *          The left-hand side triple in the test
   * @param t1
   *          the right-hand side triple in the test
   */
  @Override
  public void apply(TokenTriple t1, TokenTriple t2) {
    if (matrix.isRelationBetween(t1.getTokenName(), t2.getTokenName()) && t1.getLeftExtent() > t2.getLeftExtent()) {
      t1.setMarkedForDeletion();
    }
  }
}
