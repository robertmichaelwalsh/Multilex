package uk.ac.rhul.csle.tooling.lexer;

/**
 * A DFAMap is an interface between a <code>RegularLexer</code> and the
 * particular lexer specification.
 * <p>
 * In order to create a regular multilexer for a given lexer specification, the
 * code for the specific lexer should implement the methods contained in this
 * interface.
 * 
 * @author Robert Michael Walsh
 *
 */
public interface DFAMap {

  /**
   * Takes the given token name and returns true or false depending on whether
   * or not the DFA for this token is currently at an accepting state.
   * 
   * @param token
   *          The token whose DFA should be tested
   * @return True if the DFA for this token is in an accepting state, false
   *         otherwise
   */
  public boolean atAcceptingState(String token);

  /**
   * Takes a token name and a character. For the current state of the DFA for
   * this token, if there exists an out-edge labelled with the character, then a
   * transition is performed on this edge. If a transition was made, then the
   * function returns true, otherwise it returns false.
   * 
   * @param token
   *          The token whose DFA should be tested
   * @param input
   *          The character to act as a candidate for transition.
   * @return True if a transition was applied, false otherwise
   */
  public boolean applyTransition(String token, char input);

  /**
   * Sets the current state of the DFA for this token to the start state.
   * 
   * @param token
   *          The token whose DFA should be reset
   */
  public void reset(String token);

  /**
   * Returns an array of all the token names that are considered to be
   * whitespace.
   * 
   * @return The set of layout/whitespace token names
   */
  public String[] getLayouttokens();

  /**
   * Returns an array of all the token names in the lexical specification
   * 
   * @return The set of token names in the specification
   */
  public String[] getTokens();

  /**
   * Returns true if the given token has a pattern containing a single string,
   * otherwise it returns false.
   * 
   * @param token
   *          The token to test
   * @return True if the token is a keyword token, false otherwise.
   */
  public boolean isKeyword(String token);

  /**
   * Returns true if the given token is a layout token, otherwise it returns
   * false.
   * 
   * @param token
   *          The token to test
   * @return True if the token is a layout token, false otherwise
   */
  public boolean isLayout(String token);

  /**
   * Has two possible results. If the token has a pattern containing a single
   * string that is not the same as the token name, then that string is
   * returned. Otherwise, the token name is returned.
   * 
   * @param token
   *          The token to retrieve the string for
   * @return If the token is associated with a single pattern, return that
   *         pattern, otherwise (or if the single pattern is the token name)
   *         return the token name
   */
  public String getKeywordString(String token);

}
