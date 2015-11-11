package uk.ac.rhul.csle.tooling.lexer;

/**
 * This class provides a representation of a triple in the Multilexer formalism
 *
 * @author Robert Michael Walsh
 *
 */
public class TokenTriple {

  /**
   * The name of the token element in this triple
   */
  private final String tokenName;

  /**
   * The left extent of this triple
   */
  private int leftExtent;

  /**
   * The right extent of this triple
   *
   */
  private int rightExtent;

  /**
   * A flag to determine whether the triple has been marked for removal (by the
   * lexical disambiguation scheme)
   */
  private boolean markedForDeletion;

  /**
   * Constructs a new triple with the given token, left extent and right extent
   * 
   * @param tokenName
   *          The name of the token element in this triple
   * @param leftExtent
   *          The left extent of this triple
   * @param rightExtent
   *          The right extent of this triple
   */
  public TokenTriple(String tokenName, int leftExtent, int rightExtent) {
    this.tokenName = tokenName;
    this.leftExtent = leftExtent;
    this.rightExtent = rightExtent;
    markedForDeletion = false;
  }

  /**
   * Determines whether two triples are equal. Two triples are equal if they
   * have the same token name, left extent and right extent.
   * 
   * @param obj
   *          The second triple to test against
   * @return True if the two triples are equal, false otherwise
   * 
   * @see java.lang.Object#equals(Object obj)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TokenTriple other = (TokenTriple) obj;
    if (leftExtent != other.leftExtent) {
      return false;
    }
    if (rightExtent != other.rightExtent) {
      return false;
    }
    if (tokenName == null) {
      if (other.tokenName != null) {
        return false;
      }
    } else if (!tokenName.equals(other.tokenName)) {
      return false;
    }
    return true;
  }

  /**
   * Returns the left extent for this triple
   * 
   * @return The left extent of this triple
   */
  public int getLeftExtent() {
    return leftExtent;
  }

  /**
   * Returns the right extent for this triple
   * 
   * @return the right extent of this triple
   */
  public int getRightExtent() {
    return rightExtent;
  }

  /**
   * Returns the token name for this triple
   * 
   * @return the token name of this this triple
   */
  public String getTokenName() {
    return tokenName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + leftExtent;
    result = prime * result + rightExtent;
    result = prime * result + (tokenName == null ? 0 : tokenName.hashCode());
    return result;
  }

  /**
   * Returns whether or not this triple is marked for deleted
   * 
   * @return true if it is marked for deletion, false otherwise
   */
  public boolean isMarkedForDeletion() {
    return markedForDeletion;
  }

  /**
   * Sets the left extent to the given value
   * 
   * @param extent
   *          The value to set the left extent
   */
  public void setLeftExtent(int extent) {
    leftExtent = extent;
  }

  /**
   * Sets the triple to be marked for deletion
   */
  public void setMarkedForDeletion() {
    markedForDeletion = true;
  }

  /**
   * Sets the right extent to the given value
   * 
   * @param extent
   *          The value to set the right extent
   */
  public void setRightExtent(int extent) {
    rightExtent = extent;
  }

  /**
   * Returns a string representation of the triple. This representation will be
   * the value of
   * <p>
   * <code>this.getLeftExtent() + " " + this.getRightExtent() + " " + this.getTokenName()</code>
   * 
   * @return A string representation of the triple
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return leftExtent + " " + rightExtent + " " + tokenName;
  }

}
