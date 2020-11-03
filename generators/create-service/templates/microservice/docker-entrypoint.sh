#!/bin/bash

set -ex;

exec /usr/bin/java ${JAVA_OPTS} \
     	-Djava.security.egd=file:/dev/./urandom \
     	-jar /app/app.jar