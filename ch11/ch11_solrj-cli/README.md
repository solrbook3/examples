# solrj-cli

## プロジェクトのビルドとインストール方法

```
$ cd ${EXAMPLES}/ch11/ch11_solrj-cli
$ ant package
$ cp ${EXAMPLES}/ch11/ch11_solrj-cli/package/solrj-cli-0.1.0.tgz ${HOME}/.
$ tar -C ${HOME} -xf ${HOME}/solrj-cli-0.1.0.tgz
$ cd ${HOME}/solrj-cli-0.1.0
```

## 使い方

### メインコマンドのヘルプ表示

```
$ ./bin/solrj-cli.sh -h
usage: SolrJCLI [-h] [-s SOLR_URL] [-z ZOOKEEPER_HOST] [-R ZOOKEEPER_CHROOT] [-c COLLECTION]
                [-q QUEUE_SIZE] [-t THREAD_COUNT] [-r] [-p] [-v] SUBCOMMAND ...

SolrJ のサンプル CLI

optional arguments:
  -h, --help             show this help message and exit
  -s SOLR_URL, --solr-url SOLR_URL
                         Solr の URL を指定 (スタンドアローン) 例: http://localhost:8983/solr/collection1
  -z ZOOKEEPER_HOST, --zookeeper-host ZOOKEEPER_HOST
                         ZooKeeper 接続文字列を指定 (SolrCloud) 例: localhost:2181
  -R ZOOKEEPER_CHROOT, --zookeeper-chroot ZOOKEEPER_CHROOT
                         SolrCloud のクラスター情報が格納されている ZooKeeper 上の znode を指定 (SolrCloud) 例: /solr
  -c COLLECTION, --collection COLLECTION
                         Solr のコレクション名を指定 (SolrCloud) 例: collection1
  -q QUEUE_SIZE, --queueSize QUEUE_SIZE
                         ドキュメント更新時のキューサイズを指定
  -t THREAD_COUNT, --threadCount THREAD_COUNT
                         ドキュメント更新時のスレッド数を指定
  -r, --with-request     実行結果に与えたコマンドライン引数を付与するかを指定
  -p, --pretty-print     実行結果の JSON を整形するかを指定
  -v, --version          バージョンを表示

subcommands:
  avairable subcommands

  SUBCOMMAND
    add                  ドキュメントの追加/更新リクエストを Solr へ送信
    delete               ドキュメントの削除リクエストを Solr へ送信
    search               ドキュメントの検索リクエストを Solr へ送信
```

### add コマンド

#### add コマンドのヘルプ表示

```
$ ./bin/solrj-cli.sh add -h
usage: SolrJCLI add [-h] DOCUMENT_JSON

ドキュメントの追加/更新リクエストを Solr へ送信

positional arguments:
  DOCUMENT_JSON          追加/更新するドキュメント (JSON) を指定

optional arguments:
  -h, --help             show this help message and exit
```

#### add コマンドの実行 (スタンドアローン)

```
$ ./bin/solrj-cli.sh -s http://localhost:8983/solr/collection1 -p -r add '{ "id" : { "value" : "1" }, "title_txt_ja" : { "value" : "Apache Solr" }, "description_txt_ja" : { "value" : "Apache Solr はオープンソースの全文検索サーバーです。" } }'
{
  "request" : {
    "command" : "add",
    "parameters" : {
      "collection" : null,
      "documentJSON" : "{ \"id\" : { \"value\" : \"1\" }, \"title_txt_ja\" : { \"value\" : \"Apache Solr\" }, \"description_txt_ja\" : { \"value\" : \"Apache Solr はオープンソースの全文検索サーバーです。\" } }",
      "prettyPrint" : true,
      "queueSize" : 1,
      "solrUrl" : "http://localhost:8983/solr/collection1",
      "threadCount" : 1,
      "withRequest" : true,
      "zookeeperChroot" : null,
      "zookeeperHost" : null
    }
  },
  "response" : {
    "message" : "success",
    "status" : 0
  }
}
```

#### add コマンドの実行 (SolrCloud)

```
$ ./bin/solrj-cli.sh -z localhost:2181 -R /solr -c collection1 -p -r add '{ "id" : { "value" : "1" }, "title_txt_ja" : { "value" : "Apache Solr" }, "description_txt_ja" : { "value" : "Apache Solr はオープンソースの全文検索サーバーです。" } }'
{
  "request" : {
    "command" : "add",
    "parameters" : {
      "collection" : "collection1",
      "documentJSON" : "{ \"id\" : { \"value\" : \"1\" }, \"title_txt_ja\" : { \"value\" : \"Apache Solr\" }, \"description_txt_ja\" : { \"value\" : \"Apache Solr はオープンソースの全文検索サーバーです。\" } }",
      "prettyPrint" : true,
      "solrUrl" : null,
      "withRequest" : true,
      "zookeeperChroot" : "/solr",
      "zookeeperHost" : "localhost:2181"
    }
  },
  "response" : {
    "message" : "success",
    "status" : 0
  }
}
```

### search コマンド

#### search コマンドのヘルプ表示

```
$ ./bin/solrj-cli.sh search -h
usage: SolrJCLI search [-h] [-q QUERY_STRING] [-s START] [-r ROWS] [-S SORT_FIELD] [-o SORT_ORDER] [-l FIELD_LIST]

ドキュメントの検索リクエストを Solr へ送信

optional arguments:
  -h, --help             show this help message and exit
  -q QUERY_STRING, --query-string QUERY_STRING
                         Solr のクエリ文字列を指定
  -s START, --start START
                         取得する検索結果の開始位置を指定
  -r ROWS, --rows ROWS   取得する検索結果の件数を指定
  -S SORT_FIELD, --sort-field SORT_FIELD
                         ソートするフィールド名を指定
  -o SORT_ORDER, --sort-order SORT_ORDER
                         ソート順を指定 (asc | desc)
  -l FIELD_LIST, --field-list FIELD_LIST
                         取得する検索結果に含めるドキュメントのフィールドを指定 例: title,description
```

#### search コマンドの実行 (スタンドアローン)

```
$ ./bin/solrj-cli.sh -s http://localhost:8983/solr/collection1 -p -r search -q 'description_txt_ja:検索'
{
  "request" : {
    "command" : "search",
    "parameters" : {
      "collection" : null,
      "fieldList" : "*,score",
      "prettyPrint" : true,
      "queryString" : "description_txt_ja:検索",
      "queueSize" : 1,
      "rows" : 10,
      "solrUrl" : "http://localhost:8983/solr/collection1",
      "sortField" : null,
      "sortOrder" : null,
      "start" : 0,
      "threadCount" : 1,
      "withRequest" : true,
      "zookeeperChroot" : null,
      "zookeeperHost" : null
    }
  },
  "response" : {
    "QTime" : 3,
    "maxScore" : 0.28582606,
    "message" : "success",
    "numFound" : 1,
    "result" : [ {
      "title_txt_ja" : "Apache Solr",
      "score" : 0.28582606,
      "description_txt_ja" : "Apache Solr はオープンソースの全文検索サーバーです。",
      "_version_" : 1551047562157359104,
      "id" : "1"
    } ],
    "status" : 0
  }
}
```

#### search コマンドの実行 (ロードバランス)

```
$ ./bin/solrj-cli.sh -s "http://localhost:8983/solr/collection1,http://localhost:8985/solr/collection1" -p -r search -q 'description_txt_ja:検索'
{
  "request" : {
    "command" : "search",
    "parameters" : {
      "collection" : null,
      "fieldList" : "*,score",
      "prettyPrint" : true,
      "queryString" : "description_txt_ja:検索",
      "queueSize" : 1,
      "rows" : 10,
      "solrUrl" : "http://localhost:8983/solr/collection1,http://localhost:8985/solr/collection1",
      "sortField" : null,
      "sortOrder" : null,
      "start" : 0,
      "threadCount" : 1,
      "withRequest" : true,
      "zookeeperChroot" : null,
      "zookeeperHost" : null
    }
  },
  "response" : {
    "QTime" : 3,
    "maxScore" : 0.28582606,
    "message" : "success",
    "numFound" : 1,
    "result" : [ {
      "title_txt_ja" : "Apache Solr",
      "score" : 0.28582606,
      "description_txt_ja" : "Apache Solr はオープンソースの全文検索サーバーです。",
      "_version_" : 1551047562157359104,
      "id" : "1"
    } ],
    "status" : 0
  }
}
```

#### search コマンドの実行 (SolrCloud)

```
$ ./bin/solrj-cli.sh -z localhost:2181 -R /solr -c collection1 -p -r search -q 'description_txt_ja:検索'
{
  "request" : {
    "command" : "search",
    "parameters" : {
      "collection" : "collection1",
      "fieldList" : "*,score",
      "prettyPrint" : true,
      "queryString" : "description_txt_ja:検索",
      "queueSize" : 1,
      "rows" : 10,
      "solrUrl" : null,
      "sortField" : null,
      "sortOrder" : null,
      "start" : 0,
      "threadCount" : 1,
      "withRequest" : true,
      "zookeeperChroot" : "/solr",
      "zookeeperHost" : "localhost:2181"
    }
  },
  "response" : {
    "QTime" : 3,
    "maxScore" : 0.28582606,
    "message" : "success",
    "numFound" : 1,
    "result" : [ {
      "title_txt_ja" : "Apache Solr",
      "score" : 0.28582606,
      "description_txt_ja" : "Apache Solr はオープンソースの全文検索サーバーです。",
      "_version_" : 1551047562157359104,
      "id" : "1"
    } ],
    "status" : 0
  }
}
```

### delete コマンド

#### delete コマンドのヘルプ表示

```
$ ./bin/solrj-cli.sh delete -h
usage: SolrJCLI delete [-h] ID

ドキュメントの削除リクエストを Solr へ送信

positional arguments:
  ID                     削除するドキュメントの ID を指定

optional arguments:
  -h, --help             show this help message and exit
```

#### delete コマンドの実行 (スタンドアローン)

```
$ ./bin/solrj-cli.sh -s http://localhost:8983/solr/collection1 -p -r delete 1
{
  "request" : {
    "command" : "delete",
    "parameters" : {
      "collection" : null,
      "id" : "1",
      "prettyPrint" : true,
      "queueSize" : 1,
      "solrUrl" : "http://localhost:8983/solr/collection1",
      "threadCount" : 1,
      "withRequest" : true,
      "zookeeperChroot" : null,
      "zookeeperHost" : null
    }
  },
  "response" : {
    "message" : "success",
    "status" : 0
  }
}

#### delete コマンドの実行 (SolrCloud)

```
$ ./bin/solrj-cli.sh -z localhost:2181 -R /solr -c collection1 -p -r delete 1
{
  "request" : {
    "command" : "delete",
    "parameters" : {
      "collection" : "collection1",
      "id" : "1",
      "prettyPrint" : true,
      "queueSize" : 1,
      "solrUrl" : null,
      "threadCount" : 1,
      "withRequest" : true,
      "zookeeperChroot" : "/solr",
      "zookeeperHost" : "localhost:2181"
    }
  },
  "response" : {
    "message" : "success",
    "status" : 0
  }
}

#### search コマンドで削除できたか確認

$ ./bin/solrj-cli.sh -s http://localhost:8983/solr/collection1 -p -r search -q 'id:1'
{
  "request" : {
    "command" : "search",
    "parameters" : {
      "collection" : null,
      "fieldList" : "*,score",
      "prettyPrint" : true,
      "queryString" : "id:1",
      "queueSize" : 1,
      "rows" : 10,
      "solrUrl" : "http://localhost:8983/solr/collection1",
      "sortField" : null,
      "sortOrder" : null,
      "start" : 0,
      "threadCount" : 1,
      "withRequest" : true,
      "zookeeperChroot" : null,
      "zookeeperHost" : null
    }
  },
  "response" : {
    "QTime" : 2,
    "maxScore" : 0.0,
    "message" : "success",
    "numFound" : 0,
    "result" : [ ],
    "status" : 0
  }
}
```
