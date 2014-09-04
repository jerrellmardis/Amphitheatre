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
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.jerrellmardis.amphitheatre.R;
import com.jerrellmardis.amphitheatre.util.Constants;
import com.jerrellmardis.amphitheatre.util.Enums;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Jeremy Shore on 8/18/14.
 */
public class CustomizeDialogFragment extends DialogFragment{

    @InjectView(R.id.background_visibility) Spinner mBackgroundVisibility;
    @InjectView(R.id.background_unfocused_color) Spinner mBackgroundUnselected;
    @InjectView(R.id.background_focused_color) Spinner mBackgroundSelected;
    @InjectView(R.id.content_text_visibility) Spinner mContentVisibility;
    @InjectView(R.id.content_unselected_color) Spinner mContentUnselected;
    @InjectView(R.id.content_selected_color) Spinner mContentSelected;
    @InjectView(R.id.title_text_visibility) Spinner mTitleVisibility;
    @InjectView(R.id.title_unselected_color) Spinner mTitleUnselected;
    @InjectView(R.id.title_selected_color) Spinner mTitleSelected;

    private SharedPreferences mSharedPrefs;
    private OnSaveListener mOnSaveListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customizations, container);
        ButterKnife.inject(this, view);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mBackgroundVisibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View thisView, int i, long l) {
                String[] items = getResources().getStringArray(R.array.palette_visibility);

                if (items[i].equals(getString(R.string.no_card))) {
                    mBackgroundUnselected.setVisibility(View.GONE);
                    mBackgroundSelected.setVisibility(View.GONE);
                }
                else if(items[i].equals(getString(R.string.single_card))) {
                    mBackgroundUnselected.setVisibility(View.GONE);
                    mBackgroundSelected.setVisibility(View.VISIBLE);
                }
                else if(items[i].equals(getString(R.string.all_cards))) {
                    mBackgroundUnselected.setVisibility(View.VISIBLE);
                    mBackgroundSelected.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mContentVisibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] items = getResources().getStringArray(R.array.palette_visibility);

                if (items[i].equals(getString(R.string.no_card))) {
                    mContentUnselected.setVisibility(View.GONE);
                    mContentSelected.setVisibility(View.GONE);
                }
                else if(items[i].equals(getString(R.string.single_card))) {
                    mContentUnselected.setVisibility(View.GONE);
                    mContentSelected.setVisibility(View.VISIBLE);
                }
                else if(items[i].equals(getString(R.string.all_cards))) {
                    mContentUnselected.setVisibility(View.VISIBLE);
                    mContentSelected.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mTitleVisibility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] items = getResources().getStringArray(R.array.palette_visibility);

                if (items[i].equals(getString(R.string.no_card))) {
                    mTitleUnselected.setVisibility(View.GONE);
                    mTitleSelected.setVisibility(View.GONE);
                }
                else if(items[i].equals(getString(R.string.single_card))) {
                    mTitleUnselected.setVisibility(View.GONE);
                    mTitleSelected.setVisibility(View.VISIBLE);
                }
                else if(items[i].equals(getString(R.string.all_cards))) {
                    mTitleUnselected.setVisibility(View.VISIBLE);
                    mTitleSelected.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        checkPrefs();
        loadSettings();
        return view;
    }

    private void checkPrefs() {
        if (!mSharedPrefs.contains(Constants.PALETTE_BACKGROUND_VISIBLE)) {
            mSharedPrefs.edit().putString(Constants.PALETTE_BACKGROUND_VISIBLE, Enums.PalettePresenterType.FOCUSEDCARD.name()).apply();
        }
        if (!mSharedPrefs.contains(Constants.PALETTE_BACKGROUND_UNSELECTED)) {
            mSharedPrefs.edit().putString(Constants.PALETTE_BACKGROUND_UNSELECTED, "").apply();
        }
        if (!mSharedPrefs.contains(Constants.PALETTE_BACKGROUND_SELECTED)) {
            mSharedPrefs.edit().putString(Constants.PALETTE_BACKGROUND_SELECTED, Enums.PaletteColor.DARKMUTED.name()).apply();
        }
        if (!mSharedPrefs.contains(Constants.PALETTE_TITLE_VISIBLE)) {
            mSharedPrefs.edit().putString(Constants.PALETTE_TITLE_VISIBLE, Enums.PalettePresenterType.NOTHING.name()).apply();
        }
        if (!mSharedPrefs.contains(Constants.PALETTE_TITLE_UNSELECTED)) {
            mSharedPrefs.edit().putString(Constants.PALETTE_TITLE_UNSELECTED, "").apply();
        }
        if (!mSharedPrefs.contains(Constants.PALETTE_TITLE_SELECTED)) {
            mSharedPrefs.edit().putString(Constants.PALETTE_TITLE_UNSELECTED, "").apply();
        }
        if (!mSharedPrefs.contains(Constants.PALETTE_CONTENT_VISIBLE)) {
            mSharedPrefs.edit().putString(Constants.PALETTE_CONTENT_VISIBLE, Enums.PalettePresenterType.NOTHING.name()).apply();
        }
        if (!mSharedPrefs.contains(Constants.PALETTE_CONTENT_UNSELECTED)) {
            mSharedPrefs.edit().putString(Constants.PALETTE_CONTENT_UNSELECTED, "").apply();
        }
        if (!mSharedPrefs.contains(Constants.PALETTE_CONTENT_SELECTED)) {
            mSharedPrefs.edit().putString(Constants.PALETTE_CONTENT_UNSELECTED, "").apply();
        }
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

    private void saveCustomizations() {
        switch(Enums.PalettePresenterType.valueOf(mBackgroundVisibility.getSelectedItem().toString().replace(" ", "").toUpperCase())) {
            case NOTHING:
                saveSetting(Constants.PALETTE_BACKGROUND_VISIBLE, Enums.PalettePresenterType.NOTHING.name());
                saveSetting(Constants.PALETTE_BACKGROUND_UNSELECTED, "");
                saveSetting(Constants.PALETTE_BACKGROUND_SELECTED, "");
                break;
            case FOCUSEDCARD:
                saveSetting(Constants.PALETTE_BACKGROUND_VISIBLE, Enums.PalettePresenterType.FOCUSEDCARD.name());
                saveSetting(Constants.PALETTE_BACKGROUND_UNSELECTED, "");
                saveSetting(Constants.PALETTE_BACKGROUND_SELECTED, Enums.PaletteColor.valueOf(
                        mBackgroundSelected.getSelectedItem().toString().replace(" ", "").toUpperCase()).name());
                break;
            case ALLCARDS:
                saveSetting(Constants.PALETTE_BACKGROUND_VISIBLE, Enums.PalettePresenterType.ALLCARDS.name());
                saveSetting(Constants.PALETTE_BACKGROUND_UNSELECTED, Enums.PaletteColor.valueOf(
                        mBackgroundUnselected.getSelectedItem().toString().replace(" ", "").toUpperCase()).name());
                saveSetting(Constants.PALETTE_BACKGROUND_SELECTED, Enums.PaletteColor.valueOf(
                        mBackgroundSelected.getSelectedItem().toString().replace(" ", "").toUpperCase()).name());
                break;
        }
        switch(Enums.PalettePresenterType.valueOf(mContentVisibility.getSelectedItem().toString().replace(" ", "").toUpperCase())) {
            case NOTHING:
                saveSetting(Constants.PALETTE_CONTENT_VISIBLE, Enums.PalettePresenterType.NOTHING.name());
                saveSetting(Constants.PALETTE_CONTENT_UNSELECTED, "");
                saveSetting(Constants.PALETTE_CONTENT_SELECTED, "");
                break;
            case FOCUSEDCARD:
                saveSetting(Constants.PALETTE_CONTENT_VISIBLE, Enums.PalettePresenterType.FOCUSEDCARD.name());
                saveSetting(Constants.PALETTE_CONTENT_UNSELECTED, "");
                saveSetting(Constants.PALETTE_CONTENT_SELECTED, Enums.PaletteColor.valueOf(
                        mContentSelected.getSelectedItem().toString().replace(" ", "").toUpperCase()).name());
                break;
            case ALLCARDS:
                saveSetting(Constants.PALETTE_CONTENT_VISIBLE, Enums.PalettePresenterType.ALLCARDS.name());
                saveSetting(Constants.PALETTE_CONTENT_UNSELECTED, Enums.PaletteColor.valueOf(
                        mContentUnselected.getSelectedItem().toString().replace(" ", "").toUpperCase()).name());
                saveSetting(Constants.PALETTE_CONTENT_SELECTED, Enums.PaletteColor.valueOf(
                        mContentSelected.getSelectedItem().toString().replace(" ", "").toUpperCase()).name());
                break;
        }
        switch(Enums.PalettePresenterType.valueOf(mTitleVisibility.getSelectedItem().toString().replace(" ", "").toUpperCase())) {
            case NOTHING:
                saveSetting(Constants.PALETTE_TITLE_VISIBLE, Enums.PalettePresenterType.NOTHING.name());
                saveSetting(Constants.PALETTE_TITLE_UNSELECTED, "");
                saveSetting(Constants.PALETTE_TITLE_SELECTED, "");
                break;
            case FOCUSEDCARD:
                saveSetting(Constants.PALETTE_TITLE_VISIBLE, Enums.PalettePresenterType.FOCUSEDCARD.name());
                saveSetting(Constants.PALETTE_TITLE_UNSELECTED, "");
                saveSetting(Constants.PALETTE_TITLE_SELECTED, Enums.PaletteColor.valueOf(
                        mTitleSelected.getSelectedItem().toString().replace(" ", "").toUpperCase()).name());
                break;
            case ALLCARDS:
                saveSetting(Constants.PALETTE_TITLE_VISIBLE, Enums.PalettePresenterType.ALLCARDS.name());
                saveSetting(Constants.PALETTE_TITLE_UNSELECTED, Enums.PaletteColor.valueOf(
                        mTitleUnselected.getSelectedItem().toString().replace(" ", "").toUpperCase()).name());
                saveSetting(Constants.PALETTE_TITLE_SELECTED, Enums.PaletteColor.valueOf(
                        mTitleSelected.getSelectedItem().toString().replace(" ", "").toUpperCase()).name());
                break;
        }
    }

    @OnClick(R.id.btn_save)
    public void onOkClick() {
        saveCustomizations();
        mOnSaveListener.onSaveCustomization();
        getDialog().dismiss();
    }

    @OnClick(R.id.cancel_button)
    public void onCancelClick() {
        getDialog().dismiss();
    }

    private void saveSetting(String key, String value) {
        mSharedPrefs.edit().putString(key, value).apply();
    }

    private int getTypePosition(Enums.PalettePresenterType type) {
        switch(type) {
            case NOTHING:
                return 0;
            case ALLCARDS:
                return 2;
            default:
                return 1;
        }
    }

    private int getColorPosition(Enums.PaletteColor color) {
        switch(color) {
            case DARKVIBRANT:
                return 4;
            case DARKMUTED:
                return 1;
            case LIGHTMUTED:
                return 0;
            case LIGHTVIBRANT:
                return 3;
            case MUTED:
                return 2;
            case VIBRANT:
                return 5;
            default:
                return -1;
        }
    }

    private void loadSettings() {
        switch(Enums.PalettePresenterType.valueOf(mSharedPrefs.getString(Constants.PALETTE_BACKGROUND_VISIBLE, ""))) {
            case NOTHING:
                mBackgroundVisibility.setSelection(getTypePosition(Enums.PalettePresenterType.NOTHING));
                break;
            case FOCUSEDCARD:
                mBackgroundVisibility.setSelection(getTypePosition(Enums.PalettePresenterType.FOCUSEDCARD));
                mBackgroundSelected.setSelection(getColorPosition(Enums.PaletteColor.valueOf(mSharedPrefs.getString(Constants.PALETTE_BACKGROUND_SELECTED, ""))));
                break;
            case ALLCARDS:
                mBackgroundVisibility.setSelection(getTypePosition(Enums.PalettePresenterType.ALLCARDS));
                mBackgroundSelected.setSelection(getColorPosition(Enums.PaletteColor.valueOf(mSharedPrefs.getString(Constants.PALETTE_BACKGROUND_SELECTED, ""))));
                mBackgroundUnselected.setSelection(getColorPosition(Enums.PaletteColor.valueOf(mSharedPrefs.getString(Constants.PALETTE_BACKGROUND_UNSELECTED, ""))));
                break;
        }
        switch(Enums.PalettePresenterType.valueOf(mSharedPrefs.getString(Constants.PALETTE_CONTENT_VISIBLE, ""))) {
            case NOTHING:
                mContentVisibility.setSelection(getTypePosition(Enums.PalettePresenterType.NOTHING));
                break;
            case FOCUSEDCARD:
                mContentVisibility.setSelection(getTypePosition(Enums.PalettePresenterType.FOCUSEDCARD));
                mContentSelected.setSelection(getColorPosition(Enums.PaletteColor.valueOf(mSharedPrefs.getString(Constants.PALETTE_CONTENT_SELECTED, ""))));
                break;
            case ALLCARDS:
                mContentVisibility.setSelection(getTypePosition(Enums.PalettePresenterType.ALLCARDS));
                mContentSelected.setSelection(getColorPosition(Enums.PaletteColor.valueOf(mSharedPrefs.getString(Constants.PALETTE_CONTENT_SELECTED, ""))));
                mContentUnselected.setSelection(getColorPosition(Enums.PaletteColor.valueOf(mSharedPrefs.getString(Constants.PALETTE_CONTENT_UNSELECTED, ""))));
                break;
        }
        switch(Enums.PalettePresenterType.valueOf(mSharedPrefs.getString(Constants.PALETTE_TITLE_VISIBLE, ""))) {
            case NOTHING:
                mTitleVisibility.setSelection(getTypePosition(Enums.PalettePresenterType.NOTHING));
                break;
            case FOCUSEDCARD:
                mTitleVisibility.setSelection(getTypePosition(Enums.PalettePresenterType.FOCUSEDCARD));
                mTitleSelected.setSelection(getColorPosition(Enums.PaletteColor.valueOf(mSharedPrefs.getString(Constants.PALETTE_TITLE_SELECTED, ""))));
                break;
            case ALLCARDS:
                mTitleVisibility.setSelection(getTypePosition(Enums.PalettePresenterType.ALLCARDS));
                mTitleSelected.setSelection(getColorPosition(Enums.PaletteColor.valueOf(mSharedPrefs.getString(Constants.PALETTE_TITLE_SELECTED, ""))));
                mTitleUnselected.setSelection(getColorPosition(Enums.PaletteColor.valueOf(mSharedPrefs.getString(Constants.PALETTE_TITLE_UNSELECTED, ""))));
                break;
        }
    }
}
