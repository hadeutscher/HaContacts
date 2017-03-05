package com.haha01haha01.hacontacts;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.InputType;
import android.widget.EditText;

public class GroupAdder
{
	private HaActivity context;
	
	// Initializes GroupAdder from the parent Activity
	public GroupAdder(HaActivity context)
	{
		this.context = context;
		askGroupName();
	}
	
	// Shows the input dialog for the group name
    private void askGroupName()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setTitle("Insert group name");

    	// Set up the input
    	final EditText input = new EditText(context);
    	// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
    	input.setInputType(InputType.TYPE_CLASS_TEXT);
    	builder.setView(input);

    	// Set up the buttons
    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        addGroup(input.getText().toString());
    	    }
    	});
    	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        dialog.cancel();
    	    }
    	});

    	builder.show();
    }
    
    // Adds the group
    private void addGroup(String group_name)
    {
    	context.debug("Adding " + group_name);
    	ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
    	Uri uri = ContactsContract.Groups.CONTENT_URI; 
  		ops.add(ContentProviderOperation.newInsert(uri).withValue(ContactsContract.Groups.TITLE, group_name).build());
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