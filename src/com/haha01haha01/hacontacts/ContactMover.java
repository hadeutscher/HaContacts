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

public class ContactMover
{
	private List<Long> contact_ids;
	private Long group_id;
	private HaActivity context;
	
	// Initialize ContactMover from list of contact IDs to move, the group ID to move to, the group's name and the parent Activity
	public ContactMover(List<Long> contact_ids, Long group_id, String group_name, HaActivity context)
	{
		this.contact_ids = contact_ids;
		this.group_id = group_id;
		this.context = context;
		warnMoveContacts(group_name);
	}
	
	// Shows the Yes\No warning dialog
	private void warnMoveContacts(String group_name)
    {
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	        	moveContacts();
    	            break;

    	        case DialogInterface.BUTTON_NEGATIVE:
    	            break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setMessage("Move " + contact_ids.toString() + " to " + group_name + "?").setPositiveButton("Yes", dialogClickListener)
    	    .setNegativeButton("No", dialogClickListener).show();
    }
	
	// Does the actual moving
	private void moveContacts()
    {
    	context.debug("Moving " + contact_ids.toString());
    	ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
    	Uri uri = ContactsContract.Data.CONTENT_URI; 
    	for (Long cid : contact_ids) {
    		// This operation deletes all previous GroupMembership data entries for the contact
    		ops.add(ContentProviderOperation.newDelete(uri)
    				.withSelection(ContactsContract.Data.RAW_CONTACT_ID + "= ? AND " + ContactsContract.Data.MIMETYPE + "= ?", new String[] { cid.toString(), ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE })
    				.build());
    		if (group_id == null) {
    			continue;
    		}
    		// This operation inserts a new GroupMembership entry for the new group
    		ops.add(ContentProviderOperation.newInsert(uri)
    				.withValue(ContactsContract.Data.RAW_CONTACT_ID, cid)
    				.withValue(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, group_id)
    				.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
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