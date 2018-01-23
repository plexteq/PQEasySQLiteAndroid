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

public class DatabaseHelper
{
	/**
	 * Escapes data came from UI to be safely saved in DB
	 * without using prepared statement
	 */
	public static String escapeUserInput(String input) {
		return escapeJson(input.replaceAll("\"", "\\\\\""));
	}
	
	/**
	 * Escapes JSON serialized string to be safely saved in DB  
	 * without using prepared statement
	 */
	public static String escapeJson(String input)
	{
		return input.replaceAll("'", "\\\\'")
				 .replaceAll("/", "\\/")
				 .replaceAll("\n", "\\n")
				 .replaceAll("\b", "\\b")
				 .replaceAll("\f", "\\f")
				 .replaceAll("\r", "\\r")
				 .replaceAll("\t", "\\t");
	}
	
	/**
	 * Unescapes previously stored in database escaped
	 * user input to be used by the application
	 */
	public static String unescapeUserInput(String input) {
		return escapeJson(input.replaceAll("\\\\\"", "\""));
	}
	
	/**
	 * Unescapes previously stored in database escaped
	 * json string to be used by the application
	 */
	public static String unescapeJson(String input)
	{
		return input.replaceAll("\\\\'", "'")
				 .replaceAll("\\/", "/")
				 .replaceAll("\\n", "\n")
				 .replaceAll("\\b", "\b")
				 .replaceAll("\\f", "\f")
				 .replaceAll("\\r", "\r")
				 .replaceAll("\\t", "\t");
	}
}
