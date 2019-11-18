#!/usr/bin/env bash

SCRIPT_DIR=`dirname $0`
echo $SCRIPT_DIR

if [ -z "$1" ]; then
    echo '디렉토리를 입력해주세요 (cwebp.sh ../app/src/main/res/)'
    exit
fi

REMOVE_ORIGINAL_FILE=false
if [[ "$2" == "-r" ]]; then
    echo 'webp 변환 후 원본파일을 삭제합니다'
    REMOVE_ORIGINAL_FILE=true
fi

PARAMS=('-m 6 -q 70 -mt -af -progress')

if [[ $# -ne 0 ]]; then
	PARAMS=$@;
fi

shopt -s nullglob nocaseglob extglob

DIRECTORIES=('hdpi' 'mdpi' 'xhdpi' 'xxhdpi' 'xxxhdpi')

for DIRECTORY in ${DIRECTORIES[@]}; do
    for FILE in $1/drawable-${DIRECTORY}/*.png; do
        if [[ ${FILE} =~ .*\.9\..* ]]; then
            echo "나인패치는 webp 변환하지 않습니다!"
        else
            ${SCRIPT_DIR}/cwebp ${PARAMS} "$FILE" -o "${FILE%.*}".webp;
            if [[ ${REMOVE_ORIGINAL_FILE} == true ]]; then
                echo "원본 파일은 삭제합니다 (${FILE})"
                rm $FILE
            fi
        fi
        echo $FILE
    done
done