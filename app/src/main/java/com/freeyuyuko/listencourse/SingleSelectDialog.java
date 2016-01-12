package com.freeyuyuko.listencourse;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Created by chengang on 16-1-2.
 */
public class SingleSelectDialog extends DialogFragment {

    private static final String TAG = "SingleSelectDialog";
    private RadioGroup mRadioGroup;

    private CallBackSingleSelectFinished mCallback = null;

    public SingleSelectDialog(){

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreateDialog.");
        Bundle args = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(args.getString("title"))
                //.setIcon(savedInstanceState.getInt("icon"))
                .setMessage(args.getString("message"));

        String viewName = args.getString("viewName");
        if( viewName != null ){
            try {
                Class<?> clazz = Class.forName(viewName);
                Constructor<?> constructor = clazz.getConstructor(Context.class);
                mRadioGroup = (RadioGroup) constructor.newInstance(new Object[]{getActivity()});
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            mRadioGroup = new RadioGroup(getActivity());
        }
        mRadioGroup.setOrientation(RadioGroup.VERTICAL);
        List<String> list = args.getStringArrayList("list");
        if( list == null )
            Toast.makeText(getActivity(), "SingleSelect list is null.",
                    Toast.LENGTH_SHORT).show();
        else {
            for(int i = 0; i < list.size(); ++i ){
                RadioButton rb = new RadioButton(getActivity());
                rb.setText(list.get(i));
                mRadioGroup.addView(rb);
            }
            builder.setView(mRadioGroup)
                    .setPositiveButton("MOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String select = null;
                            if (mRadioGroup != null) {
                                int id = mRadioGroup.getCheckedRadioButtonId();
                                RadioButton rb = (RadioButton) mRadioGroup.findViewById(id);
                                if (rb != null) {
                                    select = rb.getText().toString();
                                }
                            }
                            if (getActivity() instanceof CallBackSingleSelectFinished)
                                ((CallBackSingleSelectFinished) getActivity())
                                        .OnSingleSelectFinished(select, true);
                            else
                                Toast.makeText(getActivity(),
                                        "CallBackSingleSelectFinished not implemented.",
                                        Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (getActivity() instanceof CallBackSingleSelectFinished)
                                ((CallBackSingleSelectFinished) getActivity())
                                        .OnSingleSelectFinished(null, false);
                            else
                                Toast.makeText(getActivity(),
                                        "CallBackSingleSelectFinished not implemented.",
                                        Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                        }
                    });
        }
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
//        if(mCallback != null)
//            mCallback.OnSingleSelectFinished();
    }

    public void setCallBackSingleSelectFinished(CallBackSingleSelectFinished callback){
        mCallback = callback;
    }


    public interface CallBackSingleSelectFinished{
        /**
         *
         * @param select nullable
         * @param bOk
         */
        void OnSingleSelectFinished(String select, boolean bOk);
    }
}
