package com.haha01haha01.hacontacts;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContentUtility
{
	private ContentResolver cr;
	
	// Initialize a ContentUtility from the Activity's ContactResolver
	public ContentUtility(ContentResolver cr)
	{
		this.cr = cr;
	}
	
	// Get the value of column in the specified group ID
	protected String getGroupColumn(Long id, String column)
    {
    	Cursor data = cr.query(ContactsContract.Groups.CONTENT_URI, new String[] { column }, ContactsContract.Groups._ID + "= ?", new String[] { id.toString() }, null);
    	assert (data.getCount() == 1);
    	data.moveToFirst();
    	return data.getString(data.getColumnIndex(column));
    }

	// Gets the name of the group with the specified group ID
	public String getGroupName(Long id)
    {
    	return getGroupColumn(id, ContactsContract.Groups.TITLE);
    }
	
	// Gets the account name of the group with the specified group ID
	public String getGroupAccountName(Long id)
    {
    	return getGroupColumn(id, ContactsContract.Groups.ACCOUNT_NAME);
    }
	
	// Gets the account type of the group with the specified group ID
	public String getGroupAccountType(Long id)
    {
    	return getGroupColumn(id, ContactsContract.Groups.ACCOUNT_TYPE);
    }
	
	// Gets all the contact IDs registered to the specified group ID
	public List<Long> getGroupContactIDs(Long group_id)
    {
		List<Long> result = new ArrayList<Long>();
		String query = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + " = ?";
		String[] query_params = new String[] { ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE, group_id.toString() };
		String sort_order = ContactsContract.Data.CONTACT_ID + " ASC";
		Cursor data = cr.query(ContactsContract.Data.CONTENT_URI, new String[] { ContactsContract.Data.CONTACT_ID }, query, query_params, sort_order);
		Integer column_index = data.getColumnIndex(ContactsContract.Data.CONTACT_ID);
    	for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
    		result.add(data.getLong(column_index));
    	}
    	return result;
    }
	
	// Gets the ringtone of the specified contact ID
	public Uri getContactRingtone(Long id)
	{
		Cursor data = cr.query(ContactsContract.Contacts.CONTENT_URI, new String[] { ContactsContract.Contacts.CUSTOM_RINGTONE }, ContactsContract.Contacts._ID + " = ?", new String[] { id.toString() }, null);
		assert (data.getCount() == 1);
		data.moveToFirst();
		String uri_string = data.getString(data.getColumnIndex(ContactsContract.Contacts.CUSTOM_RINGTONE));
		return (uri_string == null)? null : Uri.parse(uri_string);
	}
	
	// Gets the RAW contact IDs of the specified contact IDs
	// * If group_id is not null, only returns raw contacts whose accounts match the group's account. 
	public List<Long> getRawContactsFromContacts(List<Long> contact_ids, Long group_id)
	{
		List<Long> raw_contact_ids = new ArrayList<Long>();
		String query = ContactsContract.RawContacts.CONTACT_ID + "= ?";
		String[] queryParams; 
		
		// Prepare the queries
		if (group_id != null) {
			// Get the group account info 
			String account_name = getGroupAccountName(group_id);
			String account_type = getGroupAccountType(group_id);
			
			// Android is fucking stupid, and you cannot pass nulls as selection params. So you need to craft
			// a special query for null cases ("x IS NULL").
			assert !(account_name == null ^ account_type == null);
			if (account_name == null && account_type == null) {
				query += " AND " + ContactsContract.RawContacts.ACCOUNT_NAME + " IS NULL AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + " IS NULL";
				queryParams = new String[] { "" };
			} else {
				query += " AND " + ContactsContract.RawContacts.ACCOUNT_NAME + " = ? AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + " = ?";
				queryParams = new String[] { "", account_name, account_type };
			}
		} else {
			queryParams = new String[] { "" };
		}
		
		// Perform the queries
		for (Long cid : contact_ids) {
			queryParams[0] = cid.toString();
			Cursor data = cr.query(ContactsContract.RawContacts.CONTENT_URI, new String[] { ContactsContract.RawContacts._ID }, query, queryParams, null);
			Integer id_index = data.getColumnIndex(ContactsContract.RawContacts._ID);
	    	for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
	    		raw_contact_ids.add(data.getLong(id_index));
	    	}
		}
    	return raw_contact_ids;
	}
}