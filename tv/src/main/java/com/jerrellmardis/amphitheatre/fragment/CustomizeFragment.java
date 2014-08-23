package com.jerrellmardis.amphitheatre.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.Enums;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by Jeremy Shore on 8/18/14.
 */
public class CustomizeFragment extends DialogFragment implements RadioGroup.OnCheckedChangeListener {

    @InjectView(R.id.vibrant_palette) RadioButton mPaletteVibrant;
    @InjectView(R.id.muted_palette) RadioButton mPaletteMuted;
    @InjectView(R.id.visible_type) RadioGroup mPaletteSelectionType;

    private SharedPreferences mSharedPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customizations, container);
        ButterKnife.inject(this, view);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPaletteSelectionType.setOnCheckedChangeListener(this);

        loadSettings(view);
        return view;
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
                saveSetting(Constants.PALETTE_VISIBILITY, Enums.PalettePresenterType.NONE.name());
                break;
            case R.id.single_palette:
                setColorEnabled(true);
                saveSetting(Constants.PALETTE_VISIBILITY, Enums.PalettePresenterType.FOCUSED.name());
                break;
            case R.id.all_palette:
                setColorEnabled(true);
                saveSetting(Constants.PALETTE_VISIBILITY, Enums.PalettePresenterType.ALL.name());
                break;
            case R.id.muted_palette:
                saveSetting(Constants.PALETTE_VIBRANCY, Enums.PaletteType.MUTED.name());
                break;
            case R.id.vibrant_palette:
                saveSetting(Constants.PALETTE_VIBRANCY, Enums.PaletteType.VIBRANT.name());
                break;
        }
    }

    @OnClick(R.id.btn_okay)
    public void onOkaClick() {
        getDialog().dismiss();
    }

    private void setColorEnabled(boolean colorEnabled) {
        mPaletteVibrant.setEnabled(colorEnabled);
        mPaletteMuted.setEnabled(colorEnabled);
    }

    private void saveSetting(String key, String value) {
        mSharedPrefs.edit().putString(key, value).apply();
    }

    private void loadSettings(View view) {
        switch (Enums.PalettePresenterType.valueOf(mSharedPrefs.getString(Constants.PALETTE_VISIBILITY, ""))) {
            case ALL:
                ((RadioButton)view.findViewById(R.id.all_palette)).setChecked(true);
                break;
            case FOCUSED:
                ((RadioButton)view.findViewById(R.id.single_palette)).setChecked(true);
                break;
            case NONE:
                ((RadioButton)view.findViewById(R.id.no_palette)).setChecked(true);
                break;
        }
        switch(Enums.PaletteType.valueOf(mSharedPrefs.getString(Constants.PALETTE_VIBRANCY, ""))) {
            case MUTED:
                ((RadioButton)view.findViewById(R.id.muted_palette)).setChecked(true);
                break;
            case VIBRANT:
                ((RadioButton)view.findViewById(R.id.vibrant_palette)).setChecked(true);
                break;
        }
    }

}
