package uk.ac.rhul.csle.tooling.io;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import uk.ac.rhul.csle.tooling.lexer.ExtentMode;

/**
 * This class contains various helper functions that simplify the specification
 * in this suite.
 * 
 * @author Robert Michael Walsh
 *
 */
public class SupportFunctions {

  /**
   * A constant storing the value that a nanosecond needs to be multiplied by in
   * order to be converted into seconds.
   */
  public static final double NANOTOSEC = 1.0e-9;

  /**
   * This function calculates the median of an array of numbers.
   * 
   * @param array
   *          An array of numbers
   * @return The median of the given numbers, truncated where necessary.
   */
  public static long calculateMedian(long[] array) {
    Arrays.sort(array);
    int middle = array.length / 2;
    if (array.length % 2 == 1) {
      return array[middle];
    } else {
      return (array[middle] + array[middle - 1]) / 2;
    }
  }

  /**
   * This function defines command-line options common across all programs in
   * this suite.
   * <p>
   * Currently defined commands are
   * <ul>
   * <li><code>-r</code> - If present, tells the program to operate in regular
   * (NFA) mode rather than GLL mode.</li>
   * <li><code>-d</code> <em>output_directory</em> - Tells the program to use
   * <em>output_directory</em> as the output directory.</li>
   * <li><code>-E</code> <em>extent_mode</em> - Tells the program to use
   * <em>extent_mode</em> as the extent mode (can be one of character or unit,
   * defaults at character)
   * </ul>
   * 
   * @return The command line options schema
   */
  public static Options createCommandOptions() {
    Options options = new Options();

    OptionBuilder.withDescription("Regular mode lexer");
    options.addOption(OptionBuilder.create('r'));
    OptionBuilder.withArgName("output_directory");
    OptionBuilder.hasArg();
    OptionBuilder.withDescription("Output to output_directory");
    options.addOption(OptionBuilder.create('d'));
    OptionBuilder.withArgName("extent_mode");
    OptionBuilder.hasArg();
    OptionBuilder.withDescription("Select extent creation mode [default: character]:\tcharacter\tunit");
    options.addOption(OptionBuilder.create('E'));

    return options;
  }

  /**
   * Validates a given set of command line options and returns the resulting
   * command line
   * 
   * @param args
   *          A string representing the command line arguments
   * @return The processed command line
   */
  public static CommandLine processArguments(String[] args) {
    return validateArguments(args, SupportFunctions.createCommandOptions());
  }

  /**
   * Validates a given set of command line options and returns the resulting
   * command line, given a particular command-line option schema
   * 
   * @param args
   *          A string representing the command line arguments
   * @param options
   *          The command-line option schema to use
   * @return The processed command line
   */
  public static CommandLine validateArguments(String[] args, Options options) {
    CommandLine line = null;
    try {
      CommandLineParser parser = new PosixParser();
      line = parser.parse(options, args);
      IOReadWrite.readFile(line.getArgs()[0]).trim();
    } catch (ParseException e) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -Dfile.encoding=UTF-8 -classpath bin:commons-cli-1.2.jar:gll.jar LexTest FILE",
              options);
      return null;
    } catch (IOException | ArrayIndexOutOfBoundsException e) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("Input file not found.\n\n"
              + "java -Dfile.encoding=UTF-8 -classpath bin:commons-cli-1.2.jar:gll.jar LexTest FILE", options);
      return null;
    }
    return line;
  }

  /**
   * Maps a string representation of the extent mode to an
   * <code>ExtentMode</code> choice.
   * 
   * @param mode
   *          The string representation of the extent mode (should be
   *          "character" or "unit")
   * @return <code>ExtentMode.CHARACTER</code> if the input is "character" or
   *         <code>ExtentMode.UNIT</code> if the input is "unit". If the input
   *         is neither of these, then a warning is issued and
   *         <code>ExtentMode.CHARACTER</code> is returned as the default.
   */
  public static ExtentMode getExtentModeMapping(String mode) {
    switch (mode) {
      case "character":
        return ExtentMode.CHARACTER;
      case "unit":
        return ExtentMode.UNIT;
      default:
        System.err.println("WARNING: Invalid extent mode argument supplied. Must be one of: character\tunit\n"
                + "Using character mode");
        return ExtentMode.CHARACTER;
    }
  }
}
