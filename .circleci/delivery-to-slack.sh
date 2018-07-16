#!/bin/bash
# $1 : flavor
# $2 : Slack Channel
# $3 : Branch
VERSION="$(./gradlew -q getAppVersion)"
APK_NAME="AppBee-$VERSION-$1.apk"
echo APK_NAME
curl -F file=@app/build/outputs/apk/$APK_NAME \
    -F channels=$2 \
    -F initial_comment="$3 브랜치에서 앱 배달왔어요!" \
    -F token=$SLACK_BOT_TOKEN \
    https://slack.com/api/files.upload