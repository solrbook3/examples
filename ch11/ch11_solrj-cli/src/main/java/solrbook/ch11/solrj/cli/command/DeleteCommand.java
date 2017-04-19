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

import java.util.Map;

import org.apache.solr.client.solrj.response.UpdateResponse;

/**
 * DeleteCommand クラスは，コマンドライン引数で指定された ID のドキュメントをインデックスから削除し， 結果を標準出力へ出力します。
 */
public class DeleteCommand extends Command {
  public static final String NAME = "delete";
  public static final String HELP = "ドキュメントの削除リクエストを Solr へ送信";

  /**
   * id コマンドライン引数の定数
   */
  public static final String[] ARG_ID_FLAGS = { "id" };
  public static final String[] ARG_ID_METAVAR = { "ID" };
  public static final String ARG_ID_DEST = "id";
  public static final String ARG_ID_DEFAULT = null;
  public static final String ARG_ID_HELP = "削除するドキュメントの ID を指定";

  public String id = ARG_ID_DEFAULT;

  /**
   * コンストラクター
   */
  public DeleteCommand() {
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
     * ID を設定
     */
    if (parameters.containsKey(ARG_ID_DEST)) {
      id = (String) parameters.get(ARG_ID_DEST);
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
       * SolrClient で削除リクエストを送信
       */
      UpdateResponse updateResponse = solrClient.deleteById(id);

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
