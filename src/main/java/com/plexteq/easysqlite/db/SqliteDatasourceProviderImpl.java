/**
 * Copyright (c) 2014-2018, Plexteq OÜ
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.plexteq.easysqlite.db;

import static com.plexteq.easysqlite.db.QueryHelper.getQueryType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.plexteq.easysqlite.db.QueryHelper.QueryType;

public class SqliteDatasourceProviderImpl implements DatasourceProvider
{
	private SQLiteOpenHelper helper;
	private SQLiteDatabase database;
	
	private final String LOG_TAG = getClass().getName();
	private String databaseName;
	
	public SqliteDatasourceProviderImpl(Context ctx, String databaseName) {
		this.databaseName = databaseName;
		helper = new DatabaseHandler(databaseName, ctx, null);
		database = helper.getWritableDatabase();
	}
	
	@Override
	public void close()
	{
		database.close();
		helper.close();
	}
	
	/**
	 * Counts rows in a given table
	 * @param table
	 * @return
	 */
	public int getRowCount(String tableName)
	{
		Cursor rowCountCursor = null;
		
		// counting table rows
		try {
			rowCountCursor = database.rawQuery(
					String.format("SELECT count(*) FROM %s", tableName), null);
						
			rowCountCursor.moveToFirst();
			return rowCountCursor.getInt(0);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error counting rows in table " + tableName);
			return 0;
		} finally {
			closeQuite(rowCountCursor);
		}	
	}
	
	@Override
	public QueryResult execute(Query query)
	{
		QueryType queryType = getQueryType(query);
		switch (queryType)
		{
			case QUERY_EXTRACTION: 
				return executeSelectQuery(query);
			case QUERY_OTHER:
				return executeNonSelectQuery(query);
			default:
				return handleUnrecognizedQuery(query);
		}
	}
	
	public int getTableCount()
	{
		Cursor tableCountCursor = null;
		
		// counting tables
		try {
			tableCountCursor = database.rawQuery(String.format("SELECT count(*) FROM sqlite_master " +
							"WHERE type = 'table' AND name != 'android_metadata' AND name != 'sqlite_sequence'"), null);
			
			tableCountCursor.moveToFirst();
			return tableCountCursor.getInt(0);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Error counting tables in database " + databaseName);
			return 0;
		} finally {
			closeQuite(tableCountCursor);
		}	
	}
	
	@Override
	public Set<String> listTables()
	{
		Set<String> result = Collections.<String>emptySet();
		
		String tablesQuery = "SELECT name FROM sqlite_master " +
				"WHERE type = 'table' AND name != 'android_metadata' AND name != 'sqlite_sequence'";

		Cursor tablesIterator = null;
		try {
			tablesIterator = database.rawQuery(tablesQuery, null);
			if (tablesIterator.moveToFirst())
			{
				result = new HashSet<String>();
				do {
					String tableName = tablesIterator.getString(0);
					result.add(tableName);
				} while (tablesIterator.moveToNext());
			} 
		} finally {
			closeQuite(tablesIterator);
		}
		
		return result;
	}
	
	private void closeQuite(Cursor cursor)
	{
		try {
			if (cursor != null && cursor.isClosed() == false)
				cursor.close();
		} catch (Exception e) {
			Log.w(LOG_TAG, "Error closing cursor");
		}
	}

	
	protected QueryResult executeNonSelectQuery(Query query)
	{
		QueryResult result = new QueryResult();
		result.setTimestamp(TimeHelper.now());
		result.setData(Collections.<Map<String, String>>emptyList());
		result.setDb(databaseName);
		
		long queryStart = TimeHelper.nowMs();
		
		try
		{
			database.beginTransactionNonExclusive();
			
			for (String q : getQueries(query.getSql())) {
				database.execSQL(q);
			}
			
			database.setTransactionSuccessful();
			result.setStatus(SQL_SUCCESS);
		}
		catch (Exception e)
		{
			result.setStatus(SQL_ERROR);
			result.setError(e.getMessage());
		}
		finally {
			result.setDuration(TimeHelper.nowMs() - queryStart);
			database.endTransaction();
		}
		
		return result;
	}
	
	protected String[] getQueries(String sql) {
		return sql.split(SQL_QUERY_SEPARATOR);
	}
	
	protected QueryResult executeSelectQuery(Query query)
	{
		QueryResult result = new QueryResult();
		List<Map<String, String>> resultList = Collections.emptyList();
		
		result.setTimestamp(TimeHelper.now());
		result.setDb(databaseName);
			
		Cursor cursor = null;
		try
		{
			long queryStart = TimeHelper.nowMs();
			cursor = database.rawQuery(query.getSql(), null);
			result.setDuration(TimeHelper.nowMs() - queryStart);
			
			if (cursor.moveToFirst())
			{
				resultList = new ArrayList<Map<String,String>>();
				do {
					Map<String, String> rowData = new LinkedHashMap<String, String>();
					for (int i = 0 ; i < cursor.getColumnCount() ; i++) {
						rowData.put(cursor.getColumnName(i), cursor.getString(i));
					}
					resultList.add(rowData);
				} while (cursor.moveToNext());
			} 
			
			result.setStatus(SQL_SUCCESS);
		}
		catch (Exception e)
		{
			result.setStatus(SQL_ERROR);
			result.setError(e.getMessage());
			result.setDuration(0);
		} finally
		{
			closeQuite(cursor);
		}
		
		result.setData(resultList);
		result.setSize(resultList.size());
		
		return result;
	}
	
	protected QueryResult handleUnrecognizedQuery(Query query)
	{
	    QueryResult result = new QueryResult();
		result.setDb(databaseName);
		result.setData(Collections.<Map<String, String>>emptyList());
		result.setSize(0);
	    result.setStatus(SQL_INVALID);
	    result.setDuration(-1);
	    result.setTimestamp(TimeHelper.now());
	    result.setError("Unrecognized query");
	    return result;
	}
	
	private class DatabaseHandler extends SQLiteOpenHelper
	{
		private static final int DATABASE_VERSION = 1;
		
		public DatabaseHandler(String databaseName, Context context, CursorFactory factory) {
			super(context, databaseName, factory, DATABASE_VERSION);
		}

		/**
		 * Queries are coming from the outside. Database structure is not knows beforehand
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
