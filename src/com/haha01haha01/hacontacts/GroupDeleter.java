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

public class GroupDeleter
{
	private HaActivity context;
	private List<Long> group_ids;
	private Boolean syncadapter;
	
	// Initializes GroupDeleter from the list of group IDs to delete,  setting whether to impersonate SyncAdapter, and parent Activity
	public GroupDeleter(List<Long> group_ids, Boolean syncadapter, HaActivity context)
	{
		this.context = context;
		this.group_ids = group_ids;
		this.syncadapter = syncadapter;
		warnRemoveGroups(group_ids.toString());
	}
	
	// Shows the Yes\No warning dialog
    private void warnRemoveGroups(String text)
    {
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	        	removeGroups();
    	            break;

    	        case DialogInterface.BUTTON_NEGATIVE:
    	            break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setMessage("Remove " + text + "?").setPositiveButton("Yes", dialogClickListener)
    	    .setNegativeButton("No", dialogClickListener).show();
    }
    
    // Does the actual deletion
    private void removeGroups()
    {
    	context.debug("Removed " + group_ids.toString());
    	ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
    	Uri uri = syncadapter? ContactsContract.Groups.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build() : ContactsContract.Groups.CONTENT_URI; 
   	
    	for (int i = 0; i < group_ids.size(); i++) {
    		ops.add(ContentProviderOperation.newDelete(uri).withSelection(ContactsContract.Groups._ID + "= ?", new String[] { group_ids.get(i).toString() }).build());
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
    	context.loadGroups();
    }
}