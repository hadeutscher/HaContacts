package com.haha01haha01.hacontacts;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;

public class ContactDeleter
{
	private List<Long> contact_ids;
	private HaActivity context;
	private Boolean syncadapter;
	
	// Initialize ContactDeleter from list of contact IDs to delete, setting whether to impersonate SyncAdapter, and parent Activity
	public ContactDeleter(List<Long> contact_ids, Boolean syncadapter, HaActivity context)
	{
		this.contact_ids = contact_ids;
		this.context = context;
		this.syncadapter = syncadapter;
		warnDeleteContacts();
	}
	
	// Shows the Yes\No warning dialog
	private void warnDeleteContacts()
    {
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	        	deleteContacts();
    	            break;

    	        case DialogInterface.BUTTON_NEGATIVE:
    	            break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setMessage("Delete " + contact_ids.toString() + "?").setPositiveButton("Yes", dialogClickListener)
    	    .setNegativeButton("No", dialogClickListener).show();
    }
	
	// Does the actual deletion
	private void deleteContacts()
    {
    	context.debug("Deleting " + contact_ids.toString());
    	ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
    	Uri uri = syncadapter? ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build() : ContactsContract.RawContacts.CONTENT_URI;
    	for (Long cid : contact_ids) {
    		ops.add(ContentProviderOperation.newDelete(uri)
    				.withSelection(ContactsContract.RawContacts._ID + "= ?", new String[] { cid.toString() })
    				.build());
    	}
    	try {
			context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	context.loadContacts();
    }
}