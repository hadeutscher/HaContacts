package com.haha01haha01.hacontacts;

import android.net.Uri;
import android.provider.ContactsContract;

// This static class holds static information, such as projections and URIs of certain application queries
public final class StaticInformation 
{
	public static final String[] contact_projection = new String[] {
    	ContactsContract.Contacts._ID,
    	ContactsContract.Contacts.DISPLAY_NAME,
    	ContactsContract.Contacts.TIMES_CONTACTED,
    	ContactsContract.Contacts.LAST_TIME_CONTACTED,
    	ContactsContract.Contacts.CUSTOM_RINGTONE,
    	ContactsContract.Contacts.SEND_TO_VOICEMAIL,
    	ContactsContract.Contacts.STARRED,
    	ContactsContract.Contacts.HAS_PHONE_NUMBER,
    	ContactsContract.Contacts.IN_VISIBLE_GROUP,
    	ContactsContract.Contacts.LOOKUP_KEY
    };
    
    public static final Uri contact_content_uri = ContactsContract.Contacts.CONTENT_URI;
    public static final String contact_id_column = ContactsContract.Contacts._ID;
    
    public static final String[] raw_contact_projection = new String[] {
    	ContactsContract.RawContacts._ID,
    	ContactsContract.RawContacts.CONTACT_ID,
    	ContactsContract.RawContacts.AGGREGATION_MODE,
    	ContactsContract.RawContacts.DELETED,
    	ContactsContract.RawContacts.TIMES_CONTACTED,
    	ContactsContract.RawContacts.LAST_TIME_CONTACTED,
    	ContactsContract.RawContacts.STARRED,
    	ContactsContract.RawContacts.CUSTOM_RINGTONE,
    	ContactsContract.RawContacts.SEND_TO_VOICEMAIL,
    	ContactsContract.RawContacts.ACCOUNT_NAME,
    	ContactsContract.RawContacts.ACCOUNT_TYPE,
    	ContactsContract.RawContacts.SOURCE_ID,
    	ContactsContract.RawContacts.VERSION,
    	ContactsContract.RawContacts.DIRTY
    };
    
    public static final Uri raw_contact_content_uri = ContactsContract.RawContacts.CONTENT_URI;
    public static final String raw_contact_id_column = ContactsContract.RawContacts._ID;
    
    public static final String[] group_projection = new String[] {
    	ContactsContract.Groups._ID,
    	ContactsContract.Groups.TITLE,
    	ContactsContract.Groups.DELETED,
    	ContactsContract.Groups.GROUP_VISIBLE,
    	ContactsContract.Groups.NOTES,
    	ContactsContract.Groups.SHOULD_SYNC,
    	ContactsContract.Groups.SUMMARY_COUNT,
    	ContactsContract.Groups.SUMMARY_WITH_PHONES,
    	ContactsContract.Groups.SYSTEM_ID,
    	ContactsContract.Groups.ACCOUNT_NAME,
    	ContactsContract.Groups.ACCOUNT_TYPE,
    	ContactsContract.Groups.DIRTY,
    	ContactsContract.Groups.SOURCE_ID,
    	ContactsContract.Groups.VERSION
    };
    
    public static final Uri group_content_uri = ContactsContract.Groups.CONTENT_SUMMARY_URI;
    public static final String group_id_column = ContactsContract.Groups._ID;
    
    public static final String[] raw_entity_projection = new String[] {
    	ContactsContract.RawContacts.CONTACT_ID,
    	ContactsContract.RawContacts.Entity._ID,
    	ContactsContract.RawContacts.Entity.DATA_ID,
    	ContactsContract.RawContacts.Entity.MIMETYPE,
    	ContactsContract.RawContacts.Entity.DATA1,
    	ContactsContract.RawContacts.Entity.DATA2,
    	ContactsContract.RawContacts.Entity.DATA3,
    	ContactsContract.RawContacts.DELETED,
    	ContactsContract.RawContacts.ACCOUNT_NAME,
    	ContactsContract.RawContacts.ACCOUNT_TYPE,
    	ContactsContract.RawContacts.DIRTY,
    	ContactsContract.RawContacts.SOURCE_ID,
    	ContactsContract.RawContacts.VERSION
    };
    public static final String raw_entity_id_column = ContactsContract.RawContacts.Entity._ID;
}