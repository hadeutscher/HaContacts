package com.haha01haha01.hacontacts;

import android.os.Parcel;
import android.os.Parcelable;

public class HaRuntimeSettings implements Parcelable
{
	public Boolean selected;
	public Long id;
	public Boolean match_gid;
	public Boolean caller_is_syncadapter;
	
	// Initialize default settings
	public HaRuntimeSettings()
	{
		selected = false;
		id = 0L;
		match_gid = true;
		caller_is_syncadapter = true;
	}
	
	// Initialize settings from parcel
	public HaRuntimeSettings(Parcel in)
	{
		selected = in.readInt() != 0;
		id = in.readLong();
		match_gid = in.readInt() != 0;
		caller_is_syncadapter = in.readInt() != 0;
	}

	
	// Overrides
    @Override 
    public int describeContents() 
    { 
    	return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
    	dest.writeInt(selected? 1 : 0);
    	dest.writeLong(id);
    	dest.writeInt(match_gid? 1 : 0);
    	dest.writeInt(caller_is_syncadapter? 1 : 0);
    }
    
	public static final Parcelable.Creator<HaRuntimeSettings> CREATOR = new Parcelable.Creator<HaRuntimeSettings>() {
		public HaRuntimeSettings createFromParcel(Parcel in) {
			return new HaRuntimeSettings(in);
		}

		public HaRuntimeSettings[] newArray(int size) {
			return new HaRuntimeSettings[size];
		}
	};
	
	// Set settings to reflect the specified group id being selected
	public void selectGroup(Long id)
	{
		this.id = id;
		this.selected = true;
	}
	
	// Flip the Match GID setting
	public void flipMatch()
	{
		match_gid = !match_gid;
	}
	
	// Flip the CALLER_IS_SYNCADAPTER setting
	public void flipSyncadapter()
	{
		caller_is_syncadapter = !caller_is_syncadapter;
	}
}