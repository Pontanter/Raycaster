#!/bin/bash

if command -v java &> /dev/null
then
    if command -v javac &> /dev/null
    then
        javac *.java
        java Main
        rm -f "Main\$1.class" "Object.class" "Ray.class" "Vector.class"
        # cleaning up temp files manually as to not accidentally delete other potential files
    else
        echo "Javac not found, please install JDK, then retry."
    fi
else
    echo "Java not found, please install JDK, then retry."
fi
