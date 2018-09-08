#!/bin/bash
# $1 : flavor
# $2 : Slack Channel
# $3 : Branch
VERSION="$(./gradlew -q getAppVersion | grep -Po '(\d\.\d\.\d)')"
APK_NAME="Fomes-$VERSION-$1.apk"
echo $APK_NAME
curl -F file=@app/build/outputs/apk/$APK_NAME \
    -F channels=$2 \
    -F initial_comment="$3 브랜치에서 $1 서버에 연결된 앱 배달왔어요! :gift:" \
    -F token=$SLACK_BOT_TOKEN \
    https://slack.com/api/files.upload