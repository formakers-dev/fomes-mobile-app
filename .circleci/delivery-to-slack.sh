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

APK_NAME="Fomes-$VERSION-$1.apk"
echo $APK_NAME

curl -F file=@app/build/outputs/apk/$1/$APK_NAME \
    -F channels=$2 \
    -F initial_comment="$3 브랜치에서 $1 서버에 연결된 앱 배달왔어요! :gift:" \
    -F token=$SLACK_BOT_TOKEN \
    https://slack.com/api/files.upload

if [ $1 == "release" ]; then
    curl -F file=@app/build/outputs/mapping/$1/mapping.txt \
        -F channels=$DEV_SLACK_DELIVERY_CHANNEL \
        -F initial_comment="$3 브랜치에서 $VERSION 버전 앱 매핑파일 배달왔어요! :gift:" \
        -F token=$SLACK_BOT_TOKEN \
        https://slack.com/api/files.upload
fi