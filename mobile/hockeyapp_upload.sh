#!/bin/bash

# Usage:
# ./hockeyapp_upload.sh token app_id apk_file
#
# Example:
# ./hockeyapp_upload.sh j290dj h289sj build/outputs/apk/mobile-preview-debug.apk

curl \
  -F "status=2" \
  -F "notify=0" \
  -F "ipa=@$3" \
  -H "X-HockeyAppToken: $1" \
  https://rink.hockeyapp.net/api/2/apps/$2/app_versions/upload