package com.haha01haha01.hacontacts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactCleaner
{
	private HaActivity context;
	
	// Initialize ContactCleaner from parent Activity
	public ContactCleaner(HaActivity context)
	{
		this.context = context;
		warnCleanup();
	}
	
	// Shows the Yes\No warning dialog
    private void warnCleanup()
    {
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	        	cleanup();
    	            break;

    	        case DialogInterface.BUTTON_NEGATIVE:
    	            break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setMessage("Cleanup deleted entries (CALLER_IS_SYNCADAPTER)?").setPositiveButton("Yes", dialogClickListener)
    	    .setNegativeButton("No", dialogClickListener).show();
    }
    
    // Does the actual cleanup
    private void cleanup()
    {
    	context.debug("Cleaning up");

    	Uri contacts_uri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
    	Uri groups_uri = ContactsContract.Groups.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build(); 
    	context.getContentResolver().delete(contacts_uri, ContactsContract.RawContacts.DELETED + " = 1", new String[] { });
    	context.getContentResolver().delete(groups_uri, ContactsContract.Groups.DELETED + " = 1", new String[] { });
    	context.refresh();
    }
}