package com.freeyuyuko.listencourse;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Constructor;

/**
 * Created by chengang on 16-1-2.
 */
public class InputDialog extends DialogFragment {

    private static final String TAG = "InputDialog";
    private EditText mEditView;

    public InputDialog(){

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
                mEditView = (EditText) constructor.newInstance(new Object[]{getActivity()});
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            mEditView = new EditText(getActivity());
            if (args.containsKey("inputType"))
                mEditView.setInputType(args.getInt("inputType"));
        }

        mEditView.setText(args.getString("input"));
        mEditView.setSelectAllOnFocus(true);
        builder.setView(mEditView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mEditView != null) {
                            String input = mEditView.getText().toString();
                            if (getActivity() instanceof CallBackInputFinished)
                                ((CallBackInputFinished) getActivity())
                                        .OnInputFinished(input, which);
                            else
                                Toast.makeText(getActivity(),
                                        "CallBackInputFinished not implemented.",
                                        Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() instanceof CallBackInputFinished)
                            ((CallBackInputFinished) getActivity())
                                    .OnInputFinished(null, which);
                        else
                            Toast.makeText(getActivity(),
                                    "CallBackInputFinished not implemented.",
                                    Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });
        return builder.create();
    }


    public interface CallBackInputFinished{
        void OnInputFinished(String input, int which);
    }
}
