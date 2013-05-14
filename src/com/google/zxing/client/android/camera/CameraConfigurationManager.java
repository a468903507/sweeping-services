/*
 * Copyright (C) 2010 ZXing authors
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

package com.google.zxing.client.android.camera;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.zxing.client.android.PreferencesActivity;

/**
 * ���ദ���ȡ����������������������� ���������Ӳ��
 * @author linanjiang@guoxinad.com.cn
 *
 */
final class CameraConfigurationManager {

	private static final String TAG = "CameraConfiguration";

	/** ������Ļ�Ĵ�С*/
	private static final int MIN_PREVIEW_PIXELS = 470 * 320; // normal screen
	/** �����Ļ�Ĵ�С*/
	private static final int MAX_PREVIEW_PIXELS = 1280 * 720;

	private final Context context;
	private Point screenResolution;
	private Point cameraResolution;

	CameraConfigurationManager(Context context) {
		this.context = context;
	}

	/**
	 * ��ʼ���������
	 */
	void initFromCameraParameters(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		//��Ļ������ȷʱ�����
		if (width < height) {
			Log.i(TAG,
					"Display reports portrait orientation; assuming this is incorrect");
			int temp = width;
			width = height;
			height = temp;
		}
		screenResolution = new Point(width, height);
		Log.i(TAG, "Screen resolution: " + screenResolution);
		cameraResolution = findBestPreviewSizeValue(parameters,
				screenResolution);
		Log.i(TAG, "����ֱ���: " + cameraResolution);
	}
	/**
	 * ������������������
	 * @param camera
	 * @param safeMode
	 */
	void setDesiredCameraParameters(Camera camera, boolean safeMode) {
		Camera.Parameters parameters = camera.getParameters();

		if (parameters == null) {
			Log.w(TAG,
					"�豸����:û������������ǿ��õġ�����û������.");
			return;
		}

		Log.i(TAG, "��ʼ���������: " + parameters.flatten());

		if (safeMode) {
			Log.w(TAG,"��������ð�ȫģʽ������������ý����ᱻ֧��");
		}

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		initializeTorch(parameters, prefs, safeMode);

		String focusMode = null;
		//�Ƿ��Զ��Խ�
		if (prefs.getBoolean(PreferencesActivity.KEY_AUTO_FOCUS, true)) {
			if (safeMode
					|| prefs.getBoolean(
							PreferencesActivity.KEY_DISABLE_CONTINUOUS_FOCUS,
							false)) {
				focusMode = findSettableValue(
						parameters.getSupportedFocusModes(),
						Camera.Parameters.FOCUS_MODE_AUTO);
			} else {
				focusMode = findSettableValue(
						parameters.getSupportedFocusModes(),
						"continuous-picture", // Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
												// in 4.0+
						"continuous-video", // Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
											// in 4.0+
						Camera.Parameters.FOCUS_MODE_AUTO);
			}
		}
		// Maybe selected auto-focus but not available, so fall through here:
		if (!safeMode && focusMode == null) {
			focusMode = findSettableValue(parameters.getSupportedFocusModes(),
					Camera.Parameters.FOCUS_MODE_MACRO, "edof"); // Camera.Parameters.FOCUS_MODE_EDOF
																	// in 2.2+
		}
		if (focusMode != null) {
			parameters.setFocusMode(focusMode);
		}

		parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
		camera.setParameters(parameters);
	}

	Point getCameraResolution() {
		return cameraResolution;
	}

	Point getScreenResolution() {
		return screenResolution;
	}

	void setTorch(Camera camera, boolean newSetting) {
		Camera.Parameters parameters = camera.getParameters();
		doSetTorch(parameters, newSetting, false);
		camera.setParameters(parameters);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean currentSetting = prefs.getBoolean(
				PreferencesActivity.KEY_FRONT_LIGHT, false);
		if (currentSetting != newSetting) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean(PreferencesActivity.KEY_FRONT_LIGHT, newSetting);
			editor.commit();
		}
	}

	private void initializeTorch(Camera.Parameters parameters,
			SharedPreferences prefs, boolean safeMode) {
		boolean currentSetting = prefs.getBoolean(
				PreferencesActivity.KEY_FRONT_LIGHT, false);
		doSetTorch(parameters, currentSetting, safeMode);
	}
	/**
	 * �����Ƿ��ǰ��
	 * @param parameters �������
	 * @param newSetting �Ƿ��
	 * @param safeMode ����ģʽ
	 */
	private void doSetTorch(Camera.Parameters parameters, boolean newSetting,
			boolean safeMode) {
		String flashMode;//����ģʽ
		if (newSetting) {
			flashMode = findSettableValue(parameters.getSupportedFlashModes(),
					Camera.Parameters.FLASH_MODE_TORCH,
					Camera.Parameters.FLASH_MODE_ON);
		} else {
			flashMode = findSettableValue(parameters.getSupportedFlashModes(),
					Camera.Parameters.FLASH_MODE_OFF);
		}
		if (flashMode != null) {
			parameters.setFlashMode(flashMode);
		}

		/*
		 * SharedPreferences prefs =
		 * PreferenceManager.getDefaultSharedPreferences(context); if
		 * (!prefs.getBoolean(PreferencesActivity.KEY_DISABLE_EXPOSURE, false))
		 * { if (!safeMode) { ExposureInterface exposure = new
		 * ExposureManager().build(); exposure.setExposure(parameters,
		 * newSetting); } }
		 */
	}
	/**
	 * �ҵ���õ�Ԥ����Сֵ
	 * @param parameters
	 * @param screenResolution
	 * @return
	 */
	private Point findBestPreviewSizeValue(Camera.Parameters parameters,
			Point screenResolution) {

		List<Camera.Size> rawSupportedSizes = parameters
				.getSupportedPreviewSizes();
		if (rawSupportedSizes == null) {
			Log.w(TAG,"�豸û�з���֧��Ԥ����С;ʹ��Ĭ��");
			Camera.Size defaultSize = parameters.getPreviewSize();
			return new Point(defaultSize.width, defaultSize.height);
		}

		// ����С����
		List<Camera.Size> supportedPreviewSizes = new ArrayList<Camera.Size>(
				rawSupportedSizes);
		Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
			@Override
			public int compare(Camera.Size a, Camera.Size b) {
				int aPixels = a.height * a.width;
				int bPixels = b.height * b.width;
				if (bPixels < aPixels) {
					return -1;
				}
				if (bPixels > aPixels) {
					return 1;
				}
				return 0;
			}
		});

		if (Log.isLoggable(TAG, Log.INFO)) {
			StringBuilder previewSizesString = new StringBuilder();
			for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
				previewSizesString.append(supportedPreviewSize.width)
						.append('x').append(supportedPreviewSize.height)
						.append(' ');
			}
			Log.i(TAG, "Supported preview sizes: " + previewSizesString);
		}

		Point bestSize = null;
		float screenAspectRatio = (float) screenResolution.x
				/ (float) screenResolution.y;

		float diff = Float.POSITIVE_INFINITY;
		for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
			int realWidth = supportedPreviewSize.width;
			int realHeight = supportedPreviewSize.height;
			int pixels = realWidth * realHeight;
			if (pixels < MIN_PREVIEW_PIXELS || pixels > MAX_PREVIEW_PIXELS) {
				continue;
			}
			boolean isCandidatePortrait = realWidth < realHeight;
			int maybeFlippedWidth = isCandidatePortrait ? realHeight
					: realWidth;
			int maybeFlippedHeight = isCandidatePortrait ? realWidth
					: realHeight;
			if (maybeFlippedWidth == screenResolution.x
					&& maybeFlippedHeight == screenResolution.y) {
				Point exactPoint = new Point(realWidth, realHeight);
				Log.i(TAG, "����Ԥ����С��ȫƥ�����Ļ��С:: "
						+ exactPoint);
				return exactPoint;
			}
			float aspectRatio = (float) maybeFlippedWidth
					/ (float) maybeFlippedHeight;
			float newDiff = Math.abs(aspectRatio - screenAspectRatio);
			if (newDiff < diff) {
				bestSize = new Point(realWidth, realHeight);
				diff = newDiff;
			}
		}

		if (bestSize == null) {
			Camera.Size defaultSize = parameters.getPreviewSize();
			bestSize = new Point(defaultSize.width, defaultSize.height);
			Log.i(TAG, "û�к��ʵ�Ԥ����С,ʹ��Ĭ�ϵ�: " + bestSize);
		}

		Log.i(TAG, "������ѽ���Ԥ����С: " + bestSize);
		return bestSize;
	}
	/**
	 * �ҵ������õ�ֵ
	 * @param supportedValues
	 * @param desiredValues
	 * @return
	 */
	private static String findSettableValue(Collection<String> supportedValues,
			String... desiredValues) {
		Log.i(TAG, "֧�ֵ�ֵ: " + supportedValues);
		String result = null;
		if (supportedValues != null) {
			for (String desiredValue : desiredValues) {
				if (supportedValues.contains(desiredValue)) {
					result = desiredValue;
					break;
				}
			}
		}
		Log.i(TAG, "�����õ�ֵ: " + result);
		return result;
	}

}
