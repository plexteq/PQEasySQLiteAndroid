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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;


public class DefaultDatabaseManager implements DatabaseManager
{
	private static final DatabaseManager instance = new DefaultDatabaseManager();
	
	/**
	 * Map containing references to different databases
	 */
	private static final Map<String, DatasourceProvider> providers = new HashMap<String, DatasourceProvider>();

	private static Context ctx;
	
	private DefaultDatabaseManager() {
	}
	
	public static DatabaseManager getInstance(Context ctx) {
		DefaultDatabaseManager.ctx = ctx;
		return instance;
	}
	
	@Override
	public DatasourceProvider getDatabaseProvider(String databaseName)
	{
		if (databaseName == null)
			return null;
		
		DatasourceProvider provider = providers.get(databaseName);
		if (provider == null) {
			provider = new SqliteDatasourceProviderImpl(ctx, databaseName);
			providers.put(databaseName, provider);
		}
		
		return provider;
	}
	
	@Override
	public void closeDatabases()
	{
		for (Entry<String, DatasourceProvider> entry : providers.entrySet())
			closeDatabase(entry.getKey());
	}

	@Override
	public void closeDatabase(String database)
	{
		DatasourceProvider datasourceProvider = providers.get(database);
		
		if (datasourceProvider == null)
			return;
		
		datasourceProvider.close();
		providers.remove(database);
	}

	@Override
	public void removeDatabase(String database)
	{
		closeDatabase(database);
		ctx.deleteDatabase(database);
	}
}
