/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package solrbook.ch11.solrj.cli.command;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.apache.lucene.util.LuceneTestCase.Slow;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.cloud.AbstractDistribZkTestBase;
import org.apache.solr.cloud.SolrCloudTestCase;
import org.junit.BeforeClass;

@Slow
public class CommandTestBase extends SolrCloudTestCase {

  private ByteArrayOutputStream baos;
  private PrintStream out = System.out;

  public static String COLLECTION = "collection1";
  public static String CONF_NAME = COLLECTION + "_config";
  public static String CONF_DIR = "src/test/resources/solr/" + COLLECTION + "/conf";
  public static int NUM_NODES = 1;
  public static int NUM_SHARDS = 1;
  public static int NUM_REPLICAS = 1;
  public static int TIMEOUT = 30;

  @BeforeClass
  public static void setupCluster() throws Exception {
    configureCluster(NUM_NODES).addConfig(CONF_NAME, getFile(CONF_DIR).toPath()).configure();

    CollectionAdminRequest.createCollection(COLLECTION, CONF_NAME, NUM_SHARDS, NUM_REPLICAS)
        .process(cluster.getSolrClient());

    AbstractDistribZkTestBase.waitForRecoveriesToFinish(COLLECTION, cluster.getSolrClient().getZkStateReader(), false,
        true, TIMEOUT);
  }

  public void changeOutput() {
    baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(new BufferedOutputStream(baos)));
  }

  public String getOutput() throws IOException {
    System.out.flush();
    String ret = baos.toString();

    if (baos != null) {
      baos.close();
    }

    return ret;
  }

  public void restoreOutput() {
    System.setOut(out);
  }

  public static String readFileAsString(final String path) throws IOException {
    return Files.lines(Paths.get(path), Charset.forName("UTF-8"))
        .collect(Collectors.joining(System.getProperty("line.separator")));
  }

}
