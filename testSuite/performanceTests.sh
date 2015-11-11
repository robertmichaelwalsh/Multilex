#!/bin/sh
USAGE="Usage: $0 [-r]"
regularMode=false;
while getopts "r" opt;
do 
case $opt in
    r) regularMode=true;;
    ?) 
        echo "Error unknown option -$OPTARG";
        echo "$USAGE";
        exit 1;;
esac
shift
done

if [ "$regularMode" = true ]; then
    resultsFile=_performanceResultsReg.csv;
else
    resultsFile=_performanceResults.csv;
fi

printf "File Length" > "$resultsFile"

printf ",C# character-level grammar parse time (seconds)," >> "$resultsFile"
printf "C# character-level grammar reachable SPPF node count," >> "$resultsFile"
printf "C# character-level grammar full SPPF node count," >> "$resultsFile"
printf "C# character-level grammar reachable SPPF edge count," >> "$resultsFile"
printf "C# character-level grammar full SPPF edge count," >> "$resultsFile"
printf "C# character-level grammar reachable non-token SPPF node count," >> "$resultsFile"
printf "C# character-level grammar full non-token SPPF node count," >> "$resultsFile"
printf "C# character-level grammar reachable non-token SPPF edge count," >> "$resultsFile"
printf "C# character-level grammar full SPPF non-token edge count," >> "$resultsFile"
printf "C# character-level grammar ambiguity node count," >> "$resultsFile"
printf "C# character-level grammar GSS node count," >> "$resultsFile"
printf "C# character-level grammar GSS edge count," >> "$resultsFile"
printf "C# character-level grammar descriptor count" >> "$resultsFile";

printf ",C# w/o lexical disambiguation output triple set size," >> "$resultsFile"
printf "C# w/o lexical disambiguation output no. of tokenisations," >> "$resultsFile"
printf "C# w/o lexical disambiguation lex time (seconds)," >> "$resultsFile"
printf "C# w/o lexical disambiguation parse time (seconds)," >> "$resultsFile"
printf "C# w/o lexical disambiguation total time (seconds)," >> "$resultsFile"
printf "C# w/o lexical disambiguation reachable SPPF node count," >> "$resultsFile"
printf "C# w/o lexical disambiguation full SPPF node count," >> "$resultsFile"
printf "C# w/o lexical disambiguation reachable SPPF edge count," >> "$resultsFile"
printf "C# w/o lexical disambiguation full SPPF edge count," >> "$resultsFile"
printf "C# w/o lexical disambiguation ambiguity node count," >> "$resultsFile"
printf "C# w/o lexical disambiguation GSS node count," >> "$resultsFile"
printf "C# w/o lexical disambiguation GSS edge count," >> "$resultsFile"
printf "C# w/o lexical disambiguation descriptor count" >> "$resultsFile";

printf ",C# with full lexical disambiguation output triple set size," >> "$resultsFile"
printf "C# with full lexical disambiguation output no. of tokenisations," >> "$resultsFile"
printf "C# with full lexical disambiguation lex time (seconds)," >> "$resultsFile"
printf "C# with full lexical disambiguation parse time (seconds)," >> "$resultsFile"
printf "C# with full lexical disambiguation total time (seconds)," >> "$resultsFile";
printf "C# with full lexical disambiguation reachable SPPF node count," >> "$resultsFile"
printf "C# with full lexical disambiguation full SPPF node count," >> "$resultsFile"
printf "C# with full lexical disambiguation reachable SPPF edge count," >> "$resultsFile"
printf "C# with full lexical disambiguation full SPPF edge count," >> "$resultsFile"
printf "C# with full lexical disambiguation ambiguity node count," >> "$resultsFile"
printf "C# with full lexical disambiguation GSS node count," >> "$resultsFile"
printf "C# with full lexical disambiguation GSS edge count," >> "$resultsFile"
printf "C# with full lexical disambiguation descriptor count" >> "$resultsFile";

printf "\n" >> "$resultsFile";

for f in `ls -v output/*.cs`; do
    echo $f;
    wc -m $f | awk '{print $1}' | tr -d '\n' >> "$resultsFile";
    java -Dfile.encoding=UTF-8 -classpath ../bin:../commons-cli-1.2.jar:../gll.jar \
    uk.ac.rhul.csle.tooling.CSLexer.Measures.CSMeasurementTests -Scharacter $f >> "$resultsFile";
    if [ "$regularMode" = true ]; then
        java -Dfile.encoding=UTF-8 -classpath ../bin:../commons-cli-1.2.jar:../gll.jar \
        uk.ac.rhul.csle.tooling.CSLexer.Measures.CSMeasurementTests -Smultilex -r $f >> "$resultsFile";
        java -Dfile.encoding=UTF-8 -classpath ../bin:../commons-cli-1.2.jar:../gll.jar \
        uk.ac.rhul.csle.tooling.CSLexer.Measures.CSMeasurementTests -Smultilex -L -P -r $f >> "$resultsFile";
    else
        java -Dfile.encoding=UTF-8 -classpath ../bin:../commons-cli-1.2.jar:../gll.jar \
        uk.ac.rhul.csle.tooling.CSLexer.Measures.CSMeasurementTests -Smultilex $f >> "$resultsFile";
        java -Dfile.encoding=UTF-8 -classpath ../bin:../commons-cli-1.2.jar:../gll.jar \
        uk.ac.rhul.csle.tooling.CSLexer.Measures.CSMeasurementTests -Smultilex -L -P $f >> "$resultsFile";
    fi
    printf "\n" >> "$resultsFile";
done
rm output/*.exe