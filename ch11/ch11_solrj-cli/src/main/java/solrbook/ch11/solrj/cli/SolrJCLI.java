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
package solrbook.ch11.solrj.cli;

import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;

import java.util.Map;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import solrbook.ch11.solrj.cli.command.AddCommand;
import solrbook.ch11.solrj.cli.command.Command;
import solrbook.ch11.solrj.cli.command.CommandImpl;
import solrbook.ch11.solrj.cli.command.DeleteCommand;
import solrbook.ch11.solrj.cli.command.SearchCommand;

/**
 * SolrJ のサンプルアプリケーションです。
 */
public class SolrJCLI {
  private static final String SUBCOMMAND_TITLE = "subcommands";
  private static final String SUBCOMMAND_DESCRIPTION = "avairable subcommands";
  private static final String SUBCOMMAND_DEST = "command";
  private static final String SUBCOMMAND_METAVAR = "SUBCOMMAND";

  /**
   * サンプルアプリケーションのエントリーポイントです。
   * 
   * @param args コマンドライン引数
   */
  public static void main(String[] args) {
    /*
     * メインコマンドのパーサーを作成
     */
    ArgumentParser mainCommandArgumentParser = ArgumentParsers
        .newArgumentParser(SolrJCLI.class.getSimpleName()).version(Command.ARG_VERSION_DEFAULT);
    mainCommandArgumentParser.description(Command.HELP);
    mainCommandArgumentParser.addArgument(Command.ARG_SOLR_URL_FLAGS)
        .metavar(Command.ARG_SOLR_URL_METAVAR).dest(Command.ARG_SOLR_URL_DEST).type(String.class)
        .setDefault(Command.ARG_SOLR_URL_DEFAULT).help(Command.ARG_SOLR_URL_HELP);
    mainCommandArgumentParser.addArgument(Command.ARG_ZOOKEEPER_HOST_FLAGS)
        .metavar(Command.ARG_ZOOKEEPER_HOST_METAVAR).dest(Command.ARG_ZOOKEEPER_HOST_DEST)
        .type(String.class).setDefault(Command.ARG_ZOOKEEPER_HOST_DEFAULT)
        .help(Command.ARG_ZOOKEEPER_HOST_HELP);
    mainCommandArgumentParser.addArgument(Command.ARG_ZOOKEEPER_CHROOT_FLAGS)
        .metavar(Command.ARG_ZOOKEEPER_CHROOT_METAVAR).dest(Command.ARG_ZOOKEEPER_CHROOT_DEST)
        .type(String.class).setDefault(Command.ARG_ZOOKEEPER_CHROOT_DEFAULT)
        .help(Command.ARG_ZOOKEEPER_CHROOT_HELP);
    mainCommandArgumentParser.addArgument(Command.ARG_COLLECTION_FLAGS)
        .metavar(Command.ARG_COLLECTION_METAVAR).dest(Command.ARG_COLLECTION_DEST)
        .type(String.class).setDefault(Command.ARG_COLLECTION_DEFAULT)
        .help(Command.ARG_COLLECTION_HELP);
    mainCommandArgumentParser.addArgument(Command.ARG_QUEUE_SIZE_FLAGS)
        .metavar(Command.ARG_QUEUE_SIZE_METAVAR).dest(Command.ARG_QUEUE_SIZE_DEST)
        .type(Integer.class).setDefault(Command.ARG_QUEUE_SIZE_DEFAULT)
        .help(Command.ARG_QUEUE_SIZE_HELP);
    mainCommandArgumentParser.addArgument(Command.ARG_THREAD_COUNT_FLAGS)
        .metavar(Command.ARG_THREAD_COUNT_METAVAR).dest(Command.ARG_THREAD_COUNT_DEST)
        .type(Integer.class).setDefault(Command.ARG_THREAD_COUNT_DEFAULT)
        .help(Command.ARG_THREAD_COUNT_HELP);
    mainCommandArgumentParser.addArgument(Command.ARG_WITH_REQUEST_FLAGS)
        .dest(Command.ARG_WITH_REQUEST_DEST).type(Boolean.class)
        .setDefault(Command.ARG_WITH_REQUEST_DEFAULT).action(storeTrue())
        .help(Command.ARG_WITH_REQUEST_HELP);
    mainCommandArgumentParser.addArgument(Command.ARG_PRETTY_PRINT_FLAGS)
        .dest(Command.ARG_PRETTY_PRINT_DEST).type(Boolean.class)
        .setDefault(Command.ARG_PRETTY_PRINT_DEFAULT).action(storeTrue())
        .help(Command.ARG_PRETTY_PRINT_HELP);
    mainCommandArgumentParser.addArgument(Command.ARG_VERSION_FLAGS).dest(Command.ARG_VERSION_DEST)
        .action(Arguments.version()).help(Command.ARG_VERSION_HELP);

    /*
     * サブコマンドのパーサーを追加
     */
    Subparsers commandSubpersers = mainCommandArgumentParser.addSubparsers().title(SUBCOMMAND_TITLE)
        .description(SUBCOMMAND_DESCRIPTION).metavar(SUBCOMMAND_METAVAR);

    /*
     * add コマンドのパーサーをサブコマンドのパーサーへ登録
     */
    Subparser addCommandSubarser = commandSubpersers.addParser(AddCommand.NAME)
        .help(AddCommand.HELP).setDefault(SUBCOMMAND_DEST, new AddCommand());
    addCommandSubarser.description(AddCommand.HELP);
    addCommandSubarser.addArgument(AddCommand.ARG_DOCUMENT_JSON_FLAGS)
        .metavar(AddCommand.ARG_DOCUMENT_JSON_METAVAR).dest(AddCommand.ARG_DOCUMENT_JSON_DEST)
        .type(String.class).setDefault(AddCommand.ARG_DOCUMENT_JSON_DEFAULT)
        .help(AddCommand.ARG_DOCUMENT_JSON_HELP);

    /*
     * delete コマンドのパーサーをサブコマンドのパーサーへ登録
     */
    Subparser deleteCommandSubparser = commandSubpersers.addParser(DeleteCommand.NAME)
        .help(DeleteCommand.HELP).setDefault(SUBCOMMAND_DEST, new DeleteCommand());
    deleteCommandSubparser.description(DeleteCommand.HELP);
    deleteCommandSubparser.addArgument(DeleteCommand.ARG_ID_FLAGS)
        .metavar(DeleteCommand.ARG_ID_METAVAR).type(String.class).dest(DeleteCommand.ARG_ID_DEST)
        .setDefault(DeleteCommand.ARG_ID_DEFAULT).help(DeleteCommand.ARG_ID_HELP);

    /**
     * search コマンドのパーサーをサブコマンドのパーサーへ登録
     */
    Subparser searchCommandSubparser = commandSubpersers.addParser(SearchCommand.NAME)
        .help(SearchCommand.HELP).setDefault(SUBCOMMAND_DEST, new SearchCommand());
    searchCommandSubparser.description(SearchCommand.HELP);
    searchCommandSubparser.addArgument(SearchCommand.ARG_QUERY_STRING_FLAGS)
        .metavar(SearchCommand.ARG_QUERY_STRING_METAVAR).dest(SearchCommand.ARG_QUERY_STRING_DEST)
        .type(String.class).setDefault(SearchCommand.ARG_QUERY_STRING_DEFAULT)
        .help(SearchCommand.ARG_QUERY_STRING_HELP);
    searchCommandSubparser.addArgument(SearchCommand.ARG_START_FLAGS)
        .metavar(SearchCommand.ARG_START_METAVAR).dest(SearchCommand.ARG_START_DEST)
        .type(Integer.class).setDefault(SearchCommand.ARG_START_DEFAULT)
        .help(SearchCommand.ARG_START_HELP);
    searchCommandSubparser.addArgument(SearchCommand.ARG_ROWS_FLAGS)
        .metavar(SearchCommand.ARG_ROWS_METAVAR).dest(SearchCommand.ARG_ROWS_DEST)
        .type(Integer.class).setDefault(SearchCommand.ARG_ROWS_DEFAULT)
        .help(SearchCommand.ARG_ROWS_HELP);
    searchCommandSubparser.addArgument(SearchCommand.ARG_SORT_FIELD_FLAGS)
        .metavar(SearchCommand.ARG_SORT_FIELD_METAVAR).dest(SearchCommand.ARG_SORT_FIELD_DEST)
        .type(String.class).setDefault(SearchCommand.ARG_SORT_FIELD_DEFAULT)
        .help(SearchCommand.ARG_SORT_FIELD_HELP);
    searchCommandSubparser.addArgument(SearchCommand.ARG_SORT_ORDER_FLAGS)
        .metavar(SearchCommand.ARG_SORT_ORDER_METAVAR).dest(SearchCommand.ARG_SORT_ORDER_DEST)
        .type(String.class).setDefault(SearchCommand.ARG_SORT_ORDER_DEFAULT)
        .help(SearchCommand.ARG_SORT_ORDER_HELP);
    searchCommandSubparser.addArgument(SearchCommand.ARG_FIELD_LIST_FLAGS)
        .metavar(SearchCommand.ARG_FIELD_LIST_METAVAR).dest(SearchCommand.ARG_FIELD_LIST_DEST)
        .type(String.class).setDefault(SearchCommand.ARG_FIELD_LIST_DEFAULT)
        .help(SearchCommand.ARG_FIELD_LIST_HELP);

    /**
     * コマンドの実行
     */
    int status = Command.STATUS_SUCCESS;
    try {
      /*
       * メインコマンドライン引数のパース
       */
      Namespace namespace = mainCommandArgumentParser.parseArgs(args);

      /*
       * サブコマンドを取得
       */
      CommandImpl cmd = namespace.get(SUBCOMMAND_DEST);

      /*
       * コマンドライン引数を取得
       */
      Map<String, Object> parameters = namespace.getAttrs();

      /*
       * 不要なコマンドライン引数を除去
       */
      parameters.remove(SUBCOMMAND_DEST);
      parameters.remove(Command.ARG_VERSION_DEST);

      /*
       * サブコマンドを実行
       */
      status = cmd.execute(parameters);
    } catch (ArgumentParserException e) {
      /*
       * コマンドの実行ステータスにエラーステータスをセットしヘルプ表示
       */
      mainCommandArgumentParser.handleError(e);
      status = Command.STATUS_ERROR;
    } catch (Exception e) {
      /*
       * コマンドの実行ステータスにエラーステータスをセット
       */
      e.printStackTrace();
      status = Command.STATUS_ERROR;
    } finally {
      /*
       * コマンドの実行ステータスをセットしてアプリケーションを終了
       */
      System.exit(status);
    }
  }
}
