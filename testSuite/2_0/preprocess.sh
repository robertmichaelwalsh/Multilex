USAGE="Usage: $0 [-d output_directory]"
output_directory=output
set -- `getopt d: "$@"`
[ $# -lt 1 ] && exit 1
while [ $# -gt 0 ]
do 
    case "$1" in
        -d) output_directory="$2"; shift;;
        --) shift; break;;
        -*) 
            echo >&2 \
            "$USAGE"
            exit 1;;
        *)  break;;
    esac
    shift
done
if [ ! -d "$output_directory" ]; then
    mkdir "$output_directory"
fi
rm $output_directory/*
for f in `ls -v *.cs`; do
        echo $f
        java -Dfile.encoding=UTF-8 -classpath ../../bin:../../commons-cli-1.2.jar:../../gll.jar uk.ac.rhul.csle.tooling.CSLexer.initialProcessor.InitialProcessor -r -d$output_directory $f > $output_directory/$(basename $f).log 2>&1;
done
