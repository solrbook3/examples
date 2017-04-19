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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 * SearchCommand クラスは，コマンドライン引数で指定されたオプションでインデックスを検索し， 結果を標準出力へ出力します。
 */
public class SearchCommand extends Command {
  public static final String NAME = "search";
  public static final String HELP = "ドキュメントの検索リクエストを Solr へ送信";

  /*
   * -q, --query-string コマンドライン引数の定数
   */
  public static final String[] ARG_QUERY_STRING_FLAGS = { "-q", "--query-string" };
  public static final String[] ARG_QUERY_STRING_METAVAR = { "QUERY_STRING" };
  public static final String ARG_QUERY_STRING_DEST = "queryString";
  public static final String ARG_QUERY_STRING_DEFAULT = null;
  public static final String ARG_QUERY_STRING_HELP = "Solr のクエリ文字列を指定";

  /*
   * -s, --start コマンドライン引数の定数
   */
  public static final String[] ARG_START_FLAGS = { "-s", "--start" };
  public static final String[] ARG_START_METAVAR = { "START" };
  public static final String ARG_START_DEST = "start";
  public static final Integer ARG_START_DEFAULT = 0;
  public static final String ARG_START_HELP = "取得する検索結果の開始位置を指定";

  /*
   * -r, --rows コマンドライン引数の定数
   */
  public static final String[] ARG_ROWS_FLAGS = { "-r", "--rows" };
  public static final String[] ARG_ROWS_METAVAR = { "ROWS" };
  public static final String ARG_ROWS_DEST = "rows";
  public static final Integer ARG_ROWS_DEFAULT = 10;
  public static final String ARG_ROWS_HELP = "取得する検索結果の件数を指定";

  /*
   * -S, --sort-field コマンドライン引数の定数
   */
  public static final String[] ARG_SORT_FIELD_FLAGS = { "-S", "--sort-field" };
  public static final String[] ARG_SORT_FIELD_METAVAR = { "SORT_FIELD" };
  public static final String ARG_SORT_FIELD_DEST = "sortField";
  public static final String ARG_SORT_FIELD_DEFAULT = null;
  public static final String ARG_SORT_FIELD_HELP = "ソートするフィールド名を指定";

  /*
   * -o, --sort-order コマンドライン引数の定数
   */
  public static final String[] ARG_SORT_ORDER_FLAGS = { "-o", "--sort-order" };
  public static final String[] ARG_SORT_ORDER_METAVAR = { "SORT_ORDER" };
  public static final String ARG_SORT_ORDER_DEST = "sortOrder";
  public static final String ARG_SORT_ORDER_DEFAULT = null;
  public static final String ARG_SORT_ORDER_HELP = "ソート順を指定 (asc | desc)";

  /*
   * -l, --field-list コマンドライン引数の定数
   */
  public static final String[] ARG_FIELD_LIST_FLAGS = { "-l", "--field-list" };
  public static final String[] ARG_FIELD_LIST_METAVAR = { "FIELD_LIST" };
  public static final String ARG_FIELD_LIST_DEST = "fieldList";
  public static final String ARG_FIELD_LIST_DEFAULT = "*,score";
  public static final String ARG_FIELD_LIST_HELP =
      "取得する検索結果に含めるドキュメントのフィールドを指定 例: title,description";

  public String queryString = ARG_QUERY_STRING_DEFAULT;
  public int start = ARG_START_DEFAULT;
  public int rows = ARG_ROWS_DEFAULT;
  public String sortField = ARG_SORT_FIELD_DEFAULT;
  public String sortOrder = ARG_SORT_ORDER_DEFAULT;
  public String fieldList = ARG_FIELD_LIST_DEFAULT;

  /**
   * コンストラクター
   */
  public SearchCommand() {
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
     * クエリー文字列を設定
     */
    if (parameters.containsKey(ARG_QUERY_STRING_DEST)) {
      queryString = (String) parameters.get(ARG_QUERY_STRING_DEST);
    }

    /*
     * 取得する検索結果の開始位置を設定
     */
    if (parameters.containsKey(ARG_START_DEST)) {
      start = (Integer) parameters.get(ARG_START_DEST);
    }

    /*
     * 取得する検索結果の件数を設定
     */
    if (parameters.containsKey(ARG_ROWS_DEST)) {
      rows = (Integer) parameters.get(ARG_ROWS_DEST);
    }

    /*
     * ソートするフィールド名を設定
     */
    if (parameters.containsKey(ARG_SORT_FIELD_DEST)) {
      sortField = (String) parameters.get(ARG_SORT_FIELD_DEST);
    }

    /*
     * ソート順を設定
     */
    if (parameters.containsKey(ARG_SORT_ORDER_DEST)) {
      sortOrder = (String) parameters.get(ARG_SORT_ORDER_DEST);
    }

    /*
     * 取得するフィールドリストを設定
     */
    if (parameters.containsKey(ARG_FIELD_LIST_DEST)) {
      fieldList = (String) parameters.get(ARG_FIELD_LIST_DEST);
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
       * SolrQuery オブジェクトを作成
       */
      SolrQuery solrQuery = new SolrQuery(queryString);

      /*
       * 開始位置と件数を設定
       */
      solrQuery.setStart(start);
      solrQuery.setRows(rows);

      /*
       * ソートフィールドが指定されているかチェック
       */
      if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
        /*
         * ソートフィールドが指定されていたら，ソート順を設定
         */
        solrQuery.setSort(sortField,
            Enum.valueOf(org.apache.solr.client.solrj.SolrQuery.ORDER.class, sortOrder));
      }

      /*
       * 検索結果に含めるドキュメントのフィールドを設定
       */
      for (String f : fieldList.split(",")) {
        if (StringUtils.isNotEmpty(f)) {
          solrQuery.addField(f.trim());
        }
      }

      /*
       * SolrClient で検索リクエストを送信
       */
      QueryResponse queryResponse = solrClient.query(solrQuery);

      /*
       * 検索結果のドキュメントを格納するための List オブジェクトを作成
       */
      List<Map<String, Object>> documentList = new LinkedList<Map<String, Object>>();

      /*
       * 検索結果の取得
       */
      SolrDocumentList solrDocumentList = queryResponse.getResults();

      /*
       * 検索結果のループ処理
       */
      for (SolrDocument solrDocument : solrDocumentList) {
        /*
         * ヒットしたドキュメント情報を格納する Map を作成
         */
        Map<String, Object> documentMap = new HashMap<String, Object>();

        /*
         * ヒットしたドキュメントに含まれるフィールドのループ処理
         */
        for (String fieldName : solrDocument.getFieldNames()) {
          /*
           * フィールド名と値を Map オブジェクトへ追加
           */
          Object fieldValue = solrDocument.getFieldValue(fieldName);
          documentMap.put(fieldName, fieldValue);
        }

        /*
         * ドキュメントのスコア値をドキュメント Map へ追加
         */
        documentMap.put("score", solrDocument.getFieldValue("score"));

        /*
         * 検索結果のドキュメントリストへドキュメント情報を追加
         */
        documentList.add(documentMap);
      }

      /*
       * 検索にかかった時間を追加
       */
      response.put("QTime", queryResponse.getQTime());

      /*
       * クエリーにヒットしたドキュメントの最大スコアを追加
       */
      response.put("maxScore", solrDocumentList.getMaxScore());

      /*
       * クエリーにヒットした全体の件数を追加
       */
      response.put("numFound", solrDocumentList.getNumFound());

      /*
       * コマンド実行結果に検索結果のドキュメントリストを追加
       */
      response.put("result", documentList);

      status = STATUS_SUCCESS;
      message = SUCCESS_MESSAGE;
    } catch (Exception e) {
      /*
       * 例外が発生したら，エラーステータスとエラーメッセージをセット
       */
      status = STATUS_ERROR;
      message = e.getMessage();
    }
  }

}
