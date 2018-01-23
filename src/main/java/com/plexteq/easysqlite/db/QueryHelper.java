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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Contains SQL helper methods
 */
public class QueryHelper
{
	/**
	 * Response field names
	 */

	/**
	 * Query execution status
	 */
	public static final String RESULT_STATUS = "status";
	
	/**
	 * Error message
	 */
	public static final String RESULT_ERROR = "error";

	/**
	 * Client timestamp at the point of execution
	 */
	public static final String RESULT_TS = "timestamp";
	
	/**
	 * Query execution duration
	 */
	public static final String RESULT_DURATION = "duration";
	
	/**
	 * Resultset
	 */
	public static final String RESULT_DATA = "data";
	
	/**
	 * Response type, see ResponseType enum for details
	 */
	public static final String RESULT_TYPE = "type";
	
	/**
	 * Database name
	 */
	public static final String RESULT_DB = "db";
	
	public enum QueryType
	{
		QUERY_EXTRACTION,
		QUERY_OTHER,
		QUERY_UNRECOGNIZED;
	}
	
	public enum ResponseType
	{
		RESPONSE_TYPE_SQLRESULT(10),
		RESPONSE_TYPE_DEVICE_CONNECTED(11),
		RESPONSE_TYPE_SYNC_START(12),
		RESPONSE_TYPE_SYNC_STOP(13);
		
		private ResponseType(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
		
		private int code;
	}
	
	/**
	 * List of queries allowed to run against database
	 */
	public static final List<String> ALLOWED_QUERIES = Arrays.asList(
			"select", "update", "insert", "alter", 
            "delete", "drop", "reindex", "vacuum",
            "replace", "attach", "create", "pragma",
            "commit", "rollback", "begin", "end",
            "reindex", "replace", "analyze");
	
	/**
	 * Transforms QueryResult into a HashMap
	 * @param queryResult
	 * @return
	 */
	public static Map<String, Object> asMap(QueryResult queryResult)
	{
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(RESULT_STATUS, queryResult.getStatus());
		result.put(RESULT_ERROR, queryResult.getError());
		result.put(RESULT_DATA, queryResult.getData());
		result.put(RESULT_TS, String.valueOf(queryResult.getTimestamp()));
		result.put(RESULT_DURATION, queryResult.getDuration());
		result.put(RESULT_DB, queryResult.getDb());
		result.put(RESULT_TYPE, ResponseType.RESPONSE_TYPE_SQLRESULT.getCode());
		return result;
	}
	
	/**
	 * Returns type based on given SQL query  
	 */
	public static QueryType getQueryType(Query query)
	{
		QueryType type;
		
		String sql = query.getSql().toLowerCase(Locale.ENGLISH),
				command = sql.split(" ")[0];
		
		if ("select".equals(command) || "pragma".equals(command)) 
			type = QueryType.QUERY_EXTRACTION;
		else
			type = (ALLOWED_QUERIES.contains(command)) ?
				QueryType.QUERY_OTHER : QueryType.QUERY_UNRECOGNIZED;
				
		return type;
	}
}
