package uk.ac.rhul.csle.tooling.lexer.disambiguation;

import java.util.HashMap;

import uk.ac.rhul.csle.tooling.lexer.TokenTriple;

/**
 * This is an abstract class representing the concept of a class/strategy of
 * lexical disambiguation operations
 * 
 * @author Robert Michael Walsh
 *
 */
public abstract class LexStrategy {

  /**
   * The matrix of relations between tokens
   */
  protected final TokenMatrix matrix;

  /**
   * Constructs a new <code>LexStrategy</code> for the given set of tokens,
   * using the given mapping of token names to integers.
   * <p>
   * The token -> integer mapping must be defined so that no integer is greater
   * than the number of tokens.
   * 
   * @param tokens
   *          The set of tokens to use in this strategy
   * @param tokenIntegerMappings
   *          The mapping of tokens to integer values
   */
  public LexStrategy(String[] tokens, HashMap<String, Integer> tokenIntegerMappings) {
    matrix = new TokenMatrix(tokens, tokenIntegerMappings);
  }

  /**
   * A function that adds a new relation with <code>tokenA</code> as the
   * left-hand side and <code>tokenB</code> as the right-hand side
   * 
   * @param tokenA
   *          The left-hand side of the relation
   * @param tokenB
   *          The right-hand side of the relation
   */
  public void addRule(String tokenA, String tokenB) {
    matrix.addRelation(tokenA, tokenB);
  }

  /**
   * Removes all relations from this strategy.
   */
  public void clearStrategy() {
    matrix.clearMatrix();
  }

  /**
   * Marks <code>t1</code> for deletion if there is a relation with
   * <code>t2</code> and strategy specific rules apply.
   * 
   * @param t1
   *          The left-hand side triple in the test
   * @param t1
   *          the right-hand side triple in the test
   */
  public abstract void apply(TokenTriple t1, TokenTriple t2);

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return matrix.toString();
  }

}