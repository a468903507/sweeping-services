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
 * ��ͼ������
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class Intents {
	private Intents() {
	}

	/**
	 * ɨ��
	 * 
	 * @author Administrator
	 * 
	 */
	public static final class Scan {
		/**
		 * ͨ�������ͼ������ɨ��,�ҵ�һ��������,Ȼ�󷵻ؽ����
		 */
		public static final String ACTION = "com.google.zxing.client.android.SCAN";

		/**
		 * ɨ��ģʽ����������������
		 */
		public static final String MODE = "SCAN_MODE";

		/**
		 * ������Ʒ��ʽģʽ
		 */
		public static final String PRODUCT_MODE = "PRODUCT_MODE";

		/**
		 * ����һά��ģʽ.
		 */
		public static final String ONE_D_MODE = "ONE_D_MODE";

		/**
		 * �����ά��ģʽ
		 */
		public static final String QR_CODE_MODE = "QR_CODE_MODE";

		/**
		 * ����DM��ģʽ
		 */
		public static final String DATA_MATRIX_MODE = "DATA_MATRIX_MODE";

		/**
		 * �ö��ŷָ��ĸ�ʽ��ɨ��
		 */
		public static final String FORMATS = "SCAN_FORMATS";

		/**
		 * @see com.google.zxing.DecodeHintType#CHARACTER_SET
		 */
		public static final String CHARACTER_SET = "CHARACTER_SET";

		/**
		 * ��ѡ����ָ���Ŀ��
		 */
		public static final String WIDTH = "SCAN_WIDTH";
		/**
		 * ��ѡ����ָ���ĸ߶�
		 */
		public static final String HEIGHT = "SCAN_HEIGHT";

		/**
		 * �����ʾʱ��,�Ժ���Ϊ��λ����ͣ a long, not an integer! For example: 1000L, not
		 * 1000.
		 */
		public static final String RESULT_DISPLAY_DURATION_MS = "RESULT_DISPLAY_DURATION_MS";

		/**
		 * ��ʾ��ʾ��Ļɨ��ʱ����ͼ {@link String}.
		 */
		public static final String PROMPT_MESSAGE = "PROMPT_MESSAGE";

		/**
		 * ɨ���� 
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
		 * ɨ������ʽ
		 * Call {@link android.content.Intent#getStringExtra(String)}
		 * with {@link #RESULT_FORMAT} to determine which barcode format was
		 * found. See {@link com.google.zxing.BarcodeFormat} for possible
		 * values.
		 */
		public static final String RESULT_FORMAT = "SCAN_RESULT_FORMAT";

		/**
		 * ɨ������ʽ 
		 * Call {@link android.content.Intent#getStringExtra(String)}
		 * with {@link #RESULT_UPC_EAN_EXTENSION} to return the content of any
		 * UPC extension barcode that was also found. Only applicable to
		 * {@link com.google.zxing.BarcodeFormat#UPC_A} and
		 * {@link com.google.zxing.BarcodeFormat#EAN_13} formats.
		 */
		public static final String RESULT_UPC_EAN_EXTENSION = "SCAN_RESULT_UPC_EAN_EXTENSION";

		/**
		 * ɨ������ʽ 
		 * Call {@link android.content.Intent#getByteArrayExtra(String)}
		 * with {@link #RESULT_BYTES} to get a {@code byte[]} of raw bytes in
		 * the barcode, if available.
		 */
		public static final String RESULT_BYTES = "SCAN_RESULT_BYTES";

		/**
		 * ɨ�������� 
		 * Key for the value of
		 * {@link com.google.zxing.ResultMetadataType#ORIENTATION}, if
		 * available. Call
		 * {@link android.content.Intent#getIntArrayExtra(String)} with
		 * {@link #RESULT_ORIENTATION}.
		 */
		public static final String RESULT_ORIENTATION = "SCAN_RESULT_ORIENTATION";

		/**
		 * ɨ�������У��ˮƽ 
		 * Key for the value of
		 * {@link com.google.zxing.ResultMetadataType#ERROR_CORRECTION_LEVEL},
		 * if available. Call
		 * {@link android.content.Intent#getStringExtra(String)} with
		 * {@link #RESULT_ERROR_CORRECTION_LEVEL}.
		 */
		public static final String RESULT_ERROR_CORRECTION_LEVEL = "SCAN_RESULT_ERROR_CORRECTION_LEVEL";

		/**
		 * ɨ�����ֽڶ� 
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
		 * ��ʷ����
		 * Setting this to false will not save scanned codes in the
		 * history. Specified as a {@code boolean}.
		 */
		public static final String SAVE_HISTORY = "SAVE_HISTORY";

		private Scan() {
		}
	}

	public static final class History {
		// ��Ŀ���
		public static final String ITEM_NUMBER = "ITEM_NUMBER";

		private History() {
		}
	}

	/**
	 * ����
	 * 
	 * @author Administrator
	 * 
	 */
	public static final class Encode {
		/**
		 * ͨ�������ͼ��һ��������Ϊһ����ά�����ʾ
		 */
		public static final String ACTION = "com.google.zxing.client.android.ENCODE";

		/**
		 * ��Ҫ��������� The data to encode. Use
		 * {@link android.content.Intent#putExtra(String, String)} or
		 * {@link android.content.Intent#putExtra(String, android.os.Bundle)},
		 * depending on the type and format specified. Non-QR Code formats
		 * should just use a String here. For QR Code, see Contents for details.
		 */
		public static final String DATA = "ENCODE_DATA";

		/**
		 * ��Ҫ��������� The type of data being supplied if the format is QR Code. Use
		 * {@link android.content.Intent#putExtra(String, String)} with one of
		 * {@link Contents.Type}.
		 */
		public static final String TYPE = "ENCODE_TYPE";

		/**
		 * �������ʾ��ʽ The barcode format to be displayed. If this isn't specified
		 * or is blank, it defaults to QR Code. Use
		 * {@link android.content.Intent#putExtra(String, String)}, where format
		 * is one of {@link com.google.zxing.BarcodeFormat}.
		 */
		public static final String FORMAT = "ENCODE_FORMAT";

		/**
		 * �Ƿ���ʾΪ�����ݣ�false����ʾ Normally the contents of the barcode are displayed
		 * to the user in a TextView. Setting this boolean to false will hide
		 * that TextView, showing only the encode barcode.
		 */
		public static final String SHOW_CONTENTS = "ENCODE_SHOW_CONTENTS";

		private Encode() {
		}
	}

	/**
	 * �����鱾����
	 * 
	 * @author Administrator
	 * 
	 */
	public static final class SearchBookContents {
		/**
		 * ͨ�������ͼʹ�ùȸ�����������ͼ��
		 */
		public static final String ACTION = "com.google.zxing.client.android.SEARCH_BOOK_CONTENTS";

		/**
		 * �鱾��ISBN��
		 */
		public static final String ISBN = "ISBN";

		/**
		 * �ı�����
		 */
		public static final String QUERY = "QUERY";

		private SearchBookContents() {
		}
	}

	/**
	 * Wifi����
	 * 
	 * @author Administrator
	 * 
	 */
	public static final class WifiConnect {
		/**
		 * ͨ�������ͼ�������ӵ�wi - fi����
		 */
		public static final String ACTION = "com.google.zxing.client.android.WIFI_CONNECT";

		/**
		 * ��������SSID
		 */
		public static final String SSID = "SSID";

		/**
		 * ������������
		 */
		public static final String TYPE = "TYPE";

		/**
		 * ������������
		 */
		public static final String PASSWORD = "PASSWORD";

		private WifiConnect() {
		}
	}

	/**
	 * ������ά��ҳ��
	 * 
	 * @author Administrator
	 * 
	 */
	public static final class Share {
		/**
		 * ͨ�������ͼ�򿪶�ά���������ҳ��
		 */
		public static final String ACTION = "com.google.zxing.client.android.SHARE";

		private Share() {
		}
	}
}
