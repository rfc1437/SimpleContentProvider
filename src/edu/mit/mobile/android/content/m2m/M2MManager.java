package edu.mit.mobile.android.content.m2m;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.DBSortOrder;
import edu.mit.mobile.android.content.Manager;
import edu.mit.mobile.android.content.UriPath;

public class M2MManager implements Manager {
	private final Class<? extends ContentItem> mTo;
	private final String mPath;
	private final String mSortOrder;

	public M2MManager(Class<? extends ContentItem> to) {
		mTo = to;
		final UriPath path = mTo.getAnnotation(UriPath.class);
		mPath = path != null ? path.value() : null;
		final DBSortOrder sortOrder = mTo.getAnnotation(DBSortOrder.class);
		mSortOrder = sortOrder != null ? sortOrder.value() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.mobile.android.content.m2m.Manager#getUri(android.net.Uri)
	 */
	@Override
	public Uri getUri(Uri parent) {
		return Uri.withAppendedPath(parent, mPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.mobile.android.content.m2m.Manager#insert(android.content.ContentResolver,
	 * android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(ContentResolver cr, Uri parent, ContentValues cv) {
		return cr.insert(getUri(parent), cv);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.mobile.android.content.m2m.Manager#query(android.content.ContentResolver,
	 * android.net.Uri, java.lang.String[])
	 */
	@Override
	public Cursor query(ContentResolver cr, Uri parent, String[] projection) {
		return cr.query(getUri(parent), projection, null, null, mSortOrder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.mobile.android.content.m2m.Manager#getSortOrder()
	 */
	@Override
	public String getSortOrder() {
		return mSortOrder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.mobile.android.content.m2m.Manager#getPath()
	 */
	@Override
	public String getPath() {
		return mPath;
	}
}
