#!/usr/bin/env bash

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#
# This script cleans up old transaction logs and snapshots
#

#
# If this scripted is run out of /usr/bin or some other system bin directory
# it should be linked to and not copied. Things like java jar files are found
# relative to the canonical path of this script.
#

# use POSTIX interface, symlink is followed automatically
SCLIBIN="${BASH_SOURCE-$0}"
SCLIBIN="$(dirname "${SCLIBIN}")"
SCLIBINDIR="$(cd "${SCLIBIN}"; pwd)"

LOG4J_PROP_FILE="${SCLIBIN}/../conf/log4j.properties"

# JAVA
if [ "$JAVA_HOME" != "" ]; then
  JAVA="$JAVA_HOME/bin/java"
else
  JAVA=java
fi

# CLASSPATH
for i in "$SCLIBINDIR"/../solrj-cli-*.jar
do
  CLASSPATH="$i:$CLASSPATH"
done
for i in "$SCLIBINDIR"/../lib/*.jar
do
    CLASSPATH="$i:$CLASSPATH"
done

${JAVA} -cp "$CLASSPATH" \
        -Dlog4j.configuration="${LOG4J_PROP_FILE}" \
        solrbook.ch11.solrj.cli.SolrJCLI "$@"
