package com.touchfoo.swordigo;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.util.Log;

public class TextInput {

	private static EditText currentEditText = null;

	public static void startTextInput(final String initialText) {
		final Activity activity = (Activity) Native.mainActivity;
		if (activity == null) {
			Log.w("TextInput", "No activity");
			return;
		}

		activity.runOnUiThread(() -> {
			stopTextInputInternal(activity);

			EditText editText = new EditText(activity);

			editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_DONE);
			editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

			editText.setBackground(null);
			editText.setTextColor(0x00000000);
			editText.setCursorVisible(false);
			editText.setAlpha(0f);

			editText.setText(initialText != null ? initialText : "");
			editText.setSelection(editText.getText().length());
			editText.setFocusable(true);
			editText.setFocusableInTouchMode(true);

			ViewGroup root = activity.findViewById(android.R.id.content);
			root.addView(editText, new ViewGroup.LayoutParams(1, 1));

			currentEditText = editText;

			editText.requestFocus();

			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			editText.postDelayed(() -> {
				if (imm != null)
					imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
			}, 80);

			editText.addTextChangedListener(new TextWatcher() {
				@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				@Override public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (GameView.instance != null) {
						GameView.instance.queueEvent(() -> {
							try {
								Native.textInputTextDidChange(s.toString());
							} catch (Throwable ignored) {}
						});
					}
				}
				@Override public void afterTextChanged(Editable s) {}
			});

			editText.setOnEditorActionListener((v, actionId, event) -> {
				if (actionId == EditorInfo.IME_ACTION_DONE ||
				(event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
				event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

					if (imm != null)
						imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

					stopTextInputInternal(activity);
					if (GameView.instance != null) {
						GameView.instance.queueEvent(() -> {
							try {
								Native.textInputDidFinish();
							} catch (Throwable ignored) {}
						});
					}
					return true;
				}
				return false;
			});
		});
	}

	public static void stopTextInput() {
		final Activity activity = (Activity) Native.mainActivity;
		if (activity == null) return;

		activity.runOnUiThread(() -> stopTextInputInternal(activity));
	}

	private static void stopTextInputInternal(Activity activity) {
		if (currentEditText == null) return;

		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null)
			imm.hideSoftInputFromWindow(currentEditText.getWindowToken(), 0);

		ViewGroup root = activity.findViewById(android.R.id.content);
		root.removeView(currentEditText);

		currentEditText = null;
	}
}
