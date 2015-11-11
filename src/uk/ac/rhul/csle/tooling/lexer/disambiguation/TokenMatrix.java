package uk.ac.rhul.csle.tooling.lexer.disambiguation;

import java.util.Arrays;
import java.util.HashMap;

/**
 * A class representing a matrix mapping relations of tokens to each other
 * 
 * @author Robert Michael Walsh
 *
 */
public class TokenMatrix {

  /**
   * The matrix holding the token relations
   */
  private final int[][] matrix;

  /**
   * A mapping of token names to some integer
   */
  private final HashMap<String, Integer> mappings;

  /**
   * Constructs a new <code>TokenMatrix</code> for the given set of tokens,
   * using the given mapping of token names to integers.
   * <p>
   * The token -> integer mapping must be defined so that no integer is greater
   * than the number of tokens. The resulting matrix will be of size
   * <code>|tokens|x|tokens|</code>. The token -> integer mappings are used to
   * determine the row/column in the matrix that this token appears.
   * 
   * @param tokens
   *          The set of tokens
   * @param tokenIntegerMappings
   *          The mapping of tokens to integer values
   */
  public TokenMatrix(String[] tokens, HashMap<String, Integer> mappings) {
    matrix = new int[tokens.length][tokens.length];
    this.mappings = mappings;
  }

  /**
   * Sets the cell in the matrix corresponding to the <code>tokenA</code> row
   * and <code>tokenB</code> column to 1.
   * 
   * @param tokenA
   *          The token row
   * @param tokenB
   *          The token column
   */
  public void addRelation(String tokenA, String tokenB) {
    matrix[mappings.get(tokenA)][mappings.get(tokenB)] = 1;
  }

  /**
   * Returns true if the cell in the matrix corresponding to the
   * <code>tokenA</code> row and <code>tokenB</code> column is 1
   * 
   * @param tokenA
   *          The token row
   * @param tokenB
   *          The token column
   * @return
   */
  public boolean isRelationBetween(String tokenA, String tokenB) {
    return matrix[mappings.get(tokenA)][mappings.get(tokenB)] == 1;
  }

  /**
   * Fills the entire matrix with zeroes.
   */
  public void clearMatrix() {
    for (int[] row : matrix) {
      Arrays.fill(row, 0);
    }
  }
}
