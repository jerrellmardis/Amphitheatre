package com.jerrellmardis.amphitheatre.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.Enums;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Jeremy Shore on 8/18/14.
 */
public class CustomizeDialogFragment extends DialogFragment implements RadioGroup.OnCheckedChangeListener {

    @InjectView(R.id.visible_type) RadioGroup mPaletteSelectionType;
    @InjectView(R.id.color_type) RadioGroup mPaletteColorType;
    @InjectView(R.id.text_color) RadioGroup mPaletteTextType;

    private SharedPreferences mSharedPrefs;
    private OnSaveListener mOnSaveListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customizations, container);
        ButterKnife.inject(this, view);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPaletteSelectionType.setOnCheckedChangeListener(this);

        if (mSharedPrefs.getString(Constants.PALETTE_VIBRANCY, "").equals("")) {
            mSharedPrefs.edit().putString(Constants.PALETTE_VIBRANCY,
                    Enums.PaletteType.MUTED.name()).apply();
        }
        if (mSharedPrefs.getString(Constants.PALETTE_VISIBILITY, "").equals("")) {
            mSharedPrefs.edit().putString(Constants.PALETTE_VISIBILITY,
                    Enums.PalettePresenterType.ALL.name()).apply();
        }
        if (mSharedPrefs.getString(Constants.PALETTE_TEXT_VISIBILITY, "").equals("")) {
            mSharedPrefs.edit().putString(Constants.PALETTE_TEXT_VISIBILITY,
                    Enums.PaletteTextType.NONE.name()).apply();
        }

        loadSettings(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnSaveListener) {
            mOnSaveListener = (OnSaveListener) activity;
        } else if (getTargetFragment() instanceof OnSaveListener) {
            mOnSaveListener = (OnSaveListener) getTargetFragment();
        } else {
            throw new ClassCastException("Caller must implement OnSaveListener interface.");
        }
    }

    public interface OnSaveListener {
        void onSaveCustomization();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int viewId) {
        switch(viewId) {
            case R.id.no_palette:
                setColorEnabled(false);
                break;
            case R.id.single_palette:
                setColorEnabled(true);
                break;
            case R.id.all_palette:
                setColorEnabled(true);
                break;
        }
    }

    private void saveCustomizations() {
        switch (mPaletteTextType.getCheckedRadioButtonId()) {
            case R.id.no_text:
                saveSetting(Constants.PALETTE_TEXT_VISIBILITY, Enums.PaletteTextType.NONE.name());
                break;
            case R.id.title_only:
                saveSetting(Constants.PALETTE_TEXT_VISIBILITY, Enums.PaletteTextType.TITLE_ONLY.name());
                break;
            case R.id.subtitle_only:
                saveSetting(Constants.PALETTE_TEXT_VISIBILITY, Enums.PaletteTextType.SUBTITLE_ONLY.name());
                break;
            case R.id.all_text:
                saveSetting(Constants.PALETTE_TEXT_VISIBILITY, Enums.PaletteTextType.ALL.name());
                break;
        }
        switch(mPaletteColorType.getCheckedRadioButtonId()) {
            case R.id.muted_palette:
                saveSetting(Constants.PALETTE_VIBRANCY, Enums.PaletteType.MUTED.name());
                break;
            case R.id.vibrant_palette:
                saveSetting(Constants.PALETTE_VIBRANCY, Enums.PaletteType.VIBRANT.name());
                break;
        }
        switch (mPaletteSelectionType.getCheckedRadioButtonId()) {
            case R.id.no_palette:
                saveSetting(Constants.PALETTE_VISIBILITY, Enums.PalettePresenterType.NONE.name());
                break;
            case R.id.single_palette:
                saveSetting(Constants.PALETTE_VISIBILITY, Enums.PalettePresenterType.FOCUSED.name());
                break;
            case R.id.all_palette:
                saveSetting(Constants.PALETTE_VISIBILITY, Enums.PalettePresenterType.ALL.name());
                break;
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_save)
    public void onOkClick() {
        saveCustomizations();
        mOnSaveListener.onSaveCustomization();
        getDialog().dismiss();
    }

    private void setColorEnabled(boolean colorEnabled) {
        mPaletteColorType.setEnabled(colorEnabled);
    }

    private void saveSetting(String key, String value) {
        mSharedPrefs.edit().putString(key, value).apply();
    }

    private void loadSettings(View view) {
        switch (Enums.PaletteTextType.valueOf(mSharedPrefs.getString(Constants.PALETTE_TEXT_VISIBILITY, ""))) {
            case ALL:
                ((RadioButton)ButterKnife.findById(view, R.id.all_text)).setChecked(true);
                break;
            case TITLE_ONLY:
                ((RadioButton)ButterKnife.findById(view, R.id.title_only)).setChecked(true);
                break;
            case SUBTITLE_ONLY:
                ((RadioButton)ButterKnife.findById(view, R.id.subtitle_only)).setChecked(true);
                break;
            case NONE:
                ((RadioButton)ButterKnife.findById(view, R.id.no_text)).setChecked(true);
                break;
        }
        switch (Enums.PalettePresenterType.valueOf(mSharedPrefs.getString(Constants.PALETTE_VISIBILITY, ""))) {
            case ALL:
                ((RadioButton)ButterKnife.findById(view, R.id.all_palette)).setChecked(true);
                break;
            case FOCUSED:
                ((RadioButton)ButterKnife.findById(view, R.id.single_palette)).setChecked(true);
                break;
            case NONE:
                ((RadioButton)ButterKnife.findById(view, R.id.no_palette)).setChecked(true);
                break;
        }
        switch(Enums.PaletteType.valueOf(mSharedPrefs.getString(Constants.PALETTE_VIBRANCY, ""))) {
            case MUTED:
                ((RadioButton)ButterKnife.findById(view, R.id.muted_palette)).setChecked(true);
                break;
            case VIBRANT:
                ((RadioButton)ButterKnife.findById(view, R.id.vibrant_palette)).setChecked(true);
                break;
        }
    }

}
