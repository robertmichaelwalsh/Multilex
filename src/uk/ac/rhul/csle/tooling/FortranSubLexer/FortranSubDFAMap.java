package uk.ac.rhul.csle.tooling.FortranSubLexer;

import java.util.Arrays;
import java.util.stream.Collectors;

import uk.ac.rhul.csle.tooling.lexer.BasicDFAMap;

/**
 * This class represents a finite-state automaton implementation of a subset of
 * FORTRAN. This includes only the tokens: ID, DO, INT, LABEL, '=', ','.
 *
 * @author Robert Michael Walsh
 *
 */
public class FortranSubDFAMap extends BasicDFAMap {

  /**
   * Constructs a new FortranSubDFAMap instance and initialises all the Tokens
   * and DFAs
   */
  public FortranSubDFAMap() {
    super();
  }

  @Override
  protected void initialise() {
    /**
     * The tokens that are used in this subset of Fortran
     */
    TOKENS = new String[] { "ID", "DO", "INT", "LABEL", "=", "," };

    /**
     * No layout tokens are used in this specification (this class is in fact
     * being used to test the whitespace insensitivity of Fortran)
     */
    LAYOUTTOKENS = new String[] {};

    /**
     * The set of tokens in this Fortran subset which correspond to a single
     * lexeme
     */
    keywords = Arrays.stream(TOKENS).collect(Collectors.toSet());

    final DFA id = new DFA(2);

    id.addTransition(0, 1, 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');
    id.addTransition(1, 1, 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '_');
    id.addAcceptingState(1);

    DFAMap.put("ID", id);
    keywords.remove("ID");

    final DFA intlit = new DFA(3);

    intlit.addTransition(0, 1, '+', '-');
    intlit.addTransition(0, 2, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    intlit.addTransition(1, 2, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    intlit.addAcceptingState(2);

    DFAMap.put("INT", intlit);
    keywords.remove("INT");

    final DFA label = new DFA(6);

    label.addTransition(0, 1, '1', '2', '3', '4', '5', '6', '7', '8', '9');
    label.addTransition(1, 2, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    label.addTransition(2, 3, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    label.addTransition(3, 4, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    label.addTransition(4, 5, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    label.addAcceptingState(1);
    label.addAcceptingState(2);
    label.addAcceptingState(3);
    label.addAcceptingState(4);
    label.addAcceptingState(5);

    DFAMap.put("LABEL", label);
    keywords.remove("LABEL");
  }

}
