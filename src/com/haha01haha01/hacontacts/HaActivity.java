package com.haha01haha01.hacontacts;

import java.util.ArrayList;
import java.util.List;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HaActivity extends Activity 
{

	private OnClickListener on_click; 
	private ContentHolder content;
	private HaRuntimeSettings settings;
	private ContentUtility util;
	
	private final String SAVED_CONTENT_HOLDER = "HaContactsContentHolder";
	private final String SAVED_GROUP = "HaContactsChosenGroupIndex";

	// I am sincerely sorry for this ugly hack, but I did not find any other way to transfer the ID list
	// back to onActivityResult after I invoke the Ringtone intent
	private List<Long> temp_ids;
	
	//Overrides
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ha);
        
        // Initialize class variables
        on_click = new OnClickListener() { public void onClick(View v) { cellClick(v); } };
        content = null;
        settings = new HaRuntimeSettings();
        util = new ContentUtility(getContentResolver());
        // Restore saved instance or default to groups
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_CONTENT_HOLDER)) { 
        	content = (ContentHolder)savedInstanceState.getParcelable(SAVED_CONTENT_HOLDER);
        	putContent();
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_GROUP)) {
        	settings = (HaRuntimeSettings)savedInstanceState.getParcelable(SAVED_GROUP);
        } else {
        	loadGroups();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ha, menu);
        updateMenuTexts(menu.findItem(R.id.action_move));
        menu.findItem(R.id.setting_match_gid).setChecked(settings.match_gid);
        menu.findItem(R.id.setting_syncadapter).setChecked(settings.caller_is_syncadapter);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	List<Long> selected_ids = getSelectedIDs();;
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        	// Show Contacts
            case R.id.action_contacts:
            	loadContacts();
                break;
                
            // Raw Contacts
            case R.id.action_rawcontacts:
            	loadRawContacts();
            	break;
            	
            // Entitize
            case R.id.action_entitize:
            	if (selected_ids.size() == 0) {
            		break;
            	}
            	if (content.getType() == ContentHolder.TYPE_CONTACTS) {
            		rawEntitize(util.getRawContactsFromContacts(selected_ids, null));
            	} else if (content.getType() == ContentHolder.TYPE_RAW_CONTACTS) {
            		rawEntitize(selected_ids);
            	}
            	break;
            	
            // Show Groups
            case R.id.action_groups:
            	loadGroups();
            	break;
            	
            // Add Group
            case R.id.action_addgroup:
            	new GroupAdder(this);
            	break;
            	
            // Delete Selected
            case R.id.action_delete:
            	if (selected_ids.size() == 0) {
            		break;
            	}
            	if (content.getType() == ContentHolder.TYPE_GROUPS) {
            		new GroupDeleter(selected_ids, settings.caller_is_syncadapter, this);
            	} else if (content.getType() == ContentHolder.TYPE_CONTACTS) {
            		new ContactDeleter(util.getRawContactsFromContacts(selected_ids, null), settings.caller_is_syncadapter, this);
            	} else if (content.getType() == ContentHolder.TYPE_RAW_CONTACTS) {
            		new ContactDeleter(selected_ids, settings.caller_is_syncadapter, this);
            	}
            	break;
            	
            // Move To: 
            case R.id.action_move:
            	if (content.getType() == ContentHolder.TYPE_GROUPS) {
            		if (selected_ids.size() != 1) {
            			break;
            		}
            		settings.selectGroup(selected_ids.get(0));
            		updateMenuTexts(item);
            	} else if (content.getType() == ContentHolder.TYPE_CONTACTS) {
            		if (selected_ids.size() == 0) {
            			break;
            		}
            		Long group_id = settings.selected? settings.id : null;
            		String group_name = settings.selected? util.getGroupName(settings.id) : "<Null>";
            		new ContactMover(util.getRawContactsFromContacts(selected_ids, settings.match_gid? group_id : null), group_id, group_name, this);
            	}
            	break;
            	
            // Set Ringtone
            case R.id.action_ringtone:
            	if (content.getType() == ContentHolder.TYPE_CONTACTS) {
            		if (selected_ids.size() == 0) {
            			break;
            		}
            		changeRingtone(selected_ids);
            	} else if (content.getType() == ContentHolder.TYPE_GROUPS) {
            		if (selected_ids.size() != 1) {
            			break;
            		}
            		List<Long> ids = util.getGroupContactIDs(selected_ids.get(0));
            		if (ids.size() == 0) {
            			break;
            		}
            		changeRingtone(ids);
            	}
            	break;
            	
            // Toggle Selection
            case R.id.action_toggle:
            	toggleSelection();
            	break;
                
            // Cleanup Deleted
            case R.id.action_cleanup:
            	new ContactCleaner(this);
            	break;
                
            // Setting: Match GID
            case R.id.setting_match_gid:
            	settings.flipMatch();
            	item.setChecked(settings.match_gid);
            	break;
            	
            // Setting: CALLER_IS_SYNCADAPTER
            case R.id.setting_syncadapter:
            	settings.flipSyncadapter();
            	item.setChecked(settings.caller_is_syncadapter);
            	break;
            	
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
    	outState.putParcelable(SAVED_CONTENT_HOLDER, content);
    	outState.putParcelable(SAVED_GROUP, settings);
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK) {
	        switch (requestCode) {
	        case 1:
	            Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	            new RingtoneSetter(temp_ids, ringtone, this);
	            break;

	        default:
	            break;
	        }
	    }
	}

    // Sends debug text into debugText 
    protected void debug(Object text)
    {
    	TextView txt = (TextView)findViewById(R.id.debugText);
        txt.setText(text.toString());
    }
    
    // Handles a cell clicked
    protected void cellClick(View v)
    {
    	TableRow row = (TableRow)v.getParent();
    	Object data = row.getTag();
    	if (!(data instanceof RowTag)) {
    		return;
    	}
    	RowTag tag = (RowTag)data;
    	tag.selected = !tag.selected;
    	updateRowColor(row);
    }
    
    // Updates the color of a row to match its selected state
    protected void updateRowColor(TableRow row)
    {
    	if (!(row.getTag() instanceof RowTag)) {
    		return;
    	}
    	RowTag tag = (RowTag)row.getTag();
    	int resource = tag.selected? R.drawable.cell_shape_selected : R.drawable.cell_shape;
    	for (int i = 0; i < row.getChildCount(); i++) {
    		row.getChildAt(i).setBackgroundResource(resource);
    	}
    }
    
    // Starts the change ringtone process for the specified RAW contact IDs 
    protected void changeRingtone(List<Long> ids)
    {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		temp_ids = ids;
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, util.getContactRingtone(ids.get(0)));
		startActivityForResult(intent, 1);
    }
    
    // Puts the current content onto the TableLayout 
    private void putContent()
    {
    	content.mapToTable((TableLayout)findViewById(R.id.tableLayout), on_click, this);
    }
    
    // Retrieves a list of the selected item's IDs
    private List<Long> getSelectedIDs()
    {
    	TableLayout data_table = (TableLayout)findViewById(R.id.tableLayout);
    	
    	ArrayList<Long> result = new ArrayList<Long>();
    	for (int i = 0; i < data_table.getChildCount(); i++) {
   			if ((data_table.getChildAt(i).getTag() instanceof RowTag) && ((RowTag)data_table.getChildAt(i).getTag()).selected) {
   				result.add(((RowTag)data_table.getChildAt(i).getTag()).id);
    		}
    	}
    	return result;
    }
    
    // Toggles the selected state of all rows
    private void toggleSelection()
    {
    	TableLayout data_table = (TableLayout)findViewById(R.id.tableLayout);
    	for (int i = 0; i < data_table.getChildCount(); i++) {
   			if ((data_table.getChildAt(i).getTag() instanceof RowTag)) {
   				RowTag tag = (RowTag)data_table.getChildAt(i).getTag();
   				tag.selected = !tag.selected;
   				updateRowColor((TableRow)data_table.getChildAt(i));
    		}
    	}
    }
    
    // Starts the RAW entitization process for the specified RAW contact IDs
    protected void rawEntitize(List<Long> ids)
    {
    	if (ids.size() > 5) {
    		debug("Enititizing [...] as RAW");
    	} else { 
    		debug("Entitizing " + ids.toString() + " as RAW");
    	}
    	Uri[] uris = new Uri[ids.size()];
    	for (int i = 0; i < ids.size(); i++) {
	    	Uri contactUri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, ids.get(i));
	    	Uri entityUri = Uri.withAppendedPath(contactUri, ContactsContract.RawContacts.Entity.CONTENT_DIRECTORY);
	    	uris[i] = entityUri;
    	}
    	content = new ContentHolder(uris, StaticInformation.raw_entity_projection, StaticInformation.raw_entity_id_column, getContentResolver(), ContentHolder.TYPE_RAW_ENTITY);
    	putContent();
    }
    
    // Fetches the phone's groups onto ContentHolder and maps them on the screen
    protected void loadGroups()
    {
    	debug("Groups");
    	content = new ContentHolder(StaticInformation.group_content_uri, StaticInformation.group_projection, StaticInformation.group_id_column, this.getContentResolver(), ContentHolder.TYPE_GROUPS);
    	putContent();
    }

    // Fetches the phone's contacts onto ContentHolder and maps them on the screen
    protected void loadContacts()
    {
    	debug("Contacts");
    	content = new ContentHolder(StaticInformation.contact_content_uri, StaticInformation.contact_projection, StaticInformation.contact_id_column, this.getContentResolver(), ContentHolder.TYPE_CONTACTS);
    	putContent();
    }
    
    // Fetches the phone's RAW contacts onto ContentHolder and maps them on the screen
    protected void loadRawContacts()
    {
    	debug("Raw Contacts");
    	content = new ContentHolder(StaticInformation.raw_contact_content_uri, StaticInformation.raw_contact_projection, StaticInformation.raw_contact_id_column, this.getContentResolver(), ContentHolder.TYPE_RAW_CONTACTS);
    	putContent();
    }
    
    // Refreshes whatever content is currently viewed. If it is too complicated to refresh, switches to showing groups. 
    protected void refresh()
    {
    	switch (content.getType()) {
    	case ContentHolder.TYPE_CONTACTS:
    		loadContacts();
    		break;
    	case ContentHolder.TYPE_GROUPS:
    		loadGroups();
    		break;
    	case ContentHolder.TYPE_RAW_CONTACTS:
    		loadRawContacts();
    		break;
    	default:
    		loadGroups();
    		break;
    	}
    }
    
    // Updates the text on the "To: " menu item to match the current group selected 
	public void updateMenuTexts(MenuItem item)
	{
    	item.setTitle(settings.selected? "To: " + util.getGroupName(settings.id) : getResources().getString(R.string.action_move));
	}
}
