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

import java.util.Set;

public interface DatasourceProvider
{
	/**
	 * Code for successful SQL execution
	 */
	public final static int SQL_SUCCESS = 0;
	
	/**
	 * Code for unexpected error during SQL execution
	 */
	public final static int SQL_ERROR = 1;
	
	/**
	 * Code for invalid SQL 
	 */
	public final static int SQL_INVALID = -1;

	/**
	 * Regex for separating multiple queries
	 */
	public final static String SQL_QUERY_SEPARATOR = "\\/\\*@\\*\\/";

	/**
	 * Executes arbitrary query
	 */
	public QueryResult execute(Query query);

	/**
	 * Returns list of tables stored in database
	 */
	public Set<String> listTables();
	
	/**
	 * Provides number of rows for given table 
	 */
	public int getRowCount(String tableName);
	
	/**
	 * Closes database and releases
	 * associated handles and resources 
	 */
	public void close();
}