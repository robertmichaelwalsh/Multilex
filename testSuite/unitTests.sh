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
    resultsFile=_unitResultsReg.csv;
else
    resultsFile=_unitResults.csv;
fi

printf "File Length" > "$resultsFile"

# printf ",C# character-level grammar parse time (seconds)," >> "$resultsFile"
# printf "C# character-level grammar reachable SPPF node count," >> "$resultsFile"
# printf "C# character-level grammar full SPPF node count," >> "$resultsFile"
# printf "C# character-level grammar reachable SPPF edge count," >> "$resultsFile"
# printf "C# character-level grammar full SPPF edge count," >> "$resultsFile"
# printf "C# character-level grammar ambiguity node count," >> "$resultsFile"
# printf "C# character-level grammar GSS node count" >> "$resultsFile";

#printf ",C# w/o pruning lex time (seconds)," >> "$resultsFile"
#printf "C# w/o pruning items initially pruned," >> "$resultsFile"
#printf "C# w/o pruning parse time (seconds)," >> "$resultsFile"
#printf "C# w/o pruning total time (seconds)," >> "$resultsFile"
#printf "C# w/o pruning reachable SPPF node count," >> "$resultsFile"
#printf "C# w/o pruning full SPPF node count," >> "$resultsFile"
#printf "C# w/o pruning ambiguity node count," >> "$resultsFile"
#printf "C# w/o pruning GSS node count" >> "$resultsFile";

printf ",C# character extents output triple set size," >> "$resultsFile"
printf "C# character extents output no. of tokenisations," >> "$resultsFile"
printf "C# character extents lex time (seconds)," >> "$resultsFile"
printf "C# character extents parse time (seconds)," >> "$resultsFile"
printf "C# character extents total time (seconds)," >> "$resultsFile"
printf "C# character extents reachable SPPF node count," >> "$resultsFile"
printf "C# character extents full SPPF node count," >> "$resultsFile"
printf "C# character extents reachable SPPF edge count," >> "$resultsFile"
printf "C# character extents full SPPF edge count," >> "$resultsFile"
printf "C# character extents ambiguity node count," >> "$resultsFile"
printf "C# character extents GSS node count," >> "$resultsFile"
printf "C# character extents GSS edge count," >> "$resultsFile"
printf "C# character extents descriptor count" >> "$resultsFile";

printf ",C# unit extents output triple set size," >> "$resultsFile"
printf "C# unit extents output no. of tokenisations," >> "$resultsFile"
printf "C# unit extents lex time (seconds)," >> "$resultsFile"
printf "C# unit extents parse time (seconds)," >> "$resultsFile"
printf "C# unit extents total time (seconds)," >> "$resultsFile"
printf "C# unit extents reachable SPPF node count," >> "$resultsFile"
printf "C# unit extents full SPPF node count," >> "$resultsFile"
printf "C# unit extents reachable SPPF edge count," >> "$resultsFile"
printf "C# unit extents full SPPF edge count," >> "$resultsFile"
printf "C# unit extents ambiguity node count," >> "$resultsFile"
printf "C# unit extents GSS node count," >> "$resultsFile"
printf "C# unit extents GSS edge count," >> "$resultsFile"
printf "C# unit extents descriptor count" >> "$resultsFile";

printf "\n" >> "$resultsFile";

for f in `ls -v output/*.cs`; do
    echo $f;
    wc -m $f | awk '{print $1}' | tr -d '\n' >> "$resultsFile";
    if [ "$regularMode" = true ]; then
        java -Dfile.encoding=UTF-8 -classpath ../bin:../commons-cli-1.2.jar:../gll.jar \
        uk.ac.rhul.csle.tooling.CSLexer.Measures.CSMeasurementTests -Smultilex -r $f >> "$resultsFile";
        java -Dfile.encoding=UTF-8 -classpath ../bin:../commons-cli-1.2.jar:../gll.jar \
        uk.ac.rhul.csle.tooling.CSLexer.Measures.CSMeasurementTests -Smultilex -E unit -r $f >> "$resultsFile";
    else
        java -Dfile.encoding=UTF-8 -classpath ../bin:../commons-cli-1.2.jar:../gll.jar \
        uk.ac.rhul.csle.tooling.CSLexer.Measures.CSMeasurementTests -Smultilex $f >> "$resultsFile";
        java -Dfile.encoding=UTF-8 -classpath ../bin:../commons-cli-1.2.jar:../gll.jar \
        uk.ac.rhul.csle.tooling.CSLexer.Measures.CSMeasurementTests -Smultilex -E unit $f >> "$resultsFile";
    fi
    printf "\n" >> "$resultsFile";
done
rm output/*.exe