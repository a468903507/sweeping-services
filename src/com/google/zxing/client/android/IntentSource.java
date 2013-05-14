/*
 * Copyright (C) 2011 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;
/**
 * 意图类型枚举类
 * @author linanjiang@guoxinad.com.cn
 *
 */
enum IntentSource {
/** 当前应用程序的意图*/
  NATIVE_APP_INTENT,
  /** 产品搜索链接*/
  PRODUCT_SEARCH_LINK,
  /** ZXING链接*/
  ZXING_LINK,
  /** 没有*/
  NONE

}
