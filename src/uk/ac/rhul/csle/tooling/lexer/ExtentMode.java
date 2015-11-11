package uk.ac.rhul.csle.tooling.lexer;

/**
 * An enum that categorise the modes for calculating the extents.
 * 
 * CHARACTER - means that the extents should match the character indices in the
 * original string UNIT - means that the extents so that the right extent of a
 * triple is always just one more than the left extent
 * 
 * @author Robert Michael Walsh
 *
 */
public enum ExtentMode {
  CHARACTER, UNIT
}
