package uk.ac.rhul.csle.tooling.CSLexer.initialProcessor;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import uk.ac.rhul.csle.tooling.io.IOReadWrite;
import uk.ac.rhul.csle.tooling.io.SupportFunctions;
import uk.ac.rhul.csle.tooling.lexer.DFAMap;

/**
 * This program takes any C# string and processes it to replace all whitespace
 * and comments with a single new-line character.
 * <p>
 * The output of this program is a reformatted C# string consisting of only
 * sequences of characters that are non-layout, delimited by new-line
 * characters.
 * 
 * @author Robert Michael Walsh
 *
 */
public class InitialProcessor {

  public static void main(String[] args) {
    String input = "";
    String output_directory = "output";
    final Options options = SupportFunctions.createCommandOptions();
    CommandLine line = null;
    try {
      final CommandLineParser parser = new PosixParser();
      line = parser.parse(options, args);
      input = IOReadWrite.readFile(line.getArgs()[0]);
      if (line.hasOption("d")) {
        output_directory = line.getOptionValue("d");
      }
    } catch (final ParseException e) {
      final HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -Dfile.encoding=UTF-8 -classpath bin:commons-cli-1.2.jar:gll.jar LexTest FILE",
              options);
      return;
    } catch (final IOException e) {
      final HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("Input file not found.\n\n"
              + "java -Dfile.encoding=UTF-8 -classpath bin:commons-cli-1.2.jar:gll.jar LexTest FILE", options);
    }
    // **CSHARP INITIAL PROCESSOR**//
    final DFAMap initialProcessor = new CSProcessorMap();
    int i = 0;
    final StringBuilder newString = new StringBuilder("");
    // We only care about identifying the layout tokens so we can use a
    // traditional longest-match lexer.
    while (i < input.length()) {
      boolean whitespaceCaptured = false;
      final int startIndex = i;
      int longestEndIndex = i + 1;
      for (final String token : initialProcessor.getTokens()) {
        int endIndex = i;
        while (i < input.length() && initialProcessor.applyTransition(token, input.charAt(i))) {
          i++;
          if (initialProcessor.atAcceptingState(token)) {
            endIndex = i;
            // We might be looking at a character or string literal instead in
            // which case we simply want to copy it over
            if (initialProcessor.isLayout(token)) {
              whitespaceCaptured = true;
            }
          }
        }
        initialProcessor.reset(token);
        if (endIndex > longestEndIndex) {
          longestEndIndex = endIndex;
        }
        i = startIndex;
      }
      // If this portion of the input string was not captured by a layout token
      // then we simply copy it over. Note that if the string is also not
      // matched by string and character literal, then we simply copy a single
      // character over.
      if (!whitespaceCaptured) {
        newString.append(input.substring(startIndex, longestEndIndex));
      } else {
        newString.append('\n');
      }
      i = longestEndIndex;
    }
    IOReadWrite.writeFile(output_directory + "/" + line.getArgs()[0].substring(line.getArgs()[0].lastIndexOf('/') + 1),
            newString.toString().replaceAll("\n+", "\n").trim());
  }
}
