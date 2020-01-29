#!/usr/bin/env bash

set -euxo pipefail

cd apt
mvn clean install
cd ../apt-test
mvn clean install
cd ../demo
mvn clean install
