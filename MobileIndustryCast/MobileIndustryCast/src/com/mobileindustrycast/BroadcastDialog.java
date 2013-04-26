package com.mobileindustrycast;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

// Classical Alert dialog with three choices. 
// It is used for broadcast status selection in Chat Room class
public class BroadcastDialog extends DialogFragment{

	public interface NoticeDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);
		public void onDialogNeutralClick(DialogFragment dialog);
		public void onDialogNegativeClick(DialogFragment dialog);
	}

	NoticeDialogListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (NoticeDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()+" must implement NoticeDialogListener");
		}
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.dialog_broadcast)
		.setPositiveButton(R.string.Buy, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mListener.onDialogPositiveClick(BroadcastDialog.this);
			}
		})
		.setNeutralButton(R.string.Sell, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mListener.onDialogNeutralClick(BroadcastDialog.this);
			}
		})
		.setNegativeButton(R.string.Trade, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mListener.onDialogNegativeClick(BroadcastDialog.this);
			}
		});
		// Create the AlertDialog object and return it
				return builder.create();
	}
}
