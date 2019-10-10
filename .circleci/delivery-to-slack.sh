#!/bin/bash
# $1 : flavor
# $2 : Slack Channel
# $3 : Branch

VERSION="$(./gradlew -q getAppVersion)"
echo $VERSION
VERSION=${VERSION%####*}
echo $VERSION
VERSION=${VERSION#*####}
echo $VERSION

NAME="Fomes-$VERSION-$1"
APK_FILE_NAME="$NAME.apk"

echo $APK_FILE_NAME

RESPONSE=$(
    curl -F file=@app/build/outputs/apk/$1/$APK_FILE_NAME \
        -F channels=$2 \
        -F initial_comment="$3 브랜치에서 $1 서버에 연결된 앱 배달왔어요! :gift:" \
        -F token=$SLACK_BOT_TOKEN \
        https://slack.com/api/files.upload
)

if [ $1 == "release" ]; then
    APK_MESSAGE_META_DATA=$(echo $RESPONSE | jq '.file.shares.public')
    APK_MESSAGE_CHANNEL_ID=$(echo $APK_MESSAGE_META_DATA | jq 'keys[0]')
    APK_MESSAGE_THREAD_ID_PATH=".$APK_MESSAGE_CHANNEL_ID[0].ts"
    APK_MESSAGE_THREAD_ID_STRING=$(echo $APK_MESSAGE_META_DATA | jq $APK_MESSAGE_THREAD_ID_PATH)
    APK_MESSAGE_THREAD_ID=$(echo $APK_MESSAGE_THREAD_ID_STRING | sed 's/^"\([^"]*\).*/\1/')
    echo $APK_MESSAGE_THREAD_ID

    MAPPING_FILE_NAME="$NAME-mapping.txt"
    echo $MAPPING_FILE_NAME

    curl -F file=@app/build/outputs/apk/$1/$MAPPING_FILE_NAME \
        -F channels=$2 \
        -F thread_ts=$APK_MESSAGE_THREAD_ID \
        -F initial_comment="$3 브랜치에서 $VERSION 버전 앱 매핑파일 배달왔어요! :gift:" \
        -F token=$SLACK_BOT_TOKEN \
        https://slack.com/api/files.upload
fi