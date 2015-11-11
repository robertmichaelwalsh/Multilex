package uk.ac.rhul.csle.tooling.CSLexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.cli.CommandLine;

import uk.ac.rhul.csle.tooling.io.IOReadWrite;
import uk.ac.rhul.csle.tooling.io.SupportFunctions;
import uk.ac.rhul.csle.tooling.lexer.GLLLexer;
import uk.ac.rhul.csle.tooling.lexer.MultiLexer;
import uk.ac.rhul.csle.tooling.lexer.RegularLexer;
import uk.ac.rhul.csle.tooling.parsing.InvalidParseException;

public class TestCSLexer {

  public static void main(String[] args) {
    CommandLine line = SupportFunctions.processArguments(args);
    if (line == null) {
      System.err.println("ERROR: Invalid arguments supplied.");
      return;
    }
    String output_directory = line.hasOption('d') ? line.getOptionValue('d') : "output";
    String input = "";
    try {
      input = IOReadWrite.readFile(line.getArgs()[0]).trim();
    } catch (IOException e1) {
      // Shouldn't happen as the program checked earlier
      e1.printStackTrace();
    }

    // Create the output directory if it does not exist
    try {
      Files.createDirectories(new File(output_directory).toPath());
    } catch (IOException e1) {
      System.err.println("Unable to create output directory.");
      return;
    }

    // **CSHARP LEXER**//
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
              "increment", "decrement", "new_line", "boolean_literal", "add_op", "sub_op", "not_op", "mul_op", "div_op",
              "mod_op", "or_op", "lshift_op", "rshift_op", "lessthan_op", "greaterthan_op", "lessthaneq_op",
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

    final String unqualifiedFilename =
            line.getArgs()[0].substring(line.getArgs()[0].lastIndexOf('/') + 1, line.getArgs()[0].indexOf("."));

    try {
      lex.lexSegmented(unqualifiedFilename, input);
      String tok = lex.toTokOrdered(lex.getTriples());
      IOReadWrite.writeFile(
              output_directory + "/" + line.getArgs()[0].substring(line.getArgs()[0].lastIndexOf('/') + 1) + ".tok",
              tok);
    } catch (InvalidParseException e) {
      e.printStackTrace();
    }
  }
}
