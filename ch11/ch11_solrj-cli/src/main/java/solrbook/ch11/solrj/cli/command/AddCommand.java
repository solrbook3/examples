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

import java.util.Iterator;
import java.util.Map;

import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AddCommand クラスは，コマンドライン引数で指定されたドキュメント (JSON) を， インデックスへ追加し， 結果を標準出力へ出力します。
 */
public class AddCommand extends Command {
  public static final String NAME = "add";
  public static final String HELP = "ドキュメントの追加/更新リクエストを Solr へ送信";

  /**
   * document-json コマンドライン引数の定数
   */
  public static final String[] ARG_DOCUMENT_JSON_FLAGS = { "document-json" };
  public static final String[] ARG_DOCUMENT_JSON_METAVAR = { "DOCUMENT_JSON" };
  public static final String ARG_DOCUMENT_JSON_DEST = "documentJSON";
  public static final String ARG_DOCUMENT_JSON_DEFAULT = null;
  public static final String ARG_DOCUMENT_JSON_HELP = "追加/更新するドキュメント (JSON) を指定";

  public String documentJSON = ARG_DOCUMENT_JSON_DEFAULT;

  /**
   * コンストラクター
   */
  public AddCommand() {
    super();

    /*
     * コマンド名とヘルプをセット
     */
    name = NAME;
    help = HELP;
  }

  /*
   * (non-Javadoc)
   * 
   * @see Command#preProcess(java.util.Map)
   */
  @Override
  public void preProcess(Map<String, Object> parameters) throws Exception {
    super.preProcess(parameters);

    /*
     * JSON ドキュメントを設定
     */
    if (parameters.containsKey(ARG_DOCUMENT_JSON_DEST)) {
      documentJSON = (String) parameters.get(ARG_DOCUMENT_JSON_DEST);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see Command#mainProcess(java.util.Map)
   */
  @Override
  public void mainProcess(Map<String, Object> parameters) throws Exception {
    try {
      /*
       * 空の SolrInputDocument オブジェクトを作成
       */
      SolrInputDocument solrInputDocument = new SolrInputDocument();

      /*
       * 指定された JSON のドキュメントをパース
       */
      JsonNode documentJsonNode = new ObjectMapper().readTree(documentJSON);

      /*
       * JSON に記述されているフィールド名をループで処理
       */
      for (Iterator<String> i = documentJsonNode.fieldNames(); i.hasNext();) {
        /*
         * フィールド名を取得
         */
        String name = i.next();

        /*
         * フィールド値を取得
         */
        String value = null;
        if (documentJsonNode.get(name).has("value")) {
          value = documentJsonNode.get(name).get("value").asText();
        }

        /*
         * ブースト値を取得
         */
        float boost = 1.0f;
        if (documentJsonNode.get(name).has("boost")) {
          boost = (float) documentJsonNode.get(name).get("boost").asDouble();
        }

        /*
         * フィールド情報を元にフィールドを追加
         */
        solrInputDocument.addField(name, value, boost);
      }

      /*
       * SolrClient で追加リクエストを送信
       */
      UpdateResponse updateResponse = solrClient.add(solrInputDocument);

      /*
       * リクエストが成功したかチェック
       */
      if (updateResponse.getStatus() == 0) {
        /*
         * 成功していたらコミットを送信
         */
        updateResponse = solrClient.commit();
      }

      status = STATUS_SUCCESS;
      message = SUCCESS_MESSAGE;
    } catch (Exception e) {
      /*
       * 例外が発生したら，エラーステータスとエラーメッセージをセット
       */
      status = STATUS_ERROR;
      message = e.getMessage();

      /*
       * 例外が発生した場合，Solr への操作をキャンセルするため，ロールバック
       */
      solrClient.rollback();
    }
  }

}
