package net.kiwi.framework;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.window.OnBackInvokedDispatcher;

import com.touchfoo.swordigo.GameRenderer;
import com.touchfoo.swordigo.GameView;
import com.touchfoo.swordigo.Native;

import net.kiwi.overlay.Controller;

public class MainActivity extends Activity {
	private GameView gameView;

	static {
		System.loadLibrary("openal-soft");
		System.loadLibrary("swordigo");
		System.loadLibrary("kiwi");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FrameLayout root = new FrameLayout(this);
		root.setLayoutParams(new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT
		));

		gameView = new GameView(this);
		gameView.setEGLConfigChooser(5, 6, 5, 0, 16, 0);
		gameView.setPreserveEGLContextOnPause(true);
		gameView.setRenderer(new GameRenderer());
		root.addView(gameView, new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT
		));

		setContentView(root);
		enableImmersiveMode();

		Controller.init(this, root);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
				OnBackInvokedDispatcher.PRIORITY_DEFAULT,
				Native::handleBackButtonPress
			);
		}

		Native.mainActivity = this;
		Native.setFilesDir(getFilesDir().getAbsolutePath());
		Native.setCacheDir(getCacheDir().getAbsolutePath());
		Native.setAssetManager(getAssets());
		Native.handleApplicationLaunch();
	}

	@SuppressWarnings("deprecation")
	private void enableImmersiveMode() {
		if (gameView == null) return;
		int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_FULLSCREEN
			| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
			| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
			| View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
		gameView.setSystemUiVisibility(flags);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (gameView != null) {
			gameView.onResume();
			enableImmersiveMode();
			Native.applicationDidBecomeActive();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (gameView != null) {
			gameView.onPause();
			Native.applicationDidBecomeInactive();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Controller.removeAll();
		Native.handleApplicationQuit();
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onBackPressed() {
		super.onBackPressed();
		Native.handleBackButtonPress();
	}
}
