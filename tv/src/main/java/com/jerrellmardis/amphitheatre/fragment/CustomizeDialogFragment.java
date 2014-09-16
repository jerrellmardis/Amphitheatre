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
import com.jerrellmardis.amphitheatre.util.Utils;

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

    @InjectView(R.id.blur_type) Spinner mBlurType;

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

        Utils.checkPrefs(mSharedPrefs);
        loadSettings();
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

    private void saveCustomizations() {
        switch(Enums.PalettePresenterType.valueOf(mBackgroundVisibility.getSelectedItemPosition())) {
            case NOTHING:
                saveSetting(Constants.PALETTE_BACKGROUND_VISIBLE, Enums.PalettePresenterType.NOTHING.name());
                saveSetting(Constants.PALETTE_BACKGROUND_UNSELECTED, "");
                saveSetting(Constants.PALETTE_BACKGROUND_SELECTED, "");
                break;
            case FOCUSEDCARD:
                saveSetting(Constants.PALETTE_BACKGROUND_VISIBLE, Enums.PalettePresenterType.FOCUSEDCARD.name());
                saveSetting(Constants.PALETTE_BACKGROUND_UNSELECTED, "");
                saveSetting(
                        Constants.PALETTE_BACKGROUND_SELECTED,
                        Enums.PaletteColor.valueOf(mBackgroundSelected.getSelectedItemPosition()).name());
                break;
            case ALLCARDS:
                saveSetting(Constants.PALETTE_BACKGROUND_VISIBLE, Enums.PalettePresenterType.ALLCARDS.name());
                saveSetting(
                        Constants.PALETTE_BACKGROUND_UNSELECTED,
                        Enums.PaletteColor.valueOf(mBackgroundUnselected.getSelectedItemPosition()).name());
                saveSetting(
                        Constants.PALETTE_BACKGROUND_SELECTED,
                        Enums.PaletteColor.valueOf(mBackgroundSelected.getSelectedItemPosition()).name());
                break;
        }
        switch(Enums.PalettePresenterType.valueOf(mContentVisibility.getSelectedItemPosition())) {
            case NOTHING:
                saveSetting(Constants.PALETTE_CONTENT_VISIBLE, Enums.PalettePresenterType.NOTHING.name());
                saveSetting(Constants.PALETTE_CONTENT_UNSELECTED, "");
                saveSetting(Constants.PALETTE_CONTENT_SELECTED, "");
                break;
            case FOCUSEDCARD:
                saveSetting(Constants.PALETTE_CONTENT_VISIBLE, Enums.PalettePresenterType.FOCUSEDCARD.name());
                saveSetting(Constants.PALETTE_CONTENT_UNSELECTED, "");
                saveSetting(
                        Constants.PALETTE_CONTENT_SELECTED,
                        Enums.PaletteColor.valueOf(mContentSelected.getSelectedItemPosition()).name());
                break;
            case ALLCARDS:
                saveSetting(Constants.PALETTE_CONTENT_VISIBLE, Enums.PalettePresenterType.ALLCARDS.name());
                saveSetting(
                        Constants.PALETTE_CONTENT_UNSELECTED,
                        Enums.PaletteColor.valueOf(mContentUnselected.getSelectedItemPosition()).name());
                saveSetting(
                        Constants.PALETTE_CONTENT_SELECTED,
                        Enums.PaletteColor.valueOf(mContentSelected.getSelectedItemPosition()).name());
                break;
        }
        switch(Enums.PalettePresenterType.valueOf(mTitleVisibility.getSelectedItemPosition())) {
            case NOTHING:
                saveSetting(Constants.PALETTE_TITLE_VISIBLE, Enums.PalettePresenterType.NOTHING.name());
                saveSetting(Constants.PALETTE_TITLE_UNSELECTED, "");
                saveSetting(Constants.PALETTE_TITLE_SELECTED, "");
                break;
            case FOCUSEDCARD:
                saveSetting(Constants.PALETTE_TITLE_VISIBLE, Enums.PalettePresenterType.FOCUSEDCARD.name());
                saveSetting(Constants.PALETTE_TITLE_UNSELECTED, "");
                saveSetting(
                        Constants.PALETTE_TITLE_SELECTED,
                        Enums.PaletteColor.valueOf(mTitleSelected.getSelectedItemPosition()).name());
                break;
            case ALLCARDS:
                saveSetting(Constants.PALETTE_TITLE_VISIBLE, Enums.PalettePresenterType.ALLCARDS.name());
                saveSetting(
                        Constants.PALETTE_TITLE_UNSELECTED,
                        Enums.PaletteColor.valueOf(mTitleUnselected.getSelectedItemPosition()).name());
                saveSetting(
                        Constants.PALETTE_TITLE_SELECTED,
                        Enums.PaletteColor.valueOf(mTitleSelected.getSelectedItemPosition()).name());
                break;
        }
        switch(Enums.BlurState.valueOf(mBlurType.getSelectedItemPosition())) {
            case ON:
                saveSetting(Constants.BACKGROUND_BLUR, Enums.BlurState.ON.name());
                break;
            case OFF:
                saveSetting(Constants.BACKGROUND_BLUR, Enums.BlurState.OFF.name());
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

    private void loadSettings() {
        switch(Enums.PalettePresenterType.valueOf(mSharedPrefs.getString(Constants.PALETTE_BACKGROUND_VISIBLE, ""))) {
            case NOTHING:
                mBackgroundVisibility.setSelection(Enums.PalettePresenterType.NOTHING.getPosition());
                break;
            case FOCUSEDCARD:
                mBackgroundVisibility.setSelection(Enums.PalettePresenterType.FOCUSEDCARD.getPosition());
                mBackgroundSelected.setSelection(Enums.PaletteColor.getPosition(mSharedPrefs.getString(Constants.PALETTE_BACKGROUND_SELECTED, "")));
                break;
            case ALLCARDS:
                mBackgroundVisibility.setSelection(Enums.PalettePresenterType.ALLCARDS.getPosition());
                mBackgroundSelected.setSelection(Enums.PaletteColor.getPosition(mSharedPrefs.getString(Constants.PALETTE_BACKGROUND_SELECTED, "")));
                mBackgroundUnselected.setSelection(Enums.PaletteColor.getPosition(mSharedPrefs.getString(Constants.PALETTE_BACKGROUND_UNSELECTED, "")));
                break;
        }
        switch(Enums.PalettePresenterType.valueOf(mSharedPrefs.getString(Constants.PALETTE_CONTENT_VISIBLE, ""))) {
            case NOTHING:
                mContentVisibility.setSelection(Enums.PalettePresenterType.NOTHING.getPosition());
                break;
            case FOCUSEDCARD:
                mContentVisibility.setSelection(Enums.PalettePresenterType.FOCUSEDCARD.getPosition());
                mContentSelected.setSelection(Enums.PaletteColor.getPosition(mSharedPrefs.getString(Constants.PALETTE_CONTENT_SELECTED, "")));
                break;
            case ALLCARDS:
                mContentVisibility.setSelection(Enums.PalettePresenterType.ALLCARDS.getPosition());
                mContentSelected.setSelection(Enums.PaletteColor.getPosition(mSharedPrefs.getString(Constants.PALETTE_CONTENT_SELECTED, "")));
                mContentUnselected.setSelection(Enums.PaletteColor.getPosition(mSharedPrefs.getString(Constants.PALETTE_CONTENT_UNSELECTED, "")));
                break;
        }
        switch(Enums.PalettePresenterType.valueOf(mSharedPrefs.getString(Constants.PALETTE_TITLE_VISIBLE, ""))) {
            case NOTHING:
                mTitleVisibility.setSelection(Enums.PalettePresenterType.NOTHING.getPosition());
                break;
            case FOCUSEDCARD:
                mTitleVisibility.setSelection(Enums.PalettePresenterType.FOCUSEDCARD.getPosition());
                mTitleSelected.setSelection(Enums.PaletteColor.getPosition(mSharedPrefs.getString(Constants.PALETTE_TITLE_SELECTED, "")));
                break;
            case ALLCARDS:
                mTitleVisibility.setSelection(Enums.PalettePresenterType.ALLCARDS.getPosition());
                mTitleSelected.setSelection(Enums.PaletteColor.getPosition(mSharedPrefs.getString(Constants.PALETTE_TITLE_SELECTED, "")));
                mTitleUnselected.setSelection(Enums.PaletteColor.getPosition(mSharedPrefs.getString(Constants.PALETTE_TITLE_UNSELECTED, "")));
                break;
        }
        switch (Enums.BlurState.valueOf(mSharedPrefs.getString(Constants.BACKGROUND_BLUR, ""))) {
            case OFF:
                mBlurType.setSelection(Enums.BlurState.OFF.getPosition());
                break;
            case ON:
                mBlurType.setSelection(Enums.BlurState.ON.getPosition());
                break;
        }
    }
}
