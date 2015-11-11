package uk.ac.rhul.csle.tooling.parsing;

/**
 * This exception indicates that an attempt to analyse a string failed.
 * 
 * @author Robert Michael Walsh
 *
 */
public class InvalidParseException extends Exception {

  /*
   * Admittedly just included to get rid of the warning, I don't actually know
   * what the point of this is..
   */
  private static final long serialVersionUID = -9149170064163553683L;

  public InvalidParseException() {
  }

}
