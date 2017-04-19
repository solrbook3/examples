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

/**
 * CommandImpl インタフェース。サブコマンドはこのクラスを実装します。
 */
public interface CommandImpl {

  /**
   * サプコマンドを実行するときのエントリーポイントとなるメソッドです。
   * 
   * @param parameters コマンドライン引数
   * @return コマンドの戻り値
   * @throws Exception
   */
  int execute(Map<String, Object> parameters) throws Exception;

}
