package uk.ac.rhul.csle.tooling.FortranSubLexer;

import uk.ac.rhul.csle.tooling.io.IOReadWrite;
import uk.ac.rhul.csle.tooling.lexer.RegularLexer;
import uk.ac.rhul.csle.tooling.parsing.InvalidParseException;

/**
 * This class tests the ambiguity surrounding DO5AB=1,6 in Fortran and output
 * the dot representation of lexical analysis
 * 
 * @author Robert Michael Walsh
 *
 */
public class TestFortranDoAmbiguity {

  public static void main(String[] args) {
    RegularLexer lex = new RegularLexer(new FortranSubDFAMap());

    try {
      lex.lex(null, "DO5AB=1,6");
      IOReadWrite.writeFile("output.dot", lex.toDot(lex.getTriples()));
    } catch (InvalidParseException e) {
      e.printStackTrace();
    }

  }

}
