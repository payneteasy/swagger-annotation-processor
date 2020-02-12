#!/usr/bin/env bash

set -euxo pipefail

cd apt
mvn clean install -DskipTests -Powasp-dependency-check
