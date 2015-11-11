package uk.ac.rhul.csle.tooling.parsing;

import java.math.BigInteger;
import java.util.HashMap;

import uk.ac.rhul.csle.gll.GLLSupport;

/**
 * A class that counts the number of derivations in GLL parser
 * 
 * @author Robert Michael Walsh
 *
 */
public class DerivationCounter {

  /**
   * The ART GLL parser context
   */
  private final GLLSupport parser;

  /**
   * A HashMap storing the running total for each element of the SPPF
   */
  private final HashMap<Integer, BigInteger> previousValues;

  /**
   * Constructs a new <code>DerivationCounter</code> with the given GLL parser
   * context
   * 
   * @param parser
   *          The parser context
   */
  public DerivationCounter(GLLSupport parser) {
    this.parser = parser;
    previousValues = new HashMap<>();
  }

  /**
   * Returns the number derivations embedded in the parser context.
   * <code>BigInteger</code> is used as this number is unbounded in terms of
   * size.
   * 
   * @return The number of derivations in the parser context.
   */
  public BigInteger countDerivations() {
    if (!parser.getInLanguage()) {
      System.err.println("Attempting to count without a valid parse.");
      try {
        throw new InvalidParseException();
      } catch (InvalidParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    parser.sppfResetVisitedFlags();
    BigInteger count = countDerivationsRec(parser.sppfRoot());
    parser.sppfResetVisitedFlags();
    return count;
  }

  /**
   * A helper function for counting the number of derivations
   * 
   * @param element
   *          The current element
   * @return The count of derivations reachable from <code>element</code>
   */
  private BigInteger countDerivationsRec(int element) {
    if (parser.sppfNodeVisited(element)) {
      return previousValues.get(element);
    }
    parser.sppfNodeSetVisited(element);
    BigInteger count = new BigInteger("0");

    // If unambiguous then there is just one derivation
    if (parser.sppfNodeArity(element) == 0) {
      previousValues.put(element, new BigInteger("1"));

      return new BigInteger("1");
    }
    for (int tmp = parser.sppfNodePackNodeList(element); tmp != 0; tmp = parser.sppfPackNodePackNodeList(tmp)) {
      if (!parser.sppfPackNodeSuppressed(tmp)) {
        BigInteger packNodeCount = new BigInteger("0");
        final int leftChild = parser.sppfPackNodeLeftChild(tmp);
        if (leftChild != 0) {
          BigInteger leftChildCount = countDerivationsRec(leftChild);
          packNodeCount = packNodeCount.add(leftChildCount);
        }
        BigInteger rightChildCount = countDerivationsRec(parser.sppfPackNodeRightChild(tmp));
        if (!packNodeCount.equals(new BigInteger("0"))) {
          packNodeCount = packNodeCount.multiply(rightChildCount);
        } else {
          packNodeCount = packNodeCount.add(rightChildCount);
        }
        count = count.add(packNodeCount);
        previousValues.put(element, count);
      }
    }
    return count;
  }
}
