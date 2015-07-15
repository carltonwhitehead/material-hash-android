package com.cebesius.materialhash.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.cebesius.materialhash.util.BusSingleton;

public class SingleChoiceDialogFragment extends DialogFragment {

    public static final int CHECKED_ITEM_NONE = -1;

    private static final String ARG_ITEMS = "items";
    private static final String ARG_CHECKED_ITEM = "checkedItem";

    public static SingleChoiceDialogFragment newInstance(CharSequence[] items, int checkedItem) {
        SingleChoiceDialogFragment fragment = new SingleChoiceDialogFragment();
        Bundle args = new Bundle();
        args.putCharSequenceArray(ARG_ITEMS, items);
        args.putInt(ARG_CHECKED_ITEM, checkedItem);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle args = getArguments();
        builder.setSingleChoiceItems(
            args.getCharSequenceArray(ARG_ITEMS),
            args.getInt(ARG_CHECKED_ITEM),
            (dialog, which) -> {
                dialog.dismiss();
                BusSingleton.getInstance().post(new ChoiceEvent(getTag(), which));
            }
        );
        return builder.create();
    }

    public static class ChoiceEvent {
        private final String tag;
        private final int checkedItem;

        public ChoiceEvent(String tag, int checkedItem) {
            this.tag = tag;
            this.checkedItem = checkedItem;
        }

        public String getTag() {
            return tag;
        }

        public int getCheckedItem() {
            return checkedItem;
        }
    }
}
