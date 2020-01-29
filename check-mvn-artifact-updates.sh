#!/usr/bin/env bash

set -euxo pipefail

cd apt
mvn versions:display-dependency-updates -Dmaven.version.rules=file:///$HOME/.m2/mvn-artifact-update-detect-rules.xml
mvn versions:display-plugin-updates -Dmaven.version.rules=file:///$HOME/.m2/mvn-artifact-update-detect-rules.xml

cd ../apt-test
mvn versions:display-dependency-updates -Dmaven.version.rules=file:///$HOME/.m2/mvn-artifact-update-detect-rules.xml
mvn versions:display-plugin-updates -Dmaven.version.rules=file:///$HOME/.m2/mvn-artifact-update-detect-rules.xml

cd ../demo
mvn versions:display-dependency-updates -Dmaven.version.rules=file:///$HOME/.m2/mvn-artifact-update-detect-rules.xml
mvn versions:display-plugin-updates -Dmaven.version.rules=file:///$HOME/.m2/mvn-artifact-update-detect-rules.xml
