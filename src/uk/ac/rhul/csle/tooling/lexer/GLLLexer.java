package uk.ac.rhul.csle.tooling.lexer;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import uk.ac.rhul.csle.gll.GLLSupport;
import uk.ac.rhul.csle.tooling.parsing.InvalidParseException;

/**
 * A Multilexer implementation that uses an ART GLL parser as its back end
 *
 * @author Robert Michael Walsh
 *
 */
public class GLLLexer extends MultiLexer {

  /**
   * The ART GLL Parser to use
   */
  private GLLSupport lexer;

  /**
   * Constructs a new instance of a GLLLexer with CHARACTER mode used as the
   * mode for determining extents
   *
   * @param lexer
   *          The ART GLL Parser to use as the back end
   * @param output_directory
   *          The directory that output should be placed
   * @param tokens
   *          The set of tokens for this lexer
   * @param layoutTokens
   *          The set of tokens to be considered layout/whitespace tokens
   */
  public GLLLexer(GLLSupport lexer, String output_directory, String[] tokens, String[] layoutTokens) {
    super(output_directory, tokens, layoutTokens);
    this.lexer = lexer;
  }

  /**
   * Constructs a new instance of a MultiLexer with the given mode used as the
   * mode for determining extents
   * 
   * @param lexer
   *          The ART GLL Parser to use as the back end
   * @param output_directory
   *          The directory that output should be placed
   * @param tokens
   *          The set of tokens for this lexer
   * @param layoutTokens
   *          The set of tokens to be considered layout/whitespace
   * @param mode
   *          The mode to be used (CHARACTER or UNIT) for determining the
   *          extents
   */
  public GLLLexer(GLLSupport lexer, String output_directory, String[] tokens, String[] layoutTokens, ExtentMode mode) {
    super(output_directory, tokens, layoutTokens, mode);
    this.lexer = lexer;
  }

  /**
   * Constructs a new instance of a GLLLexer with CHARACTER mode used as the
   * mode for determining extents and a default output directory of "output".
   * 
   * @param lexer
   *          The ART GLL Parser to use as the back end
   * @param tokens
   *          The set of tokens for this lexer
   * @param layoutTokens
   *          The set of tokens to be considered layout/whitespace tokens
   */
  public GLLLexer(GLLSupport lexer, String[] tokens, String[] layoutTokens) {
    super(tokens, layoutTokens);
    this.lexer = lexer;
  }

  /**
   * Constructs a new instance of a MultiLexer with the given mode used as the
   * mode for determining extents and a default output directory of "output".
   *
   * @param lexer
   *          The ART GLL Parser to use as the back end
   * @param tokens
   *          The set of tokens for this lexer
   * @param layoutTokens
   *          The set of tokens to be considered layout/whitespace
   * @param mode
   *          The mode to be used (CHARACTER or UNIT) for determining the
   *          extents
   */
  public GLLLexer(GLLSupport lexer, String[] tokens, String[] layoutTokens, ExtentMode mode) {
    super(tokens, layoutTokens, mode);
    this.lexer = lexer;
  }

  /**
   * A function that generates triples by traversing the SPPF from the root
   *
   * @return A set of tokens with extents
   */
  private Set<TokenTriple> generateTriples() {
    final Set<TokenTriple> temp = generateTriplesItr(lexer.sppfRoot(), 0);
    lexer.sppfResetVisitedFlags();
    return temp;
  }

  /**
   * A function that generates triples by traversing the SPPF from the root
   * using an offset for the extents
   *
   * @param offset
   *          The offset for the extents
   * @return A set of tokens with extents
   */
  private Set<TokenTriple> generateTriples(int offset) {
    final Set<TokenTriple> temp = generateTriplesItr(lexer.sppfRoot(), offset);
    lexer.sppfResetVisitedFlags();
    return temp;
  }

  /**
   * A helper function for both <code>generateTriples</code> methods.
   *
   * @param element
   *          The start element
   * @param offset
   *          The offset for the extents
   * @return
   */
  private Set<TokenTriple> generateTriplesItr(int element, int offset) {
    final Set<TokenTriple> temp = new HashSet<>();
    final Stack<Integer> stack = new Stack<>();
    stack.push(element);

    while (!stack.isEmpty()) {
      final int currentElement = stack.pop();
      if (!lexer.sppfNodeVisited(currentElement)) {
        lexer.sppfNodeSetVisited(currentElement);
        if (currentElement == lexer.sppfRoot()
                || lexer.getLabelKind(lexer.sppfNodeLabel(currentElement)) == GLLSupport.ART_K_INTERMEDIATE) {
          for (int tmp = lexer.sppfNodePackNodeList(currentElement); tmp != 0; tmp =
                  lexer.sppfPackNodePackNodeList(tmp)) {
            final int rightChild = lexer.sppfPackNodeRightChild(tmp);
            if (rightChild != 0) {
              stack.push(rightChild);
            }
            final int leftChild = lexer.sppfPackNodeLeftChild(tmp);
            if (leftChild != 0) {
              stack.push(leftChild);
            }
          }
        } else {
          temp.add(new TokenTriple(lexer.labelToString(lexer.sppfNodeLabel(currentElement)),
                  offset == 0 || lexer.sppfNodeLeftExtent(currentElement) > 0
                          ? lexer.sppfNodeLeftExtent(currentElement) + offset
                          : lexer.sppfNodeLeftExtent(currentElement) + offset - 1,
                  lexer.sppfNodeRightExtent(currentElement) + offset));
        }
      }
    }
    return temp;
  }

  /**
   * Returns the GLL Parser used for this lexer
   *
   * @return The ART GLL Parser for this lexer
   */
  public GLLSupport getLexer() {
    return lexer;
  }

  @Override
  public void lex(String filename, String input) throws InvalidParseException {
    lex(filename, input, false);
  }

  @Override
  public int lex(String filename, String input, boolean statisticsRun) throws InvalidParseException {
    lexWithoutSideEffects(filename, input);
    int pruned = 0;
    if (statisticsRun) {
      pruned = triples.size();
      triples = prune(triples);
      pruned = pruned - triples.size();
    } else {
      triples = prune(triples);
    }
    disambiguated = disambiguate(triples, true);

    return pruned;
  }

  public void lexMinimal(String filename, String input) throws InvalidParseException {
    lexer.parse(input);
    if (!lexer.getInLanguage()) {
      System.err.printf("String " + filename + " rejected by lexer\n");
      throw new InvalidParseException();
    }
    setInput(input);
    setInputLength(lexer.getInputLength());
    triples.addAll(generateTriples());
    disambiguated = disambiguate(triples);

  }

  public void lexMinimal(String filename, String input, boolean pruneLayout) throws InvalidParseException {
    lexer.parse(input);
    if (!lexer.getInLanguage()) {
      System.err.printf("String " + filename + " rejected by lexer\n");
      throw new InvalidParseException();
    }
    setInputLength(lexer.getInputLength());
    triples.addAll(generateTriples());
    if (pruneLayout) {
      disambiguated = disambiguate(triples);
    } else {
      disambiguated = disambiguate(triples);
    }

  }

  @Override
  public void lexSegmented(String filename, String input) throws InvalidParseException {
    lexSegmented(filename, input, false);
  }

  @Override
  public int lexSegmented(String filename, String input, boolean statisticsRun) throws InvalidParseException {
    lexSegmentedWithoutSideEffects(filename, input);
    int pruned = 0;
    if (statisticsRun) {
      pruned = triples.size();
      triples = prune(triples);
      pruned = pruned - triples.size();
    } else {
      triples = prune(triples);
    }
    disambiguated = disambiguate(triples, true);

    return pruned;
  }

  @Override
  public void lexSegmentedWithoutSideEffects(String filename, String input) throws InvalidParseException {
    setInput(input);
    final String[] segments = input.split("\n");
    int leftOffset = 0;
    // long totalTime = 0;
    for (final String segment : segments) {
      // long startTime = System.nanoTime();
      lexer.parse(segment);
      // totalTime += System.nanoTime() - startTime;
      if (!lexer.getInLanguage()) {
        System.err.printf("String " + filename + " rejected by lexer\n");
        throw new InvalidParseException();
      }
      triples.addAll(generateTriples(leftOffset));
      leftOffset += segment.length() + 1;
    }
    // System.out.println((totalTime * NANOTOSEC));

    setInputLength(input.length() + 1);
  }

  @Override
  public void lexWithoutSideEffects(String filename, String input) throws InvalidParseException {
    lexer.parse(input);
    if (!lexer.getInLanguage()) {
      System.err.printf("String " + filename + " rejected by lexer\n");
      throw new InvalidParseException();
    }
    setInput(input);
    setInputLength(lexer.getInputLength());
    triples.addAll(generateTriples());

  }

  @Override
  public void resetLexer() {
    try {
      lexer = (GLLSupport) Class.forName(lexer.getClass().getName()).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    super.resetLexer();
  }

}
