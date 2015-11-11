package uk.ac.rhul.csle.tooling.CSLexer.initialProcessor;

import java.util.HashSet;

import uk.ac.rhul.csle.tooling.lexer.BasicDFAMap;

/**
 * This class represents a finite-state automaton implementation of a the
 * minimal set of C# tokens needed in order to recognise the layout/whitespace
 * in a C# string
 *
 * @author Robert Michael Walsh
 *
 */
public class CSProcessorMap extends BasicDFAMap {

  /**
   * Constructs a new CSProcessorMap instance and initialises all the DFAs
   */
  public CSProcessorMap() {
    super();
  }

  /**
   * Private function initialising all the DFAs in this specification
   */
  @Override
  protected void initialise() {
    /**
     * The list of tokens in C# 1.2 that correspond to layout (or whitespace)
     */
    LAYOUTTOKENS = new String[] { "whitespace", "new_line", "comment" };

    /**
     * The list of tokens in C# 1.2 that correspond to layout (or whitespace) as
     * well as character and string literals
     */
    TOKENS = new String[] { "whitespace", "comment", "new_line", "character_literal", "string_literal" };

    // Not used, initialised purely for sanity
    keywords = new HashSet<>();

    /*
     * comment : ('/' '/' [any unicode character] | '/' '*' ([any unicode
     * character except *] | '*'+ [any unicode character except /])* '*'+ '/'
     */
    final DFA comment = new DFA(8);

    comment.addTransition(0, 1, '/');
    comment.addTransition(1, 2, '/');
    comment.addAllUnicodeTransition(2, 2, '\r', '\n');
    comment.addTransition(1, 3, '*');
    comment.addAllUnicodeTransition(3, 4, '*');
    comment.addTransition(3, 5, '*');
    comment.addAllUnicodeTransition(4, 4, '*');
    comment.addTransition(4, 5, '*');
    comment.addTransition(5, 5, '*');
    comment.addAllUnicodeTransition(5, 6, '/');
    comment.addAllUnicodeTransition(6, 4, '*');
    comment.addTransition(6, 5, '*');
    comment.addTransition(5, 7, '/');
    comment.addAcceptingState(2);
    comment.addAcceptingState(7);

    DFAMap.put("comment", comment);

    /*
     * new_line : ('\r' '\n'? | '\n' )
     */
    final DFA n_l = new DFA(3);

    n_l.addTransition(0, 1, '\r');
    n_l.addTransition(0, 2, '\n');
    n_l.addTransition(1, 2, '\n');
    n_l.addAcceptingState(1);
    n_l.addAcceptingState(2);

    DFAMap.put("new_line", n_l);

    /*
     * whitespace : ( ' ' | '\t' | '\v' | '\f')
     */
    final DFA whitespace = new DFA(2);

    whitespace.addTransition(0, 1, ' ', '\t', '\f');
    whitespace.addAcceptingState(1);

    DFAMap.put("whitespace", whitespace);

    /*
     * character_literal : '\'' [any unicode character] '\''
     */
    final DFA c_l = new DFA(20);

    c_l.addTransition(0, 1, '\'');
    c_l.addAllUnicodeTransition(1, 2, '\'', '\\', '\r', '\n');
    c_l.addTransition(2, 19, '\'');
    c_l.addTransition(1, 3, '\\');
    c_l.addTransition(3, 4, '\'', '"', '\\', '0', 'a', 'b', 'f', 'n', 'r', 't', 'v');
    c_l.addTransition(4, 19, '\'');
    c_l.addTransition(3, 5, 'x');
    c_l.addTransition(5, 6, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    c_l.addTransition(6, 19, '\'');
    c_l.addTransition(6, 7, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    c_l.addTransition(7, 19, '\'');
    c_l.addTransition(7, 8, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    c_l.addTransition(8, 19, '\'');
    c_l.addTransition(8, 9, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    c_l.addTransition(9, 19, '\'');
    c_l.addTransition(3, 10, 'u');
    c_l.addTransition(10, 11, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    c_l.addTransition(11, 12, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    c_l.addTransition(12, 13, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    c_l.addTransition(13, 14, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    c_l.addTransition(14, 19, '\'');
    c_l.addTransition(3, 15, 'U');
    c_l.addTransition(15, 16, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    c_l.addTransition(16, 17, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    c_l.addTransition(17, 18, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    c_l.addTransition(18, 10, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    c_l.addAcceptingState(19);

    DFAMap.put("character_literal", c_l);

    /*
     * string_literal : '"' [any unicode character except " \ or new-line]* '"'
     * | '@' '"' ([any unicode character except "] | '"' '"')* '"'
     */
    final DFA s_l = new DFA(19);

    s_l.addTransition(0, 1, '"');
    s_l.addTransition(0, 16, '@');
    s_l.addAllUnicodeTransition(1, 1, '"', '\\', '\n', '\r');
    s_l.addTransition(1, 2, '\\');
    s_l.addTransition(1, 15, '"');
    s_l.addTransition(2, 3, 'x');
    s_l.addTransition(2, 7, 'u');
    s_l.addTransition(2, 11, 'U');
    s_l.addTransition(2, 1, '\'', '"', '\\', '0', 'a', 'b', 'f', 'n', 'r', 't', 'v');
    s_l.addTransition(3, 4, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    s_l.addTransition(3, 1, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    s_l.addTransition(4, 1, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    s_l.addTransition(4, 5, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    s_l.addTransition(5, 1, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    s_l.addTransition(5, 6, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    s_l.addTransition(6, 1, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    s_l.addTransition(7, 8, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    s_l.addTransition(8, 9, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    s_l.addTransition(9, 10, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    s_l.addTransition(10, 1, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    s_l.addTransition(11, 12, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    s_l.addTransition(12, 13, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    s_l.addTransition(13, 14, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');
    s_l.addTransition(14, 7, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B',
            'C', 'D', 'E', 'F');

    s_l.addTransition(16, 17, '"');
    s_l.addAllUnicodeTransition(17, 17, '"');
    s_l.addTransition(17, 18, '"');
    s_l.addTransition(18, 17, '"');

    s_l.addAcceptingState(15);
    s_l.addAcceptingState(18);

    DFAMap.put("string_literal", s_l);
  }

  /**
   * No keywords are used so always return false
   * 
   */
  @Override
  public boolean isKeyword(String token) {
    return false;
  }
}
