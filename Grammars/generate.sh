#!/bin/sh
ARTHOME=/home/robert/workspace/artMGLL/versions/current
cp $ARTHOME/gll.jar /home/robert/workspace/LADLex/gll.jar

$ARTHOME/art -Nuk.ac.rhul.csle.tooling.CSLexer -nCSLexerParser csharpLexer.art 
$ARTHOME/art -Nuk.ac.rhul.csle.tooling.JavaLexer -nJavaImpLexerParser JavaLexerImp.art 
$ARTHOME/art -Nuk.ac.rhul.csle.tooling.OCamlLexer -nOCMLLexerParser ocamlLexer.art 
$ARTHOME/art -Nuk.ac.rhul.csle.tooling.parsing -nCSBuiltinParser -M csharp.art 
$ARTHOME/art -Nuk.ac.rhul.csle.tooling.parsing -nCSParser -X -M csharpwLexBNF.art
$ARTHOME/art -Nuk.ac.rhul.csle.tooling.parsing -nCSParser2 -X csharp2BNF.art
$ARTHOME/art -Nuk.ac.rhul.csle.tooling.parsing -nCSScannerlessParser csharp2BNFCharacterLevel.art

mv CSLexerParser.java /home/robert/workspace/LADLex/src/uk/ac/rhul/csle/tooling/CSLexer/CSLexerParser.java
mv JavaImpLexerParser.java /home/robert/workspace/LADLex/src/uk/ac/rhul/csle/tooling/JavaLexer/JavaImpLexerParser.java
mv OCMLLexerParser.java /home/robert/workspace/LADLex/src/uk/ac/rhul/csle/tooling/OCamlLexer/OCMLLexerParser.java

mv CSParser.java /home/robert/workspace/LADLex/src/uk/ac/rhul/csle/tooling/parsing/CSParser.java
mv CSBuiltinParser.java /home/robert/workspace/LADLex/src/uk/ac/rhul/csle/tooling/parsing/CSBuiltinParser.java
mv CSScannerlessParser.java /home/robert/workspace/LADLex/src/uk/ac/rhul/csle/tooling/parsing/CSScannerlessParser.java
mv CSParser2.java /home/robert/workspace/LADLex/src/uk/ac/rhul/csle/tooling/parsing/CSParser2.java