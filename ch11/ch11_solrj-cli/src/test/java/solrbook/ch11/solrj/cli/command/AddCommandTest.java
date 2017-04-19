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

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.util.LuceneTestCase.Slow;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slow
public class AddCommandTest extends CommandTestBase {

  @Override
  public void setUp() throws Exception {
    super.setUp();

    changeOutput();
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();

    restoreOutput();
  }

  @Test
  public void testExecute() throws Exception {
    String solrUrl = cluster.getJettySolrRunners().get(0).getBaseUrl() + "/" + COLLECTION;
    String documentJSON = readFileAsString("./data/document1.json");

    Map<String, Object> attrs = new HashMap<String, Object>();
    attrs.put(AddCommand.ARG_SOLR_URL_DEST, solrUrl);
    attrs.put(AddCommand.ARG_DOCUMENT_JSON_DEST, documentJSON);

    AddCommand addCommand = new AddCommand();
    addCommand.execute(attrs);

    String expectedOutput = "{\"response\":{\"status\":0,\"message\":\"success\"}}\n";
    String actualOutput = getOutput();

    JsonNode expectedJsonNode = new ObjectMapper().readTree(expectedOutput);
    JsonNode actualJsonNode = new ObjectMapper().readTree(actualOutput);

    assertEquals(expectedJsonNode.get("response").get("message").asText(),
        actualJsonNode.get("response").get("message").asText());
  }

  @Test
  public void testExecuteSolrCloud() throws Exception {
    String zookeeperHost = cluster.getZkServer().getZkHost();
    String zookeeperChroot = "/solr";
    String collection = COLLECTION;
    String documentJSON = readFileAsString("./data/document1.json");

    Map<String, Object> attrs = new HashMap<String, Object>();
    attrs.put(AddCommand.ARG_ZOOKEEPER_HOST_DEST, zookeeperHost);
    attrs.put(AddCommand.ARG_ZOOKEEPER_CHROOT_DEST, zookeeperChroot);
    attrs.put(AddCommand.ARG_COLLECTION_DEST, collection);
    attrs.put(AddCommand.ARG_DOCUMENT_JSON_DEST, documentJSON);

    AddCommand addCommand = new AddCommand();
    addCommand.execute(attrs);

    String expectedOutput = "{\"response\":{\"status\":0,\"message\":\"success\"}}\n";
    String actualOutput = getOutput();

    JsonNode expectedJsonNode = new ObjectMapper().readTree(expectedOutput);
    JsonNode actualJsonNode = new ObjectMapper().readTree(actualOutput);

    assertEquals(expectedJsonNode.get("response").get("message").asText(),
        actualJsonNode.get("response").get("message").asText());
  }

}
