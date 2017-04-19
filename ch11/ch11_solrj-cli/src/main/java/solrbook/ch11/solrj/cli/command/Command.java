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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient.Builder;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.LBHttpSolrClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import solrbook.ch11.solrj.cli.SolrJCLI;

/**
 * Command クラスは，サブコマンドの親クラスとなります。 サブコマンドは，Command クラスを拡張して，必要な機能を実装してください。
 * Command クラスは，サブコマンドの共通するメソッドを提供します。
 */
public class Command implements CommandImpl {

  /**
   * コマンドの戻り値の定数
   */
  public static final int STATUS_SUCCESS = 0;
  public static final int STATUS_ERROR = 1;

  /**
   * コマンドの成功時のメッセージ定数
   */
  protected static final String SUCCESS_MESSAGE = "success";

  /**
   * コマンドの名前と，ヘルプ
   */
  public static final String NAME = SolrJCLI.class.getSimpleName();
  public static final String HELP = "SolrJ のサンプル CLI";

  /**
   * -s, --solr-url コマンドライン引数の定数
   */
  public static final String[] ARG_SOLR_URL_FLAGS = { "-s", "--solr-url" };
  public static final String[] ARG_SOLR_URL_METAVAR = { "SOLR_URL" };
  public static final String ARG_SOLR_URL_DEST = "solrUrl";
  public static final String ARG_SOLR_URL_DEFAULT = null;
  public static final String ARG_SOLR_URL_HELP =
      "Solr の URL を指定 (スタンドアローン) 例: http://localhost:8983/solr/collection1";

  /**
   * -z, --zookeeper-host コマンドライン引数の定数
   */
  public static final String[] ARG_ZOOKEEPER_HOST_FLAGS = { "-z", "--zookeeper-host" };
  public static final String[] ARG_ZOOKEEPER_HOST_METAVAR = { "ZOOKEEPER_HOST" };
  public static final String ARG_ZOOKEEPER_HOST_DEST = "zookeeperHost";
  public static final String ARG_ZOOKEEPER_HOST_DEFAULT = null;
  public static final String ARG_ZOOKEEPER_HOST_HELP =
      "ZooKeeper 接続文字列を指定 (SolrCloud) 例: localhost:2181";

  /**
   * -R, --zookeeper-chroot コマンドライン引数の定数
   */
  public static final String[] ARG_ZOOKEEPER_CHROOT_FLAGS = { "-R", "--zookeeper-chroot" };
  public static final String[] ARG_ZOOKEEPER_CHROOT_METAVAR = { "ZOOKEEPER_CHROOT" };
  public static final String ARG_ZOOKEEPER_CHROOT_DEST = "zookeeperChroot";
  public static final String ARG_ZOOKEEPER_CHROOT_DEFAULT = null;
  public static final String ARG_ZOOKEEPER_CHROOT_HELP =
      "SolrCloud のクラスター情報が格納されている ZooKeeper 上の znode を指定 (SolrCloud) 例: /solr";

  /**
   * -c, --collection コマンドライン引数の定数
   */
  public static final String[] ARG_COLLECTION_FLAGS = { "-c", "--collection" };
  public static final String[] ARG_COLLECTION_METAVAR = { "COLLECTION" };
  public static final String ARG_COLLECTION_DEST = "collection";
  public static final String ARG_COLLECTION_DEFAULT = null;
  public static final String ARG_COLLECTION_HELP = "Solr のコレクション名を指定 (SolrCloud) 例: collection1";

  /**
   * -q, --queueSize コマンドライン引数の定数
   */
  public static final String[] ARG_QUEUE_SIZE_FLAGS = { "-q", "--queueSize" };
  public static final String[] ARG_QUEUE_SIZE_METAVAR = { "QUEUE_SIZE" };
  public static final String ARG_QUEUE_SIZE_DEST = "queueSize";
  public static final int ARG_QUEUE_SIZE_DEFAULT = 1;
  public static final String ARG_QUEUE_SIZE_HELP = "ドキュメント更新時のキューサイズを指定";

  /**
   * -t, --threadCount コマンドライン引数の定数
   */
  public static final String[] ARG_THREAD_COUNT_FLAGS = { "-t", "--threadCount" };
  public static final String[] ARG_THREAD_COUNT_METAVAR = { "THREAD_COUNT" };
  public static final String ARG_THREAD_COUNT_DEST = "threadCount";
  public static final int ARG_THREAD_COUNT_DEFAULT = 1;
  public static final String ARG_THREAD_COUNT_HELP = "ドキュメント更新時のスレッド数を指定";

  /**
   * -r, --with-request コマンドライン引数の定数
   */
  public static final String[] ARG_WITH_REQUEST_FLAGS = { "-r", "--with-request" };
  public static final String ARG_WITH_REQUEST_DEST = "withRequest";
  public static final boolean ARG_WITH_REQUEST_DEFAULT = false;
  public static final String ARG_WITH_REQUEST_HELP = "実行結果に与えたコマンドライン引数を付与するかを指定";

  /**
   * -p, --pretty-print コマンドライン引数の定数
   */
  public static final String[] ARG_PRETTY_PRINT_FLAGS = { "-p", "--pretty-print" };
  public static final String ARG_PRETTY_PRINT_DEST = "prettyPrint";
  public static final boolean ARG_PRETTY_PRINT_DEFAULT = false;
  public static final String ARG_PRETTY_PRINT_HELP = "実行結果の JSON を整形するかを指定";

  /**
   * -v, --version コマンドライン引数の定数
   */
  public static final String[] ARG_VERSION_FLAGS = { "-v", "--version" };
  public static final String ARG_VERSION_DEST = "version";
  public static final String ARG_VERSION_DEFAULT = "0.1.0";
  public static final String ARG_VERSION_HELP = "バージョンを表示";

  public String name = NAME;
  public String help = HELP;

  public String solrUrl = ARG_SOLR_URL_DEFAULT;
  public String zookeeperHost = ARG_ZOOKEEPER_HOST_DEFAULT;
  public String zookeeperChroot = ARG_ZOOKEEPER_CHROOT_DEFAULT;
  public String collection = ARG_COLLECTION_DEFAULT;
  public int queueSize = ARG_QUEUE_SIZE_DEFAULT;
  public int threadCount = ARG_THREAD_COUNT_DEFAULT;
  public boolean withRequest = ARG_WITH_REQUEST_DEFAULT;
  public boolean prettyPrint = ARG_PRETTY_PRINT_DEFAULT;

  public SolrClient solrClient = null;

  public int status = STATUS_SUCCESS;
  public String message = SUCCESS_MESSAGE;
  public Map<String, Object> request = new TreeMap<String, Object>();
  public Map<String, Object> response = new TreeMap<String, Object>();

  /**
   * コンストラクター
   */
  public Command() {
    super();

    /*
     * コマンド名とヘルプをセット
     */
    name = NAME;
    help = HELP;
  }

  /**
   * preProcess は前処理を行うメソッドです。
   * 
   * @param parameters コマンドライン引数
   * @throws Exception
   */
  public void preProcess(Map<String, Object> parameters) throws Exception {
    /*
     * Solr の URL を設定
     */
    if (parameters.containsKey(ARG_SOLR_URL_DEST)) {
      solrUrl = (String) parameters.get(ARG_SOLR_URL_DEST);
    }

    /*
     * ZooKeeper の接続文字列を設定
     */
    if (parameters.containsKey(ARG_ZOOKEEPER_HOST_DEST)) {
      zookeeperHost = (String) parameters.get(ARG_ZOOKEEPER_HOST_DEST);
    }

    /*
     * znode を設定
     */
    if (parameters.containsKey(ARG_ZOOKEEPER_CHROOT_DEST)) {
      zookeeperChroot = (String) parameters.get(ARG_ZOOKEEPER_CHROOT_DEST);
    }

    /*
     * コレクション名を設定
     */
    if (parameters.containsKey(ARG_COLLECTION_DEST)) {
      collection = (String) parameters.get(ARG_COLLECTION_DEST);
    }

    /*
     * キューサイズを設定
     */
    if (parameters.containsKey(ARG_QUEUE_SIZE_DEST)) {
      queueSize = (Integer) parameters.get(ARG_QUEUE_SIZE_DEST);
    }

    /*
     * スレッド数を設定
     */
    if (parameters.containsKey(ARG_THREAD_COUNT_DEST)) {
      threadCount = (Integer) parameters.get(ARG_THREAD_COUNT_DEST);
    }

    /*
     * 実行結果に実行パラメータを含めるか設定
     */
    if (parameters.containsKey(ARG_WITH_REQUEST_DEST)) {
      withRequest = (Boolean) parameters.get(ARG_WITH_REQUEST_DEST);
    }

    /*
     * 実行結果を整形するか設定
     */
    if (parameters.containsKey(ARG_PRETTY_PRINT_DEST)) {
      prettyPrint = (Boolean) parameters.get(ARG_PRETTY_PRINT_DEST);
    }

    /*
     * Solr の URL が空かチェック
     */
    if (StringUtils.isNotEmpty(solrUrl)) {
      /*
       * コマンドをチェック
       */
      if (SearchCommand.NAME.equals(name)) {
        /*
         * search コマンドの場合，Solr の URL のカンマ (,) が含まれているかチェック
         */
        if (solrUrl.contains(",")) {
          /*
           * カンマ (,) が含まれていた場合，複数の Solr URL に分割
           */
          List<String> solrUrls = new ArrayList<String>();
          for (String s : solrUrl.split(",")) {
            if (StringUtils.isNotEmpty(s)) {
              solrUrls.add(s.trim());
            }
          }

          /*
           * 複数の Solr に対してロードバランスする LBHttpSolrClient オブジェクトを作成
           */
          LBHttpSolrClient lbHttpSolrClient = new LBHttpSolrClient.Builder()
              .withBaseSolrUrls(solrUrls.toArray(new String[0])).build();
          solrClient = lbHttpSolrClient;
        } else {
          /*
           * カンマ (,) を含んでいない場合，スタンドアローンの Solr へ接続する HttpSolrClient オブジェクトを作成
           */
          HttpSolrClient httpSolrClient = new HttpSolrClient.Builder(solrUrl).build();
          solrClient = httpSolrClient;
        }
      } else if (AddCommand.NAME.equals(name) || DeleteCommand.NAME.equals(name)) {
        /*
         * add または delete コマンドの場合，Solr の URL のカンマ (,) が含まれているかチェック
         */
        if (solrUrl.contains(",")) {
          /*
           * 更新系コマンドで，複数 Solr をサポートしないので，例外をスロー
           */
          throw new IllegalArgumentException(name + " does not support multiple Solr nodes.");
        }

        /*
         * 更新系コマンドでは，ConcurrentUpdateSolrClient オブジェクトを作成
         */
        ConcurrentUpdateSolrClient concurrentUpdateSolrClient =
            new ConcurrentUpdateSolrClient.Builder(solrUrl).withQueueSize(queueSize)
                .withThreadCount(threadCount).build();
        solrClient = concurrentUpdateSolrClient;
      }
    } else if (StringUtils.isNotEmpty(zookeeperHost)) {
      /*
       * Solr の URL が空で，ZooKeeper 接続文字列が空ではない場合，CloudSolrClient.Builder を作成
       */
      Builder builder = new CloudSolrClient.Builder();

      /*
       * ZooKeeper ホストにカンマ (,) が含まれているかチェック
       */
      if (zookeeperHost.contains(",")) {
        /*
         * カンマ (,) が含まれていた場合，複数の ZooKeeper ホストに分割
         */
        List<String> zookeeperHosts = new ArrayList<String>();
        for (String z : zookeeperHost.split(",")) {
          if (StringUtils.isNotEmpty(z)) {
            zookeeperHosts.add(z.trim());
          }
        }

        /*
         * 複数の ZooKeeper へ接続するため，ZooKeeper ホストのリストを設定
         */
        builder.withZkHost(zookeeperHosts);
      } else {
        /*
         * カンマ (,) が含まれていない場合，1つの ZooKeeper ホストを設定
         */
        builder.withZkHost(zookeeperHost);
      }

      /*
       * znode が指定されていたら設定
       */
      if (StringUtils.isNotEmpty(zookeeperChroot)) {
        builder.withZkChroot(zookeeperChroot);
      }

      /*
       * CloudSolrClient オブジェクトを生成する
       */
      CloudSolrClient cloudSolrClient = builder.build();
      if (StringUtils.isNotEmpty(collection)) {
        cloudSolrClient.setDefaultCollection(collection);
      }
      solrClient = cloudSolrClient;
    } else {
      /*
       * 引数が少ないということで例外をスロー
       */
      throw new Exception("too few arguments");
    }
  }

  /**
   * mainProcess はメイン処理を行うメソッドです。
   * 
   * @param parameters コマンドライン引数
   * @throws Exception
   */
  public void mainProcess(Map<String, Object> parameters) throws Exception {
    /*
     * 何もしないで成功のステータスとメッセージをセット
     */
    status = STATUS_SUCCESS;
    message = SUCCESS_MESSAGE;
  }

  /**
   * output はコマンドの実行結果を標準出力に出力するメソッドです。
   * 
   * @param parameters コマンドライン引数
   * @throws Exception
   */
  public void output(Map<String, Object> parameters) throws Exception {
    /*
     * 実行結果を格納する Map オブジェクトを作成
     */
    Map<String, Object> result = new LinkedHashMap<String, Object>();

    /*
     * コマンドライン引数で，指定されたパラメータを出力するように指定していたら，コマンド実行時に指定されたパラメーターをセット
     */
    if (withRequest) {
      request.put("command", name);
      request.put("parameters", new TreeMap<String, Object>(parameters));
      result.put("request", request);
    }

    /*
     * コマンドの実行結果をセット
     */
    response.put("status", status);
    response.put("message", message);
    result.put("response", response);

    /*
     * コマンドライン引数で，実行結果を整形するように指定されていたら，整形して標準出力へ出力
     */
    ObjectMapper mapper = new ObjectMapper();
    if (prettyPrint) {
      System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
    } else {
      System.out.println(mapper.writeValueAsString(result));
    }
  }

  /**
   * postProcess は後処理をするメソッドです。
   * 
   * @param parameters コマンドライン引数
   * @throws Exception
   */
  public void postProcess(Map<String, Object> parameters) throws Exception {
    /*
     * 使用した SolrClient オブジェクトをクローズ
     */
    if (solrClient != null) {
      solrClient.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see CommandImpl#execute(java.util.Map)
   */
  @Override
  public int execute(Map<String, Object> parameters) throws Exception {
    try {
      /*
       * 前処理
       */
      preProcess(parameters);

      /*
       * メイン処理
       */
      mainProcess(parameters);

      /*
       * 実行結果の出力
       */
      output(parameters);
    } catch (Exception e) {
      /*
       * エラーステータスとエラーメッセージをセット
       */
      status = STATUS_ERROR;
      message = e.getMessage();
    } finally {
      /*
       * 後処理
       */
      postProcess(parameters);
    }

    /*
     * コマンドのステータスを返却
     */
    return status;
  }

}