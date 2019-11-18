#!/usr/bin/env bash

if [ -z "$1" ]; then
    echo '디렉토리를 입력해주세요 (cwebp.sh ../app/src/main/res/'
    exit
fi

PARAMS=('-m 6 -q 70 -mt -af -progress')

if [ $# -ne 0 ]; then
	PARAMS=$@;
fi

shopt -s nullglob nocaseglob extglob

DIRECTORIES=('hdpi' 'mdpi' 'xhdpi' 'xxhdpi' 'xxxhdpi')

for DIRECTORY in ${DIRECTORIES[@]}; do
    for FILE in $1/drawable-${DIRECTORY}/*.png; do
        echo $FILE
        ./cwebp $PARAMS "$FILE" -o "${FILE%.*}".webp;
        rm $FILE
    done
done