package edu.mit.mobile.android.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Database helper to make it easier to access foreign key relationships between a parent and a
 * child with a foreign key pointing to that parent.
 *
 * <pre>
 *      relation
 *          ↓
 * [parent] → [child]
 *          → [child 2]
 * </pre>
 *
 * For example, you could have an BlogPost that has a relation to multiple Comments.
 *
 * Unfortunately, if your version of SQLite doesn't support foreign keys (see
 * {@link AndroidVersions}), this will not automatically cascade deletes for you or verify any
 * relationships. It will otherwise function, though; you will just need to cascade deletes by hand.
 *
 * The query function supports wildcard parent IDs. So to select all the children with any parent,
 * you can use {@link #WILDCARD_PATH_SEGMENT} instead of the parent's ID number. Eg.a path of
 *
 * <pre>
 * /parent/&#42;/child/
 * </pre>
 *
 * to get a list of all the children in any parent.
 *
 * @author steve
 *
 */
public class ForeignKeyDBHelper extends GenericDBHelper {
	public static final String WILDCARD_PATH_SEGMENT = "_all";
	private final String mColumn;
	private final Class<? extends ContentItem> mChild;
	private final Class<? extends ContentItem> mParent;

	public ForeignKeyDBHelper(Class<? extends ContentItem> parent,
			Class<? extends ContentItem> child, String column) {
		super(child);
		mChild = child;
		mParent = parent;
		mColumn = column;
	}

	@Override
	public Uri insertDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
			ContentValues values) throws SQLException {
		final long parentId = Long.valueOf(ProviderUtils.getNthPathFromEnd(uri,
				1));
		values.put(mColumn, parentId);
		return super.insertDir(db, provider, uri, values);
	}

	@Override
	public int updateItem(SQLiteDatabase db, ContentProvider provider, Uri uri,
			ContentValues values, String where, String[] whereArgs) {
		final String parentId = ProviderUtils.getNthPathFromEnd(uri, 2);

		return super.updateItem(db, provider, uri, values,
				ProviderUtils.addExtraWhere(where, mColumn + "=?"),
				ProviderUtils.addExtraWhereArgs(whereArgs, parentId));
	}

	@Override
	public int updateDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
			ContentValues values, String where, String[] whereArgs) {
		final String parentId = ProviderUtils.getNthPathFromEnd(uri, 1);

		return super.updateDir(db, provider, uri, values,
				ProviderUtils.addExtraWhere(where, mColumn + "=?"),
				ProviderUtils.addExtraWhereArgs(whereArgs, parentId));
	}

	@Override
	public int deleteItem(SQLiteDatabase db, ContentProvider provider, Uri uri,
			String where, String[] whereArgs) {
		final String parentId = ProviderUtils.getNthPathFromEnd(uri, 2);

		return super.deleteItem(db, provider, uri,
				ProviderUtils.addExtraWhere(where, mColumn + "=?"),
				ProviderUtils.addExtraWhereArgs(whereArgs, parentId));
	}

	@Override
	public int deleteDir(SQLiteDatabase db, ContentProvider provider, Uri uri,
			String where, String[] whereArgs) {
		final String parentId = ProviderUtils.getNthPathFromEnd(uri, 1);

		return super.deleteDir(db, provider, uri,
				ProviderUtils.addExtraWhere(where, mColumn + "=?"),
				ProviderUtils.addExtraWhereArgs(whereArgs, parentId));
	}

	@Override
	public Cursor queryDir(SQLiteDatabase db, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		final String parentId = ProviderUtils.getNthPathFromEnd(uri, 1);

		if (WILDCARD_PATH_SEGMENT.equals(parentId)) {
			return super.queryDir(db, uri, projection, selection, selectionArgs, sortOrder);

		} else {
			return super.queryDir(db, uri, projection,
					ProviderUtils.addExtraWhere(selection, mColumn + "=?"),
					ProviderUtils.addExtraWhereArgs(selectionArgs, parentId), sortOrder);
		}
	}

	@Override
	public Cursor queryItem(SQLiteDatabase db, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		final String parentId = ProviderUtils.getNthPathFromEnd(uri, 2);

		if (WILDCARD_PATH_SEGMENT.equals(parentId)) {
			return super.queryItem(db, uri, projection, selection, selectionArgs, sortOrder);

		} else {
			return super.queryItem(db, uri, projection,
					ProviderUtils.addExtraWhere(selection, mColumn + "=?"),
					ProviderUtils.addExtraWhereArgs(selectionArgs, parentId), sortOrder);
		}
	}

	@Override
	public void createTables(SQLiteDatabase db) throws SQLGenerationException {
		if (!mChild.equals(mParent)) {
			super.createTables(db);
		}
	}

	@Override
	public void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (!mChild.equals(mParent)) {
			super.upgradeTables(db, oldVersion, newVersion);
		}
	}
}
