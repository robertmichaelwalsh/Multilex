package uk.ac.rhul.csle.tooling.CSLexer.Measures;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Stack;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import uk.ac.rhul.csle.gll.GLLHashPool;
import uk.ac.rhul.csle.gll.GLLSupport;
import uk.ac.rhul.csle.text.DefaultTextHandler;
import uk.ac.rhul.csle.text.Text;
import uk.ac.rhul.csle.text.TextLevel;
import uk.ac.rhul.csle.tooling.CSLexer.CS2DFAMap;
import uk.ac.rhul.csle.tooling.CSLexer.CSLexerParser;
import uk.ac.rhul.csle.tooling.io.IOReadWrite;
import uk.ac.rhul.csle.tooling.io.SupportFunctions;
import uk.ac.rhul.csle.tooling.lexer.GLLLexer;
import uk.ac.rhul.csle.tooling.lexer.MultiLexer;
import uk.ac.rhul.csle.tooling.lexer.RegularLexer;
import uk.ac.rhul.csle.tooling.parsing.CSBuiltinParserAdditionalBuiltins;
import uk.ac.rhul.csle.tooling.parsing.CSParser2;
import uk.ac.rhul.csle.tooling.parsing.CSScannerlessParser;
import uk.ac.rhul.csle.tooling.parsing.InvalidParseException;

/**
 * This class contains all the measurement tests that are used for the C# case
 * study
 *
 * @author Robert Michael Walsh
 *
 */
public class CSMeasurementTests {

  private enum LexStyle {
    BUILTIN, CHARACTER, MULTILEX
  };

  /**
   * Counts the number of edges reachable from the root in the given ESPPF
   *
   * @param parser
   *          The parser for which the ESPPF is constructed
   * @return The number of reachable edges
   */
  public static int countEdges(GLLHashPool parser) {
    final Stack<Integer> stack = new Stack<Integer>();
    stack.push(parser.sppfRoot());
    int count = 0;
    while (!stack.isEmpty()) {
      final int currentElement = stack.pop();
      if (!parser.sppfNodeVisited(currentElement)) {
        parser.sppfNodeSetVisited(currentElement);
        for (int tmp = parser.sppfNodePackNodeList(currentElement); tmp != 0; tmp =
                parser.sppfPackNodePackNodeList(tmp)) {
          ++count;
          final int rightChild = parser.sppfPackNodeRightChild(tmp);
          if (rightChild != 0) {
            ++count;
            stack.push(rightChild);
          }
          final int leftChild = parser.sppfPackNodeLeftChild(tmp);
          if (leftChild != 0) {
            ++count;
            stack.push(leftChild);
          }
        }

      }
    }

    parser.sppfResetVisitedFlags();
    return count;
  }

  /**
   * Counts the number of edges in the ESPPF in total
   *
   * @param parser
   *          The parser for which the ESPPF is constructed
   * @return The number of ESPPF edges
   */
  public static int countEdgesFull(GLLHashPool parser) {
    final Stack<Integer> stack = new Stack<Integer>();
    for (int element = parser.sppfNodeFirst(); element != 0; element = parser.sppfNodeNext()) {
      stack.push(element);
    }
    stack.push(parser.sppfRoot());
    int count = 0;
    while (!stack.isEmpty()) {
      final int currentElement = stack.pop();
      if (!parser.sppfNodeVisited(currentElement)) {
        parser.sppfNodeSetVisited(currentElement);
        for (int tmp = parser.sppfNodePackNodeList(currentElement); tmp != 0; tmp =
                parser.sppfPackNodePackNodeList(tmp)) {
          ++count;
          final int rightChild = parser.sppfPackNodeRightChild(tmp);
          if (rightChild != 0) {
            ++count;
            stack.push(rightChild);
          }
          final int leftChild = parser.sppfPackNodeLeftChild(tmp);
          if (leftChild != 0) {
            ++count;
            stack.push(leftChild);
          }
        }

      }
    }

    parser.sppfResetVisitedFlags();
    return count;
  }

  /**
   * Counts the number of nodes reachable from the root in the given ESPPF
   *
   * @param parser
   *          The parser for which the ESPPF is constructed
   * @return The number of reachable nodes
   */
  public static int countNodes(GLLHashPool parser) {
    final Stack<Integer> stack = new Stack<Integer>();
    stack.push(parser.sppfRoot());
    int count = 0;
    while (!stack.isEmpty()) {
      final int currentElement = stack.pop();
      if (!parser.sppfNodeVisited(currentElement)) {
        ++count;
        parser.sppfNodeSetVisited(currentElement);
        for (int tmp = parser.sppfNodePackNodeList(currentElement); tmp != 0; tmp =
                parser.sppfPackNodePackNodeList(tmp)) {
          ++count;
          final int rightChild = parser.sppfPackNodeRightChild(tmp);
          if (rightChild != 0) {
            stack.push(rightChild);
          }
          final int leftChild = parser.sppfPackNodeLeftChild(tmp);
          if (leftChild != 0) {
            stack.push(leftChild);
          }
        }

      }
    }

    parser.sppfResetVisitedFlags();
    return count;
  }

  /**
   * Counts the number of nodes in the ESPPF in total
   *
   * @param parser
   *          The parser for which the ESPPF is constructed
   * @return The number of ESPPF nodes
   */
  public static int countNodesFull(GLLHashPool parser) {
    final Stack<Integer> stack = new Stack<Integer>();
    for (int element = parser.sppfNodeFirst(); element != 0; element = parser.sppfNodeNext()) {
      stack.push(element);
    }
    stack.push(parser.sppfRoot());
    int count = 0;
    while (!stack.isEmpty()) {
      final int currentElement = stack.pop();
      if (!parser.sppfNodeVisited(currentElement)) {
        ++count;
        parser.sppfNodeSetVisited(currentElement);
        for (int tmp = parser.sppfNodePackNodeList(currentElement); tmp != 0; tmp =
                parser.sppfPackNodePackNodeList(tmp)) {
          ++count;
          final int rightChild = parser.sppfPackNodeRightChild(tmp);
          if (rightChild != 0) {
            stack.push(rightChild);
          }
          final int leftChild = parser.sppfPackNodeLeftChild(tmp);
          if (leftChild != 0) {
            stack.push(leftChild);
          }
        }

      }
    }

    parser.sppfResetVisitedFlags();
    return count;
  }

  /**
   * Counts the number of edges reachable from the root in the given ESPPF which
   * are not the children of a token non-terminal
   *
   * @param parser
   *          The parser for which the ESPPF is constructed
   * @return The number of reachable edges
   */
  public static int countNonTokenEdges(GLLHashPool parser) {
    parser.sppfResetVisitedFlags();
    final Stack<Integer> stack = new Stack<Integer>();
    int count = 0;
    stack.push(parser.sppfRoot());
    while (!stack.isEmpty()) {
      final int currentElement = stack.pop();
      if (!parser.sppfNodeVisited(currentElement)) {
        if (!(isAllUpperCase(parser.labelToString(parser.sppfNodeLabel(currentElement)))
                && parser.getLabelKind(parser.sppfNodeLabel(currentElement)) == GLLSupport.ART_K_NONTERMINAL)) {
          parser.sppfNodeSetVisited(currentElement);
          for (int tmp = parser.sppfNodePackNodeList(currentElement); tmp != 0; tmp =
                  parser.sppfPackNodePackNodeList(tmp)) {
            ++count;
            final int rightChild = parser.sppfPackNodeRightChild(tmp);
            if (rightChild != 0) {
              ++count;
              stack.push(rightChild);
            }
            final int leftChild = parser.sppfPackNodeLeftChild(tmp);
            if (leftChild != 0) {
              ++count;
              stack.push(leftChild);
            }
          }
        } else {

          markSubtree(parser, currentElement);
        }
      }
    }

    parser.sppfResetVisitedFlags();
    return count;
  }

  /**
   * Counts the number of edges in the ESPPF in total which are not the children
   * of a token non-terminal
   *
   * @param parser
   *          The parser for which the ESPPF is constructed
   * @return The number of ESPPF edges
   */
  public static int countNonTokenEdgesFull(GLLHashPool parser) {
    parser.sppfResetVisitedFlags();
    final Stack<Integer> stack = new Stack<Integer>();
    int count = 0;
    for (int element = parser.sppfNodeFirst(); element != 0; element = parser.sppfNodeNext()) {
      if (isAllUpperCase(parser.labelToString(parser.sppfNodeLabel(element)))
              && parser.getLabelKind(parser.sppfNodeLabel(element)) == GLLSupport.ART_K_NONTERMINAL) {
        stack.push(element);
      }
    }
    while (!stack.isEmpty()) {
      final int currentElement = stack.pop();
      if (!parser.sppfNodeVisited(currentElement)) {
        markSubtree(parser, currentElement);
      }
    }
    for (int element = parser.sppfNodeFirst(); element != 0; element = parser.sppfNodeNext()) {
      stack.push(element);
    }
    stack.push(parser.sppfRoot());
    while (!stack.isEmpty()) {
      final int currentElement = stack.pop();
      if (!parser.sppfNodeVisited(currentElement)) {
        if (!(isAllUpperCase(parser.labelToString(parser.sppfNodeLabel(currentElement)))
                && parser.getLabelKind(parser.sppfNodeLabel(currentElement)) == GLLSupport.ART_K_NONTERMINAL)) {
          parser.sppfNodeSetVisited(currentElement);
          for (int tmp = parser.sppfNodePackNodeList(currentElement); tmp != 0; tmp =
                  parser.sppfPackNodePackNodeList(tmp)) {
            count++;
            final int rightChild = parser.sppfPackNodeRightChild(tmp);
            if (rightChild != 0) {
              ++count;
              stack.push(rightChild);
            }
            final int leftChild = parser.sppfPackNodeLeftChild(tmp);
            if (leftChild != 0) {
              ++count;
              stack.push(leftChild);
            }
          }
        } else {

          markSubtree(parser, currentElement);
        }
      }
    }

    parser.sppfResetVisitedFlags();
    return count;
  }

  /**
   * Counts the number of nodes reachable from the root in the given ESPPF which
   * are not the children of a token non-terminal
   *
   * @param parser
   *          The parser for which the ESPPF is constructed
   * @return The number of reachable nodes
   */
  public static int countNonTokenNodes(GLLHashPool parser) {
    parser.sppfResetVisitedFlags();
    final Stack<Integer> stack = new Stack<Integer>();
    int count = 0;
    stack.push(parser.sppfRoot());
    while (!stack.isEmpty()) {
      final int currentElement = stack.pop();
      if (!parser.sppfNodeVisited(currentElement)) {
        count++;
        if (!(isAllUpperCase(parser.labelToString(parser.sppfNodeLabel(currentElement)))
                && parser.getLabelKind(parser.sppfNodeLabel(currentElement)) == GLLSupport.ART_K_NONTERMINAL)) {
          parser.sppfNodeSetVisited(currentElement);
          for (int tmp = parser.sppfNodePackNodeList(currentElement); tmp != 0; tmp =
                  parser.sppfPackNodePackNodeList(tmp)) {
            count++;
            final int rightChild = parser.sppfPackNodeRightChild(tmp);
            if (rightChild != 0) {
              stack.push(rightChild);
            }
            final int leftChild = parser.sppfPackNodeLeftChild(tmp);
            if (leftChild != 0) {
              stack.push(leftChild);
            }
          }
        } else {

          markSubtree(parser, currentElement);
        }
      }
    }

    parser.sppfResetVisitedFlags();
    return count;
  }

  /**
   * Counts the number of nodes in the ESPPF in total which are not the children
   * of token non-terminals
   *
   * @param parser
   *          The parser for which the ESPPF is constructed
   * @return The number of ESPPF nodes
   */
  public static int countNonTokenNodesFull(GLLHashPool parser) {
    parser.sppfResetVisitedFlags();
    final Stack<Integer> stack = new Stack<Integer>();
    int count = 0;
    for (int element = parser.sppfNodeFirst(); element != 0; element = parser.sppfNodeNext()) {
      if (isAllUpperCase(parser.labelToString(parser.sppfNodeLabel(element)))
              && parser.getLabelKind(parser.sppfNodeLabel(element)) == GLLSupport.ART_K_NONTERMINAL) {
        stack.push(element);
      }
    }
    while (!stack.isEmpty()) {
      final int currentElement = stack.pop();
      if (!parser.sppfNodeVisited(currentElement)) {
        count++;
        markSubtree(parser, currentElement);
      }
    }
    for (int element = parser.sppfNodeFirst(); element != 0; element = parser.sppfNodeNext()) {
      stack.push(element);
    }
    stack.push(parser.sppfRoot());
    while (!stack.isEmpty()) {
      final int currentElement = stack.pop();
      if (!parser.sppfNodeVisited(currentElement)) {
        count++;
        if (!(isAllUpperCase(parser.labelToString(parser.sppfNodeLabel(currentElement)))
                && parser.getLabelKind(parser.sppfNodeLabel(currentElement)) == GLLSupport.ART_K_NONTERMINAL)) {
          parser.sppfNodeSetVisited(currentElement);
          for (int tmp = parser.sppfNodePackNodeList(currentElement); tmp != 0; tmp =
                  parser.sppfPackNodePackNodeList(tmp)) {
            count++;
            final int rightChild = parser.sppfPackNodeRightChild(tmp);
            if (rightChild != 0) {
              stack.push(rightChild);
            }
            final int leftChild = parser.sppfPackNodeLeftChild(tmp);
            if (leftChild != 0) {
              stack.push(leftChild);
            }
          }
        } else {

          markSubtree(parser, currentElement);
        }
      }
    }

    parser.sppfResetVisitedFlags();
    return count;
  }

  /**
   * A function that initialises all lexical priority rules
   *
   * @param lex
   *          The lexer to initialise the priority rules for
   */
  public static void equalPriorityMatchesCS(MultiLexer lex) {
    lex.addRestrictedPriorityGrouping("identifier", "abstract");
    lex.addRestrictedPriorityGrouping("identifier", "as");
    lex.addRestrictedPriorityGrouping("identifier", "base");
    lex.addRestrictedPriorityGrouping("identifier", "bool");
    lex.addRestrictedPriorityGrouping("identifier", "break");
    lex.addRestrictedPriorityGrouping("identifier", "byte");
    lex.addRestrictedPriorityGrouping("identifier", "case");
    lex.addRestrictedPriorityGrouping("identifier", "catch");
    lex.addRestrictedPriorityGrouping("identifier", "char");
    lex.addRestrictedPriorityGrouping("identifier", "checked");
    lex.addRestrictedPriorityGrouping("identifier", "class");
    lex.addRestrictedPriorityGrouping("identifier", "const");
    lex.addRestrictedPriorityGrouping("identifier", "continue");
    lex.addRestrictedPriorityGrouping("identifier", "decimal");
    lex.addRestrictedPriorityGrouping("identifier", "default");
    lex.addRestrictedPriorityGrouping("identifier", "delegate");
    lex.addRestrictedPriorityGrouping("identifier", "do");
    lex.addRestrictedPriorityGrouping("identifier", "double");
    lex.addRestrictedPriorityGrouping("identifier", "else");
    lex.addRestrictedPriorityGrouping("identifier", "enum");
    lex.addRestrictedPriorityGrouping("identifier", "event");
    lex.addRestrictedPriorityGrouping("identifier", "explicit");
    lex.addRestrictedPriorityGrouping("identifier", "extern");
    lex.addRestrictedPriorityGrouping("identifier", "false");
    lex.addRestrictedPriorityGrouping("identifier", "finally");
    lex.addRestrictedPriorityGrouping("identifier", "fixed");
    lex.addRestrictedPriorityGrouping("identifier", "float");
    lex.addRestrictedPriorityGrouping("identifier", "for");
    lex.addRestrictedPriorityGrouping("identifier", "foreach");
    lex.addRestrictedPriorityGrouping("identifier", "goto");
    lex.addRestrictedPriorityGrouping("identifier", "if");
    lex.addRestrictedPriorityGrouping("identifier", "implicit");
    lex.addRestrictedPriorityGrouping("identifier", "in");
    lex.addRestrictedPriorityGrouping("identifier", "int");
    lex.addRestrictedPriorityGrouping("identifier", "interface");
    lex.addRestrictedPriorityGrouping("identifier", "internal");
    lex.addRestrictedPriorityGrouping("identifier", "is");
    lex.addRestrictedPriorityGrouping("identifier", "lock");
    lex.addRestrictedPriorityGrouping("identifier", "long");
    lex.addRestrictedPriorityGrouping("identifier", "namespace");
    lex.addRestrictedPriorityGrouping("identifier", "new");
    lex.addRestrictedPriorityGrouping("identifier", "object");
    lex.addRestrictedPriorityGrouping("identifier", "operator");
    lex.addRestrictedPriorityGrouping("identifier", "out");
    lex.addRestrictedPriorityGrouping("identifier", "override");
    lex.addRestrictedPriorityGrouping("identifier", "params");
    lex.addRestrictedPriorityGrouping("identifier", "private");
    lex.addRestrictedPriorityGrouping("identifier", "protected");
    lex.addRestrictedPriorityGrouping("identifier", "public");
    lex.addRestrictedPriorityGrouping("identifier", "readonly");
    lex.addRestrictedPriorityGrouping("identifier", "ref");
    lex.addRestrictedPriorityGrouping("identifier", "return");
    lex.addRestrictedPriorityGrouping("identifier", "sbyte");
    lex.addRestrictedPriorityGrouping("identifier", "sealed");
    lex.addRestrictedPriorityGrouping("identifier", "short");
    lex.addRestrictedPriorityGrouping("identifier", "stackalloc");
    lex.addRestrictedPriorityGrouping("identifier", "sizeof");
    lex.addRestrictedPriorityGrouping("identifier", "static");
    lex.addRestrictedPriorityGrouping("identifier", "string");
    lex.addRestrictedPriorityGrouping("identifier", "struct");
    lex.addRestrictedPriorityGrouping("identifier", "switch");
    lex.addRestrictedPriorityGrouping("identifier", "this");
    lex.addRestrictedPriorityGrouping("identifier", "throw");
    lex.addRestrictedPriorityGrouping("identifier", "true");
    lex.addRestrictedPriorityGrouping("identifier", "try");
    lex.addRestrictedPriorityGrouping("identifier", "typeof");
    lex.addRestrictedPriorityGrouping("identifier", "uint");
    lex.addRestrictedPriorityGrouping("identifier", "ulong");
    lex.addRestrictedPriorityGrouping("identifier", "unchecked");
    lex.addRestrictedPriorityGrouping("identifier", "unsafe");
    lex.addRestrictedPriorityGrouping("identifier", "ushort");
    lex.addRestrictedPriorityGrouping("identifier", "using");
    lex.addRestrictedPriorityGrouping("identifier", "virtual");
    lex.addRestrictedPriorityGrouping("identifier", "void");
    lex.addRestrictedPriorityGrouping("identifier", "volatile");
    lex.addRestrictedPriorityGrouping("identifier", "while");
    lex.addRestrictedPriorityGrouping("identifier", "null");
  }

  /**
   * Checks whether the given string is in all uppercase.
   * 
   * @param s
   *          The string to check
   * @return true if the string is all uppercase, false otherwise
   */
  private static boolean isAllUpperCase(String s) {
    for (final char c : s.toCharArray()) {
      if (!(Character.isUpperCase(c) || c == '_')) {
        return false;
      }
    }
    return true;
  }

  /**
   * A function that initialises all lexical longest match rules
   *
   * @param lex
   *          The lexer to initialise the longest match rules for
   */
  public static void longestMatchesCS(MultiLexer lex) {
    lex.addLeftLongestGrouping("/", "comment", true);
    lex.addLeftLongestGrouping("whitespace");
    lex.addLeftLongestGrouping("new_line");

    lex.addLeftLongestGrouping("string_literal");

    lex.addLeftLongestGrouping("integer_literal", "real_literal", true);
    lex.addLeftLongestGrouping(".", "real_literal", true);
    lex.addLeftLongestGrouping("in", "int", "internal", "interface", "identifier");
    lex.addLeftLongestGrouping("do", "double", "identifier");
    lex.addLeftLongestGrouping("for", "foreach", "identifier");
    lex.addLeftLongestGrouping("identifier");
    lex.addLeftLongestGrouping("abstract", "identifier", true);
    lex.addLeftLongestGrouping("as", "identifier", true);
    lex.addLeftLongestGrouping("base", "identifier", true);
    lex.addLeftLongestGrouping("bool", "identifier", true);
    lex.addLeftLongestGrouping("break", "identifier", true);
    lex.addLeftLongestGrouping("byte", "identifier", true);
    lex.addLeftLongestGrouping("case", "identifier", true);
    lex.addLeftLongestGrouping("catch", "identifier", true);
    lex.addLeftLongestGrouping("char", "identifier", true);
    lex.addLeftLongestGrouping("checked", "identifier", true);
    lex.addLeftLongestGrouping("class", "identifier", true);
    lex.addLeftLongestGrouping("const", "identifier", true);
    lex.addLeftLongestGrouping("continue", "identifier", true);
    lex.addLeftLongestGrouping("decimal", "identifier", true);
    lex.addLeftLongestGrouping("delegate", "identifier", true);
    lex.addLeftLongestGrouping("default", "identifier", true);
    lex.addLeftLongestGrouping("else", "identifier", true);
    lex.addLeftLongestGrouping("enum", "identifier", true);
    lex.addLeftLongestGrouping("event", "identifier", true);
    lex.addLeftLongestGrouping("explicit", "identifier", true);
    lex.addLeftLongestGrouping("false", "identifier", true);
    lex.addLeftLongestGrouping("finally", "identifier", true);
    lex.addLeftLongestGrouping("fixed", "identifier", true);
    lex.addLeftLongestGrouping("float", "identifier", true);
    lex.addLeftLongestGrouping("extern", "identifier", true);
    lex.addLeftLongestGrouping("goto", "identifier", true);
    lex.addLeftLongestGrouping("if", "identifier", true);
    lex.addLeftLongestGrouping("implicit", "identifier", true);
    lex.addLeftLongestGrouping("is", "identifier", true);
    lex.addLeftLongestGrouping("lock", "identifier", true);
    lex.addLeftLongestGrouping("long", "identifier", true);
    lex.addLeftLongestGrouping("namespace", "identifier", true);
    lex.addLeftLongestGrouping("new", "identifier", true);
    lex.addLeftLongestGrouping("null", "identifier", true);
    lex.addLeftLongestGrouping("object", "identifier", true);
    lex.addLeftLongestGrouping("operator", "identifier", true);
    lex.addLeftLongestGrouping("out", "identifier", true);
    lex.addLeftLongestGrouping("override", "identifier", true);
    lex.addLeftLongestGrouping("params", "identifier", true);
    lex.addLeftLongestGrouping("private", "identifier", true);
    lex.addLeftLongestGrouping("protected", "identifier", true);
    lex.addLeftLongestGrouping("return", "identifier", true);
    lex.addLeftLongestGrouping("ref", "identifier", true);
    lex.addLeftLongestGrouping("sbyte", "identifier", true);
    lex.addLeftLongestGrouping("sealed", "identifier", true);
    lex.addLeftLongestGrouping("short", "identifier", true);
    lex.addLeftLongestGrouping("stackalloc", "identifier", true);
    lex.addLeftLongestGrouping("sizeof", "identifier", true);
    lex.addLeftLongestGrouping("static", "identifier", true);
    lex.addLeftLongestGrouping("string", "identifier", true);
    lex.addLeftLongestGrouping("struct", "identifier", true);
    lex.addLeftLongestGrouping("switch", "identifier", true);
    lex.addLeftLongestGrouping("this", "identifier", true);
    lex.addLeftLongestGrouping("throw", "identifier", true);
    lex.addLeftLongestGrouping("true", "identifier", true);
    lex.addLeftLongestGrouping("try", "identifier", true);
    lex.addLeftLongestGrouping("typeof", "identifier", true);
    lex.addLeftLongestGrouping("uint", "identifier", true);
    lex.addLeftLongestGrouping("ulong", "identifier", true);
    lex.addLeftLongestGrouping("unchecked", "identifier", true);
    lex.addLeftLongestGrouping("unsafe", "identifier", true);
    lex.addLeftLongestGrouping("ushort", "identifier", true);
    lex.addLeftLongestGrouping("using", "identifier", true);
    lex.addLeftLongestGrouping("virtual", "identifier", true);
    lex.addLeftLongestGrouping("void", "identifier", true);
    lex.addLeftLongestGrouping("volatile", "identifier", true);
    lex.addLeftLongestGrouping("while", "identifier", true);

    lex.addLeftLongestGrouping("yield", "identifier", true);
    lex.addLeftLongestGrouping("alias", "identifier", true);
    lex.addLeftLongestGrouping("partial", "identifier", true);
    lex.addLeftLongestGrouping("get", "identifier", true);
    lex.addLeftLongestGrouping("set", "identifier", true);
    lex.addLeftLongestGrouping("add", "identifier", true);
    lex.addLeftLongestGrouping("remove", "identifier", true);
    lex.addLeftLongestGrouping("where", "identifier", true);

    lex.addLeftLongestGrouping("=", "==");
    lex.addLeftLongestGrouping("!", "!=");
    lex.addLeftLongestGrouping("<", "<=", "<<", "<<=");

    // Must be separated due to the ambiguity with generics
    lex.addLeftLongestGrouping(">", ">=");
    lex.addLeftLongestGrouping(">>", ">>=");

    lex.addLeftLongestGrouping("+", "++", "+=");
    lex.addLeftLongestGrouping("-", "--", "-=");
    lex.addLeftLongestGrouping("*", "*=");
    lex.addLeftLongestGrouping("/", "/=");
    lex.addLeftLongestGrouping("%", "%=");
    lex.addLeftLongestGrouping("|", "||", "|=");
    lex.addLeftLongestGrouping("^", "^=");
    lex.addLeftLongestGrouping("&", "&&", "&=");
    lex.addLeftLongestGrouping("?", "??");
    lex.addLeftLongestGrouping(":", "::");
  }

  public static void main(String[] args) {
    final Options options = SupportFunctions.createCommandOptions();
    OptionBuilder.withArgName("style");
    OptionBuilder.hasArg();
    OptionBuilder.withDescription("Select lex style [builtin,character,multilex] (default:multilex)");
    options.addOption(OptionBuilder.create('S'));
    OptionBuilder.withDescription("Enable Longest Match");
    options.addOption(OptionBuilder.create('L'));
    OptionBuilder.withDescription("Enable Priority");
    options.addOption(OptionBuilder.create('P'));
    OptionBuilder.withArgName("number_of_runs");
    OptionBuilder.hasArg();
    OptionBuilder.withDescription("Specifies the number of runs for the tests [default:10]");
    options.addOption(OptionBuilder.create('N'));

    final CommandLine line = SupportFunctions.validateArguments(args, options);
    if (line == null) {
      System.err.println("ERROR: Invalid arguments supplied.");
      return;
    }
    String input = "";
    try {
      input = IOReadWrite.readFile(line.getArgs()[0]).trim();
    } catch (final IOException e1) {
      // Shouldn't happen as the program checked earlier
      e1.printStackTrace();
    }

    // In my script I set CompileThreshold to 10, so I use 11 to be sure
    int warmup = 0;

    final short noOfRuns = line.hasOption('N') ? Short.parseShort(line.getOptionValue('N')) : 1;

    LexStyle style = LexStyle.MULTILEX;
    if (line.hasOption('S')) {
      switch (line.getOptionValue('S')) {
        case "builtin":
          style = LexStyle.BUILTIN;
          break;
        case "character":
          style = LexStyle.CHARACTER;
          break;
        case "multilex":
          style = LexStyle.MULTILEX;
          break;
        default:
          System.err.println("WARNING: Invalid lex style provided, using multilex style.");
      }
    }
    final boolean longestMatch = line.hasOption('L');
    final boolean priority = line.hasOption('P');

    if (style == LexStyle.MULTILEX) {
      MultiLexer lex;
      if (line.hasOption('r')) {
        if (line.hasOption('E')) {
          lex = new RegularLexer(new CS2DFAMap(), SupportFunctions.getExtentModeMapping(line.getOptionValue('E')));
        } else {
          lex = new RegularLexer(new CS2DFAMap());
        }
      } else {
        final String[] tokens = { "whitespace", "comment", "identifier", "abstract", "as", "base", "bool", "break",
                "byte", "case", "catch", "char", "checked", "class", "const", "continue", "decimal", "default",
                "delegate", "do", "double", "else", "enum", "event", "explicit", "extern", "false", "finally", "fixed",
                "float", "for", "foreach", "goto", "if", "implicit", "in", "int", "interface", "internal", "is", "lock",
                "long", "namespace", "new", "object", "operator", "out", "override", "params", "private", "protected",
                "public", "readonly", "ref", "return", "sbyte", "sealed", "short", "stackalloc", "static", "string",
                "struct", "switch", "this", "throw", "true", "try", "typeof", "uint", "ulong", "unchecked", "unsafe",
                "ushort", "using", "virtual", "void", "volatile", "while", "method", "integer_literal", "real_literal",
                "character_literal", "string_literal", "dot", "comma", "lparen", "rparen", "lbracket", "rbracket",
                "increment", "decrement", "new_line", "boolean_literal", "add_op", "sub_op", "not_op", "mul_op",
                "div_op", "mod_op", "or_op", "lshift_op", "rshift_op", "lessthan_op", "greaterthan_op", "lessthaneq_op",
                "greaterthaneq_op", "equality_op", "nequality_op", "and_op", "xor_op", "condand_op", "condor_op",
                "question", "colon", "semicolon", "dirassign", "addassign", "subassign", "mulassign", "divassign",
                "modassign", "andassign", "orassign", "xorassign", "lshiftassign", "rshiftassign", "lbrace", "rbrace",
                "tilde", "assembly", "module", "field", "param", "property", "type", "add", "remove", "get", "set",
                "null_literal" };
        final String[] layoutTokens = { "comment", "whitespace", "new_line" };
        if (line.hasOption('E')) {
          lex = new GLLLexer(new CSLexerParser(), tokens, layoutTokens,
                  SupportFunctions.getExtentModeMapping(line.getOptionValue('E')));
        } else {
          lex = new GLLLexer(new CSLexerParser(), tokens, layoutTokens);
        }
      }

      if (longestMatch) {
        longestMatchesCS(lex);
      }

      if (priority) {
        equalPriorityMatchesCS(lex);
      }

      try {
        final long[] lexTimes = new long[noOfRuns];
        final long[] parseTimes1 = new long[noOfRuns];
        final long[] totalTimes = new long[noOfRuns];
        long averageLexTime = 0;
        long averageParseTime1 = 0;
        long averageTotalTime = 0;

        final String unqualifiedFilename =
                line.getArgs()[0].substring(line.getArgs()[0].lastIndexOf('/') + 1, line.getArgs()[0].indexOf("."));

        GLLHashPool parserReference1 = null;
        int tripleSetSize = 0;
        BigInteger noOfTokenisations = null;

        for (int i = 0; i < (warmup + noOfRuns); ++i) {
          // Run the lexer
          long lexTime = System.nanoTime();
          lex.lexSegmented(unqualifiedFilename, input);
          final String tok = lex.toString();
          lexTime = System.nanoTime() - lexTime;
          // Run the parser, re-initialising each time
          final GLLHashPool parser = new CSParser2();
          long parseTime = System.nanoTime();
          parser.parse(tok);

          parseTime = System.nanoTime() - parseTime;
          if (!parser.getInLanguage()) {
            new Text(new DefaultTextHandler()).printf(TextLevel.ERROR, "String rejected by MGLL Parser");
          }
          parserReference1 = parser;
          // On the first run (as the value stays constant between runs), get
          // the
          // size of the triple set and the number of tokenisations
          if (i == 0) {
            tripleSetSize = lex.getDisambiguated().size();
            noOfTokenisations = lex.getTokenisationCount(lex.getDisambiguated());
          }
          // Reset the lexer
          lex.resetLexer();
          if (i >= warmup) {
            lexTimes[i - warmup] = lexTime;
            parseTimes1[i - warmup] = parseTime;
            totalTimes[i - warmup] = lexTime + parseTime;
          }
          System.gc();
        }
        parserReference1.computeStatistics();
        averageLexTime = SupportFunctions.calculateMedian(lexTimes);
        averageParseTime1 = SupportFunctions.calculateMedian(parseTimes1);
        averageTotalTime = SupportFunctions.calculateMedian(totalTimes);

        // ,no. Of Triples, no. Of tokenisations, Average lex time (seconds),
        // Average parse time (seconds), Average total time
        // (seconds), No. of reachable ESPPF nodes, No. of total ESPPF nodes,
        // No. of reachable ESPPF edges, No. of total ESPPF edges,
        // No. of ambiguous nodes, No. of GSS Nodes, No. of GSS Edges, No. of
        // Descriptors
        System.out.print("," + tripleSetSize + "," + noOfTokenisations + ","
                + averageLexTime * SupportFunctions.NANOTOSEC + ',' + averageParseTime1 * SupportFunctions.NANOTOSEC
                + ',' + averageTotalTime * SupportFunctions.NANOTOSEC + ',' + countNodes(parserReference1) + ','
                + countNodesFull(parserReference1) + ',' + countEdges(parserReference1) + ','
                + countEdgesFull(parserReference1) + ',' + parserReference1.sppfAmbiguityNodes + ','
                + parserReference1.gssNodeHistogram.weightedSumBuckets() + ','
                + parserReference1.gssEdgeHistogram.weightedSumBuckets() + ','
                + parserReference1.descriptorHistogram.weightedSumBuckets());

      } catch (final InvalidParseException e) {
        e.printStackTrace();
      }
    } else {
      final long[] parseTimes = new long[noOfRuns];
      long averageParseTime = 0;
      GLLHashPool parserReference = null;
      for (int i = 0; i < (warmup + noOfRuns); ++i) {
        final GLLHashPool parser =
                style == LexStyle.BUILTIN ? new CSBuiltinParserAdditionalBuiltins() : new CSScannerlessParser();
        long parseTime = System.nanoTime();
        parser.parse(input);
        parseTime = System.nanoTime() - parseTime;
        parserReference = parser;
        if (i >= warmup) {
          parseTimes[i - warmup] = parseTime;
        }
        System.gc();
      }

      parserReference.computeStatistics();
      averageParseTime = SupportFunctions.calculateMedian(parseTimes);

      // ,Average parser time (seconds), No. of reachable SPPF nodes, No. of
      // total SPPF nodes, No. of reachable SPPF edges, No. of total SPPF edges,
      // No. of reachable non-token SPPF nodes, No. of
      // total non-token SPPF nodes, No. of reachable non-token SPPF edges, No.
      // of total non-token SPPF edges,
      // No. of ambiguous nodes, No. of GSS Nodes, No. of GSS
      // Edges, No. of Descriptors
      System.out.print("," + averageParseTime * SupportFunctions.NANOTOSEC + ',' + countNodes(parserReference) + ','
              + countNodesFull(parserReference) + ',' + countEdges(parserReference) + ','
              + countEdgesFull(parserReference) + ',' + countNonTokenNodes(parserReference) + ','
              + countNonTokenNodesFull(parserReference) + ',' + countNonTokenEdges(parserReference) + ','
              + countNonTokenEdgesFull(parserReference) + ',' + parserReference.sppfAmbiguityNodes + ','
              + parserReference.gssNodeHistogram.weightedSumBuckets() + ','
              + parserReference.gssEdgeHistogram.weightedSumBuckets() + ','
              + parserReference.descriptorHistogram.weightedSumBuckets());
    }
  }

  /**
   * Marks the entire subtree rooted at the given element as visited in the
   * given ESPPF.
   * 
   * @param parser
   *          The parser for which the ESPPF is constructed
   * @param element
   *          The root element for the subtree
   */
  private static void markSubtree(GLLHashPool parser, int element) {
    final Stack<Integer> stack = new Stack<Integer>();
    stack.push(element);

    while (!stack.isEmpty()) {
      final int currentElement = stack.pop();
      if (!parser.sppfNodeVisited(currentElement)) {
        if (parser.getLabelKind(parser.sppfNodeLabel(currentElement)) != GLLSupport.ART_K_EPSILON) {
          parser.sppfNodeSetVisited(currentElement);
        }
        for (int tmp = parser.sppfNodePackNodeList(currentElement); tmp != 0; tmp =
                parser.sppfPackNodePackNodeList(tmp)) {
          final int rightChild = parser.sppfPackNodeRightChild(tmp);
          if (rightChild != 0) {
            stack.push(rightChild);
          }
          final int leftChild = parser.sppfPackNodeLeftChild(tmp);
          if (leftChild != 0) {
            stack.push(leftChild);
          }
        }
      }
    }
  }
}
