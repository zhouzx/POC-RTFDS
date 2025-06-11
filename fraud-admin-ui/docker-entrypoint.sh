#!/bin/sh

# Replace API base URL in frontend configuration
if [ -n "$API_BASE_URL" ]; then
  echo "Configuring API base URL: $API_BASE_URL"
  sed -i "s|http://localhost:8080/api|$API_BASE_URL|g" /usr/share/nginx/html/static/js/*.js
fi

# Replace other frontend configuration parameters
if [ -n "$APP_TITLE" ]; then
  echo "Configuring application title: $APP_TITLE"
  sed -i "s|Fraud Detection Admin|$APP_TITLE|g" /usr/share/nginx/html/index.html
fi

# Replace API proxy URL in Nginx configuration
if [ -n "$API_PROXY_URL" ]; then
  echo "Configuring API proxy URL: $API_PROXY_URL"
  sed -i "s|http://fraud-detection-engine:8080/api/|$API_PROXY_URL|g" /etc/nginx/conf.d/default.conf
fi

# Execute CMD
exec "$@" 