package com.haha01haha01.hacontacts;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;

public class RingtoneSetter
{
	private HaActivity context;
	private List<Long> ids;
	private Uri ringtone;
	
	// Initialize RingtoneSetter from the list of RAW contant IDs to change, the URI of the new ringtone and the parent Activity
	public RingtoneSetter(List<Long> ids, Uri ringtone, HaActivity context)
	{
		this.context = context;
		this.ids = ids;
		this.ringtone = ringtone;
		setRingtone();
	}
	
	// Sets the ringtone
	public void setRingtone()
	{
		context.debug("Set Ringtone");
    	ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
    	String ringtone_string = (ringtone == null)? null : ringtone.toString();
    	
  		for(Long id : ids) {
  			ops.add(ContentProviderOperation.newUpdate(ContactsContract.Contacts.CONTENT_URI)
  					.withSelection(ContactsContract.Contacts._ID + "= ?", new String[] { id.toString() })
  					.withValue(ContactsContract.Contacts.CUSTOM_RINGTONE, ringtone_string)
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
  		context.refresh();
	}
}