package edu.oakland.lifestory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MessageDialogFragment extends DialogFragment {
    Button dialogAction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_message_dialog, container, false);
        getDialog().setTitle("Simple Dialog");
        dialogAction = rootView.findViewById(R.id.dialogAction);
        dialogAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Close", Toast.LENGTH_SHORT).show();
                dismiss();
                Intent homeIntent = new Intent("edu.oakland.lifestory.ReturnHome");
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                homeIntent.putExtra("DialogAction", "DialogClose");
                rootView.getContext().startActivity(homeIntent);
            }
        });
        return rootView;
    }
}
