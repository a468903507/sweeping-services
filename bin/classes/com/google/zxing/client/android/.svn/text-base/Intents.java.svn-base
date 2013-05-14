/*
 * Copyright (C) 2008 ZXing authors
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
 * 意图常量类
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class Intents {
	private Intents() {
	}

	/**
	 * 扫描
	 * 
	 * @author Administrator
	 * 
	 */
	public static final class Scan {
		/**
		 * 通过这个意图打开条码扫描,找到一个条形码,然后返回结果。
		 */
		public static final String ACTION = "com.google.zxing.client.android.SCAN";

		/**
		 * 扫描模式，解码所有条形码
		 */
		public static final String MODE = "SCAN_MODE";

		/**
		 * 解码商品格式模式
		 */
		public static final String PRODUCT_MODE = "PRODUCT_MODE";

		/**
		 * 解码一维码模式.
		 */
		public static final String ONE_D_MODE = "ONE_D_MODE";

		/**
		 * 解码二维码模式
		 */
		public static final String QR_CODE_MODE = "QR_CODE_MODE";

		/**
		 * 解码DM码模式
		 */
		public static final String DATA_MATRIX_MODE = "DATA_MATRIX_MODE";

		/**
		 * 用逗号分隔的格式来扫描
		 */
		public static final String FORMATS = "SCAN_FORMATS";

		/**
		 * @see com.google.zxing.DecodeHintType#CHARACTER_SET
		 */
		public static final String CHARACTER_SET = "CHARACTER_SET";

		/**
		 * 可选参数指定的宽度
		 */
		public static final String WIDTH = "SCAN_WIDTH";
		/**
		 * 可选参数指定的高度
		 */
		public static final String HEIGHT = "SCAN_HEIGHT";

		/**
		 * 结果显示时间,以毫秒为单位后暂停 a long, not an integer! For example: 1000L, not
		 * 1000.
		 */
		public static final String RESULT_DISPLAY_DURATION_MS = "RESULT_DISPLAY_DURATION_MS";

		/**
		 * 提示显示屏幕扫描时的意图 {@link String}.
		 */
		public static final String PROMPT_MESSAGE = "PROMPT_MESSAGE";

		/**
		 * 扫描结果 
		 * If a barcode is found, Barcodes returns
		 * {@link android.app.Activity#RESULT_OK} to
		 * {@link android.app.Activity#onActivityResult(int, int, android.content.Intent)}
		 * of the app which requested the scan via
		 * {@link android.app.Activity#startActivityForResult(android.content.Intent, int)}
		 * The barcodes contents can be retrieved with
		 * {@link android.content.Intent#getStringExtra(String)}. If the user
		 * presses Back, the result code will be
		 * {@link android.app.Activity#RESULT_CANCELED}.
		 */
		public static final String RESULT = "SCAN_RESULT";

		/**
		 * 扫描结果格式
		 * Call {@link android.content.Intent#getStringExtra(String)}
		 * with {@link #RESULT_FORMAT} to determine which barcode format was
		 * found. See {@link com.google.zxing.BarcodeFormat} for possible
		 * values.
		 */
		public static final String RESULT_FORMAT = "SCAN_RESULT_FORMAT";

		/**
		 * 扫描结果格式 
		 * Call {@link android.content.Intent#getStringExtra(String)}
		 * with {@link #RESULT_UPC_EAN_EXTENSION} to return the content of any
		 * UPC extension barcode that was also found. Only applicable to
		 * {@link com.google.zxing.BarcodeFormat#UPC_A} and
		 * {@link com.google.zxing.BarcodeFormat#EAN_13} formats.
		 */
		public static final String RESULT_UPC_EAN_EXTENSION = "SCAN_RESULT_UPC_EAN_EXTENSION";

		/**
		 * 扫描结果格式 
		 * Call {@link android.content.Intent#getByteArrayExtra(String)}
		 * with {@link #RESULT_BYTES} to get a {@code byte[]} of raw bytes in
		 * the barcode, if available.
		 */
		public static final String RESULT_BYTES = "SCAN_RESULT_BYTES";

		/**
		 * 扫描结果导向 
		 * Key for the value of
		 * {@link com.google.zxing.ResultMetadataType#ORIENTATION}, if
		 * available. Call
		 * {@link android.content.Intent#getIntArrayExtra(String)} with
		 * {@link #RESULT_ORIENTATION}.
		 */
		public static final String RESULT_ORIENTATION = "SCAN_RESULT_ORIENTATION";

		/**
		 * 扫描结果误差校正水平 
		 * Key for the value of
		 * {@link com.google.zxing.ResultMetadataType#ERROR_CORRECTION_LEVEL},
		 * if available. Call
		 * {@link android.content.Intent#getStringExtra(String)} with
		 * {@link #RESULT_ERROR_CORRECTION_LEVEL}.
		 */
		public static final String RESULT_ERROR_CORRECTION_LEVEL = "SCAN_RESULT_ERROR_CORRECTION_LEVEL";

		/**
		 * 扫描结果字节段 
		 * Prefix for keys that map to the values of
		 * {@link com.google.zxing.ResultMetadataType#BYTE_SEGMENTS}, if
		 * available. The actual values will be set under a series of keys
		 * formed by adding 0, 1, 2, ... to this prefix. So the first byte
		 * segment is under key "SCAN_RESULT_BYTE_SEGMENTS_0" for example. Call
		 * {@link android.content.Intent#getByteArrayExtra(String)} with these
		 * keys.
		 */
		public static final String RESULT_BYTE_SEGMENTS_PREFIX = "SCAN_RESULT_BYTE_SEGMENTS_";

		/**
		 * 历史保存
		 * Setting this to false will not save scanned codes in the
		 * history. Specified as a {@code boolean}.
		 */
		public static final String SAVE_HISTORY = "SAVE_HISTORY";

		private Scan() {
		}
	}

	public static final class History {
		// 项目编号
		public static final String ITEM_NUMBER = "ITEM_NUMBER";

		private History() {
		}
	}

	/**
	 * 编码
	 * 
	 * @author Administrator
	 * 
	 */
	public static final class Encode {
		/**
		 * 通过这个意图把一块数据作为一个二维码和显示
		 */
		public static final String ACTION = "com.google.zxing.client.android.ENCODE";

		/**
		 * 需要编码的数据 The data to encode. Use
		 * {@link android.content.Intent#putExtra(String, String)} or
		 * {@link android.content.Intent#putExtra(String, android.os.Bundle)},
		 * depending on the type and format specified. Non-QR Code formats
		 * should just use a String here. For QR Code, see Contents for details.
		 */
		public static final String DATA = "ENCODE_DATA";

		/**
		 * 需要编码的类型 The type of data being supplied if the format is QR Code. Use
		 * {@link android.content.Intent#putExtra(String, String)} with one of
		 * {@link Contents.Type}.
		 */
		public static final String TYPE = "ENCODE_TYPE";

		/**
		 * 条码的显示格式 The barcode format to be displayed. If this isn't specified
		 * or is blank, it defaults to QR Code. Use
		 * {@link android.content.Intent#putExtra(String, String)}, where format
		 * is one of {@link com.google.zxing.BarcodeFormat}.
		 */
		public static final String FORMAT = "ENCODE_FORMAT";

		/**
		 * 是否显示为本内容，false则不显示 Normally the contents of the barcode are displayed
		 * to the user in a TextView. Setting this boolean to false will hide
		 * that TextView, showing only the encode barcode.
		 */
		public static final String SHOW_CONTENTS = "ENCODE_SHOW_CONTENTS";

		private Encode() {
		}
	}

	/**
	 * 搜索书本内容
	 * 
	 * @author Administrator
	 * 
	 */
	public static final class SearchBookContents {
		/**
		 * 通过这个意图使用谷歌搜索来搜索图书
		 */
		public static final String ACTION = "com.google.zxing.client.android.SEARCH_BOOK_CONTENTS";

		/**
		 * 书本的ISBN号
		 */
		public static final String ISBN = "ISBN";

		/**
		 * 文本搜索
		 */
		public static final String QUERY = "QUERY";

		private SearchBookContents() {
		}
	}

	/**
	 * Wifi连接
	 * 
	 * @author Administrator
	 * 
	 */
	public static final class WifiConnect {
		/**
		 * 通过这个意图触发连接到wi - fi网络
		 */
		public static final String ACTION = "com.google.zxing.client.android.WIFI_CONNECT";

		/**
		 * 网络链接SSID
		 */
		public static final String SSID = "SSID";

		/**
		 * 网络连接类型
		 */
		public static final String TYPE = "TYPE";

		/**
		 * 网络连接密码
		 */
		public static final String PASSWORD = "PASSWORD";

		private WifiConnect() {
		}
	}

	/**
	 * 制作二维码页面
	 * 
	 * @author Administrator
	 * 
	 */
	public static final class Share {
		/**
		 * 通过这个意图打开二维码分享生成页面
		 */
		public static final String ACTION = "com.google.zxing.client.android.SHARE";

		private Share() {
		}
	}
}
