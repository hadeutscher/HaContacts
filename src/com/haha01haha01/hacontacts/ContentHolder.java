package com.haha01haha01.hacontacts;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ContentHolder implements Parcelable 
{
	public static final int TYPE_CONTACTS = 1;
	public static final int TYPE_GROUPS = 2;
	public static final int TYPE_RAW_CONTACTS = 3;
	public static final int TYPE_ENTITY = 4;
	public static final int TYPE_RAW_ENTITY = 5;
	
	private String[] projection;
	private String[][] table_data;
	private long[] ids;
	private int type;
	
	// Initialize ContentHolder from a URI, the projection parameters, the name of the ID column, the ContentUtility and the content type 
	public ContentHolder(Uri content_uri, String[] projection, String id_col, ContentResolver resolver, int type)
	{
		this(new Uri[] { content_uri }, projection, id_col, resolver, type);
	}
	
	// Initialize ContentHolder from multiple URIs, the projection parameters, the name of the ID column, the ContentUtility
	// and the content type
	public ContentHolder(Uri[] content_uris, String[] projection, String id_col, ContentResolver resolver, int type)
	{
		this.projection = projection;
		this.type = type;
		String sortOrder = projection[0] + " ASC";
		Cursor[] datas = new Cursor[content_uris.length];
		Integer total_size = 0;
		
		// Perform all queries at once, to know their sizes
		for(int i = 0; i < content_uris.length; i++) {
			datas[i] = resolver.query(content_uris[i], projection, null, null, sortOrder);
			total_size += datas[i].getCount();
		}
		
		// Initialize the receiving variables
    	table_data = new String[total_size][projection.length];
    	ids = new long[total_size];
    	
    	// Parse the data
    	Integer row_pointer = 0;
    	for(int i = 0; i < content_uris.length; i++) {
    		Cursor data = datas[i];
	    	int id_col_idx = data.getColumnIndex(id_col);
	    	int y = 0;
	    	for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
	    		for (int x = 0; x < projection.length; x++) {
	    			table_data[row_pointer + y][x] = data.getString(x);
	    		}
	    		ids[row_pointer + y] = data.getLong(id_col_idx);
	    		y++;
	    	}
	    	row_pointer += data.getCount();
    	}
	}
	
	// Initialize ContentHolder from Parcel
	public ContentHolder(Parcel in)
	{
		projection = in.createStringArray();
		table_data = new String[in.readInt()][projection.length];
    	for (int i = 0; i < table_data.length; i++) {
    		in.readStringArray(table_data[i]);
    	}
    	type = in.readInt();
    	ids = in.createLongArray();
	}
	
	//Parcelable requirements
    @Override 
    public int describeContents() 
    { 
    	return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
    	dest.writeStringArray(projection);
    	dest.writeInt(table_data.length);
    	for (int i = 0; i < table_data.length; i++) {
    		dest.writeStringArray(table_data[i]);
    	}
    	dest.writeInt(type);
    	dest.writeLongArray(ids);
    }
    
	public static final Parcelable.Creator<ContentHolder> CREATOR = new Parcelable.Creator<ContentHolder>() {
		public ContentHolder createFromParcel(Parcel in) {
			return new ContentHolder(in);
		}

		public ContentHolder[] newArray(int size) {
			return new ContentHolder[size];
		}
	};
	
	// Maps the current content to a TableLayout
	public void mapToTable(TableLayout table, OnClickListener on_click, Activity context)
	{
		// Initialize table and head row
		table.removeAllViews();
		TableRow title = new TableRow(context);
    	for (int i = 0; i < projection.length; i++) {
    		title.addView(makeTextView(projection[i], on_click, context));
    	}
    	table.addView(title);
    	
    	// Insert the data
    	for (int y = 0; y < table_data.length; y++) {
    		TableRow row = new TableRow(context);
    		row.setTag(new RowTag(false, ids[y]));
    		for (int x = 0; x < table_data[y].length; x++) {
    			row.addView(makeTextView(table_data[y][x], on_click, context));
    		}
    		table.addView(row);
    	}
	}
	
	// Creates a generic TextView cell
    public TextView makeTextView(String text, OnClickListener on_click, Activity context)
    {
    	TextView txt = new TextView(context);
    	txt.setBackgroundResource(R.drawable.cell_shape);
    	txt.setPadding(5, 5, 5, 5);
    	txt.setOnClickListener(on_click);
    	txt.setTextAppearance(context, android.R.style.TextAppearance_Medium);
    	txt.setTextColor(Color.WHITE);
    	txt.setText(text);
    	return txt;
    }

    // Accessor for type
	public int getType() 
	{
		return type;
	}
	
	// Accessor for length
	public int getLength()
	{
		return table_data.length;
	}
}