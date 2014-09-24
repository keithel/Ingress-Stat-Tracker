/*
 * Copyright (C) 2014 Keith Kyzivat
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package info.kkyzivat.ingress_stat_tracker;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AgentStatsDataSource
{
    private SQLiteDatabase mDb;
    private StatSqliteHelper mDbHelper;
    private String[] mAllColumns = {
            StatSqliteHelper.COLUMN_ID, StatSqliteHelper.COLUMN_TIMESTAMP,
            StatSqliteHelper.COLUMN_AGENT, StatSqliteHelper.COLUMN_AP };

    public AgentStatsDataSource(Context context)
    {
        mDbHelper = new StatSqliteHelper(context);
    }

    public void open() throws SQLException
    {
        mDb = mDbHelper.getWritableDatabase();
    }

    public void close()
    {
        mDbHelper.close();
    }

    public AgentStats appendAgentStats(AgentStats stats) throws SQLException
    {
        ContentValues values = new ContentValues();
        values.put(StatSqliteHelper.COLUMN_TIMESTAMP, stats.getIso8601Timestamp());
        values.put(StatSqliteHelper.COLUMN_AGENT, stats.getAgent());
        values.put(StatSqliteHelper.COLUMN_AP, stats.getAp());
        long insertId = mDb.insert(StatSqliteHelper.TABLE_AGENT_STATS, null, values);
        Cursor cursor = mDb.query(StatSqliteHelper.TABLE_AGENT_STATS, mAllColumns,
                String.format("%s = %d", StatSqliteHelper.COLUMN_ID, insertId), null, null, null, null);
        cursor.moveToFirst();
        AgentStats newStats = cursorToAgentStats(cursor);
        return newStats;
    }

    public void deleteAgentStats(AgentStats stats) throws SQLException
    {
        Long id = stats.getId();
        if (id == null)
            throw new SQLException(String.format("Stats object contains no Database ID - timestamp %s", stats.getIso8601Timestamp()));

        if (mDb.delete(StatSqliteHelper.TABLE_AGENT_STATS, String.format("%s = %d", StatSqliteHelper.COLUMN_ID, id), null) < 1)
            throw new SQLException(String.format("Failed to delete stats - timestamp %s", stats.getIso8601Timestamp()));
    }

    public List<AgentStats> getAllStats() throws SQLException
    {
        List<AgentStats> list = new ArrayList<AgentStats>();
        Cursor cursor = mDb.query(StatSqliteHelper.TABLE_AGENT_STATS, mAllColumns,
                null, null, null, null, StatSqliteHelper.COLUMN_TIMESTAMP + " ASC");
        try
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                AgentStats stats = cursorToAgentStats(cursor);
                list.add(stats);
                cursor.moveToNext();
            }
        }
        finally
        {
            cursor.close();
        }

        return list;
    }

    private AgentStats cursorToAgentStats(Cursor cursor) throws SQLException
    {
        AgentStats stats = new AgentStats();
        try
        {
            stats.setId(cursor.getLong(0));
            stats.setTimestamp(cursor.getString(1));
            stats.setAgent(cursor.getString(2));
            stats.setAp(cursor.getLong(3));
        } catch (ParseException pe)
        {
            throw new SQLException(String.format("Database corrupt - timestamp %s on id %d invalid",
                    cursor.getString(3), cursor.getLong(0)));
        }

        return stats;
    }

}
