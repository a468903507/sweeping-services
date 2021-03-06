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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import net.sweeping.app.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.history.HistoryManager;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.client.android.result.ResultHandlerFactory;
import com.google.zxing.client.android.result.supplement.SupplementalInfoRetriever;

/**
 * ɨ��ͷ��ؽ�����
 * 
 * @author linanjiang@guoxinad.com.cn
 * 
 */
public final class CaptureActivity extends Activity implements
		SurfaceHolder.Callback {

	private static final String TAG = CaptureActivity.class.getSimpleName();

	/** Ĭ�ϵ���ͼ������ʱ�� */
	private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;
	/** ����ģʽɨ���ӳٺ��� */
	private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;

	/** ���̰��� */
	private static final String PACKAGE_NAME = "com.google.zxing.client.android";
	/** ����urlǰ׺ */
	private static final String PRODUCT_SEARCH_URL_PREFIX = "http://www.google";
	/** ����url��׺ */
	private static final String PRODUCT_SEARCH_URL_SUFFIX = "/m/products/scan";
	/** ���ش���ռλ�� */
	private static final String RETURN_CODE_PLACEHOLDER = "{CODE}";
	/** ���ص�url���� */
	private static final String RETURN_URL_PARAM = "ret";
	/** ԭʼ���� */
	private static final String RAW_PARAM = "raw";

	/** ��ʷ������� */
	public static final int HISTORY_REQUEST_CODE = 0x0000bacc;

	/** ����Ԫ������� */
	private static final Set<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = EnumSet
			.of(ResultMetadataType.ISSUE_NUMBER,
					ResultMetadataType.SUGGESTED_PRICE,
					ResultMetadataType.ERROR_CORRECTION_LEVEL,
					ResultMetadataType.POSSIBLE_COUNTRY);

	/** ����������� */
	private CameraManager cameraManager;
	/** ���봦���࣬�������������߳̽��н��� */
	private CaptureActivityHandler handler;
	/** ��������� */
	private Result savedResultToShow;
	/** ���ȡ���� */
	private ViewfinderView viewfinderView;
	/** ������ı��� */
	private TextView statusView;
	/** ���ؽ��ҳ�� */
	private View resultView;
	/** ���ս�� */
	private Result lastResult;
	/** surfaceView�Ƿ���� */
	private boolean hasSurface;
	/** �Ƿ񿽱���������� */
	private boolean copyToClipboard;
	/** ��ͼ����ö���� */
	private IntentSource source;
	/** ��Դ·�� */
	private String sourceUrl;
	/** ����·��ģ�� */
	private String returnUrlTemplate;
	/** */
	private boolean returnRaw;
	/** �����ʽ */
	private Collection<BarcodeFormat> decodeFormats;
	/** �ַ� */
	private String characterSet;
	/** ��ʷ������ */
	private HistoryManager historyManager;
	/** ��ʱ�� */
	private InactivityTimer inactivityTimer;
	/** �������𶯹����� */
	private BeepManager beepManager;

	/**
	 * 
	 * @return ���ȡ����
	 */
	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	/**
	 * 
	 * @return ���봦����
	 */
	public Handler getHandler() {
		return handler;
	}

	/**
	 * 
	 * @return �����������
	 */
	CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Window window = getWindow();
		// ������Ļ����
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.capture);

		hasSurface = false;
		historyManager = new HistoryManager(this);
		historyManager.trimHistory();
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// CameraManager�����������ʼ������Ȼ�ᱨ��
		cameraManager = new CameraManager(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);
		resultView = findViewById(R.id.result_view);
		statusView = (TextView) findViewById(R.id.status_view);

		handler = null;
		lastResult = null;

		resetStatusView();
		// ɨ��ҳ��
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			// ���ûص�,�ȴ�surfaceCreated()����ʼ�������
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		beepManager.updatePrefs();

		inactivityTimer.onResume();

		Intent intent = getIntent();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		copyToClipboard = prefs.getBoolean(
				PreferencesActivity.KEY_COPY_TO_CLIPBOARD, true)
				&& (intent == null || intent.getBooleanExtra(
						Intents.Scan.SAVE_HISTORY, true));

		source = IntentSource.NONE;
		decodeFormats = null;
		characterSet = null;

		if (intent != null) {

			String action = intent.getAction();
			String dataString = intent.getDataString();

			if (Intents.Scan.ACTION.equals(action)) {

				// Scan the formats the intent requested, and return the result
				// to the calling activity.
				source = IntentSource.NATIVE_APP_INTENT;
				decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);

				if (intent.hasExtra(Intents.Scan.WIDTH)
						&& intent.hasExtra(Intents.Scan.HEIGHT)) {
					int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
					int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
					if (width > 0 && height > 0) {
						// ָ��ɨ����γߴ�
						cameraManager.setManualFramingRect(width, height);
					}
				}

				String customPromptMessage = intent
						.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
				if (customPromptMessage != null) {
					statusView.setText(customPromptMessage);
				}

			} else if (dataString != null
					&& dataString.contains(PRODUCT_SEARCH_URL_PREFIX)
					&& dataString.contains(PRODUCT_SEARCH_URL_SUFFIX)) {

				// Scan only products and send the result to mobile Product
				// Search.
				source = IntentSource.PRODUCT_SEARCH_LINK;
				sourceUrl = dataString;
				decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;

			}

			characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);

		}
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (source == IntentSource.NATIVE_APP_INTENT) {
				setResult(RESULT_CANCELED);
				finish();
				return true;
			}
			if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK)
					&& lastResult != null) {
				restartPreviewAfterDelay(0L);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_FOCUS:
		case KeyEvent.KEYCODE_CAMERA:
			// Handle these events so they don't launch the Camera app
			return true;
			// Use volume up/down to turn on light
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			cameraManager.setTorch(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			cameraManager.setTorch(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * �ײ��˵�
	 */
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater menuInflater = getMenuInflater();
//		menuInflater.inflate(R.menu.capture, menu);
//		return super.onCreateOptionsMenu(menu);
//	}

	/**
	 * �ײ��˵�
	 */
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		Intent intent = new Intent(Intent.ACTION_VIEW);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//		switch (item.getItemId()) {
//		case R.id.menu_share:
//			intent.setClassName(this, ShareActivity.class.getName());
//			startActivity(intent);
//			break;
//		case R.id.menu_history:
//			intent.setClassName(this, HistoryActivity.class.getName());
//			startActivityForResult(intent, HISTORY_REQUEST_CODE);
//			break;
//		case R.id.menu_settings:
//			intent.setClassName(this, PreferencesActivity.class.getName());
//			startActivity(intent);
//			break;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//		return true;
//	}

//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//		if (resultCode == RESULT_OK) {
//			if (requestCode == HISTORY_REQUEST_CODE) {
//				int itemNumber = intent.getIntExtra(
//						Intents.History.ITEM_NUMBER, -1);
//				if (itemNumber >= 0) {
//					HistoryItem historyItem = historyManager
//							.buildHistoryItem(itemNumber);
//					decodeOrStoreSavedBitmap(null, historyItem.getResult());
//				}
//			}
//		}
//	}

	/**
	 * �����洢�����λͼ
	 * 
	 * @param bitmap
	 * @param result
	 */
	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler,
						R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG,
					"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * 
	 * �����뱻����,���Ը�ɹ���һ������,��ʾ���
	 * 
	 * @param rawResult
	 *            �����������.
	 * @param barcode
	 *            ������һ���Ҷ�ͼ�������ݽ���
	 */
	public void handleDecode(Result rawResult, Bitmap barcode) {
		inactivityTimer.onActivity();
		lastResult = rawResult;
		ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(
				this, rawResult);

		boolean fromLiveScan = barcode != null;
		if (fromLiveScan) {
			historyManager.addHistoryItem(rawResult, resultHandler);
			// Then not from history, so beep/vibrate and we have an image to
			// draw on
			beepManager.playBeepSoundAndVibrate();
			drawResultPoints(barcode, rawResult);
		}

		switch (source) {
		case NATIVE_APP_INTENT:
		case PRODUCT_SEARCH_LINK:
			handleDecodeExternally(rawResult, resultHandler, barcode);
			break;
		case ZXING_LINK:
			if (returnUrlTemplate == null) {
				handleDecodeInternally(rawResult, resultHandler, barcode);
			} else {
				handleDecodeExternally(rawResult, resultHandler, barcode);
			}
			break;
		case NONE:
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			if (fromLiveScan
					&& prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE,
							false)) {
				String message = getResources().getString(
						R.string.msg_bulk_mode_scanned)
						+ " (" + rawResult.getText() + ')';
				Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
				// Wait a moment or else it will scan the same barcode
				// continuously about 3 times
				restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
			} else {
				handleDecodeInternally(rawResult, resultHandler, barcode);
			}
			break;
		}
	}

	/**
	 * ���ƽ��
	 * 
	 * @param barcode
	 *            A bitmap of the captured image.
	 * @param rawResult
	 *            The decoded results which contains the points to draw.
	 */
	private void drawResultPoints(Bitmap barcode, Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if (points != null && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.result_points));
			if (points.length == 2) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1]);
			} else if (points.length == 4
					&& (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult
							.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
				// Hacky special case -- draw two lines, for the barcode and
				// metadata
				drawLine(canvas, paint, points[0], points[1]);
				drawLine(canvas, paint, points[2], points[3]);
			} else {
				paint.setStrokeWidth(10.0f);
				for (ResultPoint point : points) {
					canvas.drawPoint(point.getX(), point.getY(), paint);
				}
			}
		}
	}

	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a,
			ResultPoint b) {
		canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
	}

	/**
	 * ��ʾ��������ҳ��
	 * 
	 * @param rawResult
	 * @param resultHandler
	 * @param barcode
	 */
	private void handleDecodeInternally(Result rawResult,
			ResultHandler resultHandler, Bitmap barcode) {
		
		statusView.setVisibility(View.GONE);
		viewfinderView.setVisibility(View.GONE);
		resultView.setVisibility(View.VISIBLE);

		ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
		if (barcode == null) {
			barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(
					getResources(), R.drawable.launcher_icon));
		} else {
			barcodeImageView.setImageBitmap(barcode);
		}
		// ���ø�ʽ
		TextView formatTextView = (TextView) findViewById(R.id.format_text_view);
		formatTextView.setText(rawResult.getBarcodeFormat().toString());
		// ��������
		TextView typeTextView = (TextView) findViewById(R.id.type_text_view);
		typeTextView.setText(resultHandler.getType().toString());
		// ����ʱ��
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT);
		String formattedTime = formatter.format(new Date(rawResult
				.getTimestamp()));
		TextView timeTextView = (TextView) findViewById(R.id.time_text_view);
		timeTextView.setText(formattedTime);

		// ����Ԫ���
		TextView metaTextView = (TextView) findViewById(R.id.meta_text_view);
		View metaTextViewLabel = findViewById(R.id.meta_text_view_label);
		metaTextView.setVisibility(View.GONE);
		metaTextViewLabel.setVisibility(View.GONE);
		Map<ResultMetadataType, Object> metadata = rawResult
				.getResultMetadata();
		if (metadata != null) {
			StringBuilder metadataText = new StringBuilder(20);
			for (Map.Entry<ResultMetadataType, Object> entry : metadata
					.entrySet()) {
				if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
					metadataText.append(entry.getValue()).append('\n');
				}
			}
			if (metadataText.length() > 0) {
				metadataText.setLength(metadataText.length() - 1);
				metaTextView.setText(metadataText);
				metaTextView.setVisibility(View.VISIBLE);
				metaTextViewLabel.setVisibility(View.VISIBLE);
			}
		}
		// ���ý�����������
		TextView contentsTextView = (TextView) findViewById(R.id.contents_text_view);
		CharSequence displayContents = resultHandler.getDisplayContents();
		contentsTextView.setText(displayContents);
		// Crudely scale betweeen 22 and 32 -- bigger font for shorter text
		int scaledSize = Math.max(22, 32 - displayContents.length() / 4);
		contentsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);
		// ���ý������Ĳ�������
		TextView supplementTextView = (TextView) findViewById(R.id.contents_supplement_text_view);
		supplementTextView.setText("");
		supplementTextView.setOnClickListener(null);
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				PreferencesActivity.KEY_SUPPLEMENTAL, true)) {
			SupplementalInfoRetriever.maybeInvokeRetrieval(supplementTextView,
					resultHandler.getResult(), historyManager, this);
		}

//		int buttonCount = resultHandler.getButtonCount();
//		ViewGroup buttonView = (ViewGroup) findViewById(R.id.result_button_view);
//		buttonView.requestFocus();
//		// ���ý���ɹ��ײ���ť
//		for (int x = 0; x < ResultHandler.MAX_BUTTON_COUNT; x++) {
//			TextView button = (TextView) buttonView.getChildAt(x);
//			if (x < buttonCount) {
//				button.setVisibility(View.VISIBLE);
//				button.setText(resultHandler.getButtonText(x));
//				button.setOnClickListener(new ResultButtonListener(
//						resultHandler, x));
//			} else {
//				button.setVisibility(View.GONE);
//			}
//		}

		if (copyToClipboard && !resultHandler.areContentsSecure()) {
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			if (displayContents != null) {
				clipboard.setText(displayContents);
			}
		}
	}

	// Briefly show the contents of the barcode, then handle the result outside
	// Barcode Scanner.
	private void handleDecodeExternally(Result rawResult,
			ResultHandler resultHandler, Bitmap barcode) {

		if (barcode != null) {
			viewfinderView.drawResultBitmap(barcode);
		}
		/** ������ʱ��*/
		long resultDurationMS;
		if (getIntent() == null) {
			resultDurationMS = DEFAULT_INTENT_RESULT_DURATION_MS;
		} else {
			resultDurationMS = getIntent().getLongExtra(
					Intents.Scan.RESULT_DISPLAY_DURATION_MS,
					DEFAULT_INTENT_RESULT_DURATION_MS);
		}

		// Since this message will only be shown for a second, just tell the
		// user what kind of
		// barcode was found (e.g. contact info) rather than the full contents,
		// which they won't
		// have time to read.
		if (resultDurationMS > 0) {
			statusView.setText(getString(resultHandler.getDisplayTitle()));
		}

		if (copyToClipboard && !resultHandler.areContentsSecure()) {
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			CharSequence text = resultHandler.getDisplayContents();
			if (text != null) {
				clipboard.setText(text);
			}
		}

		if (source == IntentSource.NATIVE_APP_INTENT) {

			// Hand back whatever action they requested - this can be changed to
			// Intents.Scan.ACTION when
			// the deprecated intent is retired.
			Intent intent = new Intent(getIntent().getAction());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
			intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult
					.getBarcodeFormat().toString());
			byte[] rawBytes = rawResult.getRawBytes();
			if (rawBytes != null && rawBytes.length > 0) {
				intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
			}
			Map<ResultMetadataType, ?> metadata = rawResult.getResultMetadata();
			if (metadata != null) {
				if (metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {
					intent.putExtra(Intents.Scan.RESULT_UPC_EAN_EXTENSION,
							metadata.get(ResultMetadataType.UPC_EAN_EXTENSION)
									.toString());
				}
				Integer orientation = (Integer) metadata
						.get(ResultMetadataType.ORIENTATION);
				if (orientation != null) {
					intent.putExtra(Intents.Scan.RESULT_ORIENTATION,
							orientation.intValue());
				}
				String ecLevel = (String) metadata
						.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
				if (ecLevel != null) {
					intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL,
							ecLevel);
				}
				Iterable<byte[]> byteSegments = (Iterable<byte[]>) metadata
						.get(ResultMetadataType.BYTE_SEGMENTS);
				if (byteSegments != null) {
					int i = 0;
					for (byte[] byteSegment : byteSegments) {
						intent.putExtra(
								Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + i,
								byteSegment);
						i++;
					}
				}
			}
			sendReplyMessage(R.id.return_scan_result, intent, resultDurationMS);

		} else if (source == IntentSource.PRODUCT_SEARCH_LINK) {

			// Reformulate the URL which triggered us into a query, so that the
			// request goes to the same
			// TLD as the scan URL.
			int end = sourceUrl.lastIndexOf("/scan");
			String replyURL = sourceUrl.substring(0, end) + "?q="
					+ resultHandler.getDisplayContents() + "&source=zxing";
			sendReplyMessage(R.id.launch_product_query, replyURL,
					resultDurationMS);

		} else if (source == IntentSource.ZXING_LINK) {

			// Replace each occurrence of RETURN_CODE_PLACEHOLDER in the
			// returnUrlTemplate
			// with the scanned code. This allows both queries and REST-style
			// URLs to work.
			if (returnUrlTemplate != null) {
				CharSequence codeReplacement = returnRaw ? rawResult.getText()
						: resultHandler.getDisplayContents();
				try {
					codeReplacement = URLEncoder.encode(
							codeReplacement.toString(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// can't happen; UTF-8 is always supported. Continue, I
					// guess, without encoding
				}
				String replyURL = returnUrlTemplate.replace(
						RETURN_CODE_PLACEHOLDER, codeReplacement);
				sendReplyMessage(R.id.launch_product_query, replyURL,
						resultDurationMS);
			}

		}
	}

	/**
	 * ����Ӧ����Ϣ
	 * @param id handler��Ϣ����
	 * @param arg ��Ϣ����
	 * @param delayMS
	 */
	private void sendReplyMessage(int id, Object arg, long delayMS) {
		Message message = Message.obtain(handler, id, arg);
		if (delayMS > 0L) {
			handler.sendMessageDelayed(message, delayMS);
		} else {
			handler.sendMessage(message);
		}
	}

	/**
	 * ����ʼ��
	 * 
	 * @param surfaceHolder
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG,
					"initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats,
						characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	/**
	 * ����Ի��򣬱�Ǹ��Android���������⡣�������Ҫ�����豸
	 */
	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	/**
	 * ����Ԥ�����ӳ�
	 * 
	 * @param delayMS
	 */
	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	/**
	 * �������ó�ʼ��
	 */
	private void resetStatusView() {
		resultView.setVisibility(View.GONE);
		statusView.setText(R.string.msg_default_status);
		statusView.setVisibility(View.VISIBLE);
		viewfinderView.setVisibility(View.VISIBLE);
		lastResult = null;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}
}
