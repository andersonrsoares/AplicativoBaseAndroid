package br.com.anderson.aplicativobase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class DefaultDialog extends DialogFragment {
	private String title;
	private String message;
	private String bt1Title;
	private String bt2Title;
	public OnDialogButtonClick onClick;
	
	public static final DefaultDialog newInstance(String title, String message, String bt1Title, String bt2Title, OnDialogButtonClick onClick)
	{
		DefaultDialog fragment = new DefaultDialog();
		fragment.onClick = onClick;
	    Bundle bundle = new Bundle();
	    bundle.putString("title", title);
	    bundle.putString("message", message);
	    bundle.putString("bt1Title", bt1Title);
	    bundle.putString("bt2Title", bt2Title);
	    
	    fragment.setArguments(bundle);
	    return fragment ;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (this.getArguments() != null) {
			title = this.getArguments().getString("title");
			message = this.getArguments().getString("message");
			bt1Title = this.getArguments().getString("bt1Title");
			bt2Title = this.getArguments().getString("bt2Title");
		}
		
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(message)
               .setPositiveButton(bt1Title, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   onClick.onPositiveClick();
                   }
               })
               .setNegativeButton(bt2Title, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   onClick.onNegativeClick();
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }

	public interface OnDialogButtonClick {
		public void onPositiveClick();
		public void onNegativeClick();
	}



}
