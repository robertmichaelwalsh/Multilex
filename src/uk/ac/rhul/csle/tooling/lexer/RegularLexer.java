package uk.ac.rhul.csle.tooling.lexer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import uk.ac.rhul.csle.tooling.parsing.InvalidParseException;

/**
 * A Multilexer implementation that uses a set of DFAs as its back end
 * 
 * @author Robert Michael Walsh
 *
 */
public class RegularLexer extends MultiLexer {

  /**
   * A map of all the DFAs in this lexer
   */
  private final DFAMap DFAs;

  /**
   * A map used for UNIT extent mode that maps character extents to their
   * equivalent unit extents
   */
  private final HashMap<Integer, HashSet<Integer>> tokenExtentMap;

  /**
   * Constructs a new instance of a RegularLexer with CHARACTER mode used as the
   * mode for determining extents and a default output directory of "output".
   * 
   * @param map
   *          The lexer specification to use
   */
  public RegularLexer(DFAMap map) {
    super(map.getTokens(), map.getLayouttokens());
    DFAs = map;
    tokenExtentMap = new HashMap<>();
  }

  /**
   * Constructs a new instance of a RegularLexer with the given mode used as the
   * mode for determining extents and a default output directory of "output".
   * 
   * @param map
   *          The lexer specification to use
   * @param mode
   *          The mode to be used (CHARACTER or UNIT) for determining the
   *          extents
   */
  public RegularLexer(DFAMap map, ExtentMode mode) {
    super(map.getTokens(), map.getLayouttokens(), mode);
    DFAs = map;
    tokenExtentMap = new HashMap<>();
  }

  /**
   * Constructs a new instance of a RegularLexer with CHARACTER mode used as the
   * mode for determining extents
   * 
   * @param output_directory
   *          The directory that output should be placed
   * @param map
   *          The lexer specification to use
   */
  public RegularLexer(String output_directory, DFAMap map) {
    super(output_directory, map.getTokens(), map.getLayouttokens());
    DFAs = map;
    tokenExtentMap = new HashMap<>();
  }

  /**
   * Constructs a new instance of a MultiLexer with the given mode used as the
   * mode for determining extents
   * 
   * @param output_directory
   *          The directory that output should be placed
   * @param map
   *          The lexer specification to use
   * @param mode
   *          The mode to be used (CHARACTER or UNIT) for determining the
   *          extents
   */
  public RegularLexer(String output_directory, DFAMap map, ExtentMode mode) {
    super(output_directory, map.getTokens(), map.getLayouttokens(), mode);
    DFAs = map;
    tokenExtentMap = new HashMap<>();
  }

  /**
   * A helper function that finds all matches on a particular token for the
   * input at a given start index and generates triples for all matches
   * 
   * @param input
   *          The input string to analyse
   * @param startIndex
   *          The index to start from
   * @param token
   *          The token to match
   * @param offset
   *          The offset for the extents
   * @return A set of indices indicating the right extents of all triples that
   *         were generated as a result of this call to the function
   */
  private int[] DFAmatch(String input, int startIndex, String token, int offset) {
    int currentIndex = startIndex;
    int[] nextPoss = new int[input.length() - startIndex];
    int arrayPos = 0;

    while (currentIndex < input.length() && DFAs.applyTransition(token, input.charAt(currentIndex))) {
      ++currentIndex;
      if (DFAs.atAcceptingState(token)) {
        if (mode == ExtentMode.UNIT) {
          for (final Integer tokenOffset : tokenExtentMap
                  .get(offset == 0 || startIndex > 0 ? startIndex + offset : startIndex + offset - 1)) {
            triples.add(new TokenTriple(token, tokenOffset,
                    currentIndex + offset == getInputLength() - 1 ? getInputLength() - 1 : tokenOffset + 1));
            if (!tokenExtentMap.containsKey(currentIndex + offset)) {
              tokenExtentMap.put(currentIndex + offset, new HashSet<>());
            }
            tokenExtentMap.get(currentIndex + offset).add(tokenOffset + 1);
          }
        } else {
          triples.add(
                  new TokenTriple(token, offset == 0 || startIndex > 0 ? startIndex + offset : startIndex + offset - 1,
                          currentIndex + offset));
        }
        nextPoss[arrayPos] = currentIndex;
        ++arrayPos;
      }
    }

    DFAs.reset(token);
    if (mode == ExtentMode.UNIT && nextPoss.length > 1) {
      Arrays.sort(nextPoss);
      final int[] tmp = new int[nextPoss.length];
      for (int i = nextPoss.length - 1; i != 0; --i) {
        tmp[nextPoss.length - 1 - i] = nextPoss[i];
      }
      nextPoss = tmp;
    }
    return nextPoss;
  }

  /**
   * A helper function that determines whether a particular keyword token has a
   * match for the input at a given start index and generates a triple for the
   * match if so
   * 
   * @param input
   *          The input string to analyse
   * @param index
   *          The index to start from
   * @param token
   *          The keyword token to match
   * @param string
   *          The keyword pattern for the keyword token
   * @param offset
   *          The offset for the extents
   * @return The right extent of the triple generated, or 0 if no triple was
   *         generated
   */
  private int keywordMatcher(String input, int index, String token, String string, int offset) {
    if (index + string.length() <= input.length()) {
      if (input.substring(index, index + string.length()).equals(string)) {
        if (mode == ExtentMode.UNIT) {
          for (final Integer tokenOffset : tokenExtentMap
                  .get(offset == 0 || index > 0 ? index + offset : index + offset - 1)) {
            triples.add(new TokenTriple(token, tokenOffset,
                    index + string.length() + offset == getInputLength() - 1 ? getInputLength() - 1 : tokenOffset + 1));
            if (!tokenExtentMap.containsKey(index + string.length() + offset)) {
              tokenExtentMap.put(index + string.length() + offset, new HashSet<>());
            }
            tokenExtentMap.get(index + string.length() + offset).add(tokenOffset + 1);
          }
        } else {
          triples.add(new TokenTriple(token, offset == 0 || index > 0 ? index + offset : index + offset - 1,
                  index + string.length() + offset));
        }
        return index + string.length();
      }
    }
    return 0;
  }

  @Override
  public void lex(String filename, String input) throws InvalidParseException {
    lex(filename, input, false);
  }

  @Override
  public int lex(String filename, String input, boolean statisticsRun) throws InvalidParseException {
    lexWithoutSideEffects(filename, input);
    return lexPostProcess(filename, statisticsRun);
  }

  @Override
  public void lexSegmented(String filename, String input) throws InvalidParseException {
    lexSegmented(filename, input, false);
  }

  @Override
  public int lexSegmented(String filename, String input, boolean statisticsRun) throws InvalidParseException {
    if (input.contains("\n\n")) {
      System.err.println("WARNING: String contains a blank line. Possibly not initially processed?");
    }
    lexSegmentedWithoutSideEffects(filename, input);
    return lexPostProcess(filename, statisticsRun);
  }

  private int lexPostProcess(String filename, boolean statisticsRun) throws InvalidParseException {
    if (triples.isEmpty() && getInput().length() != 0) {
      System.err.printf("String %s rejected by lexer%n", filename);
      throw new InvalidParseException();
    }
    int pruned = 0;
    Set<TokenTriple> prunedSet = null;
    if (statisticsRun) {
      pruned = triples.size();
      prunedSet = prune(triples);
      pruned = pruned - triples.size();
    } else {
      prunedSet = prune(triples);
    }
    if (prunedSet.isEmpty() && getInput().length() != 0) {
      System.err.printf("String %s rejected by lexer%n", filename);
      throw new InvalidParseException();
    }

    triples = prunedSet;

    disambiguated = disambiguate(triples, true);
    return pruned;
  }

  @Override
  public void lexSegmentedWithoutSideEffects(String filename, String input) {
    if (input.length() == 0) {
      System.err.println("WARNING: String is empty.");
    }
    setInput(input);
    final String[] segments = input.split("\n");
    int leftOffset = 0;
    setInputLength(input.length() + 1);
    if (mode == ExtentMode.UNIT) {
      tokenExtentMap.put(0, new HashSet<>());
      tokenExtentMap.get(0).add(0);
    }
    for (final String segment : segments) {
      regMatch(segment, leftOffset);
      leftOffset += segment.length() + 1;
    }
  }

  @Override
  public void lexWithoutSideEffects(String filename, String input) {
    setInput(input);
    setInputLength(input.length() + 1);
    regMatch(input, 0);

  }

  /**
   * A helper function that runs the main loop for regular lexical analysis
   * 
   * @param input
   *          The input string
   * @param offset
   *          The offset for the extents
   */
  private void regMatch(String input, int offset) {
    final Stack<Integer> nextPos = new Stack<>();
    final Set<Integer> used = new HashSet<>();
    int highest = 0;
    nextPos.push(0);
    used.add(0);
    while (!nextPos.isEmpty()) {
      final int i = nextPos.pop();
      for (final String token : DFAs.getTokens()) {
        if (DFAs.isKeyword(token)) {
          final String underlying = DFAs.getKeywordString(token);
          final int next = keywordMatcher(input, i, token, underlying, offset);
          if (!used.contains(next)) {
            nextPos.push(next);
            used.add(next);
            if (next > highest) {
              highest = next;
            }
          }
        } else {
          for (final int next : DFAmatch(input, i, token, offset)) {
            if (next == 0) {
              break;
            }
            if (!used.contains(next)) {
              nextPos.push(next);
              used.add(next);
              if (next > highest) {
                highest = next;
              }
            }
          }
        }
      }
    }
    if (highest < input.length()) {
      System.err.printf("Lexical analysis failed at input position %d.%n", (highest + offset));
      System.err.printf("Failure occurred at:%n%s%n^%n",
              input.substring(highest, (highest + 20 > input.length()) ? input.length() : highest + 20));
      triples.clear();
    }
  }
}
