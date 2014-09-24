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
            StatSqliteHelper.COLUMN_ID,
            StatSqliteHelper.COLUMN_TIMESTAMP,
            StatSqliteHelper.COLUMN_AGENT,
            StatSqliteHelper.COLUMN_AP,
            StatSqliteHelper.COLUMN_UPVS,
            StatSqliteHelper.COLUMN_PORTALS_DISCOVERED,
            StatSqliteHelper.COLUMN_XM_COLLECTED,
            StatSqliteHelper.COLUMN_HACKS,
            StatSqliteHelper.COLUMN_RESONATORS_DEPLOYED,
            StatSqliteHelper.COLUMN_LINKS_CREATED,
            StatSqliteHelper.COLUMN_CONTROL_FIELDS_CREATED,
            StatSqliteHelper.COLUMN_MIND_UNITS_CAPTURED,
            StatSqliteHelper.COLUMN_LONGEST_LINK,
            StatSqliteHelper.COLUMN_LARGEST_CONTROL_FIELD,
            StatSqliteHelper.COLUMN_XM_RECHARGED,
            StatSqliteHelper.COLUMN_PORTALS_CAPTURED,
            StatSqliteHelper.COLUMN_UPCS,
            StatSqliteHelper.COLUMN_RESONATORS_DESTROYED,
            StatSqliteHelper.COLUMN_PORTALS_NEUTRALIZED,
            StatSqliteHelper.COLUMN_LINKS_DESTROYED,
            StatSqliteHelper.COLUMN_CONTROL_FIELDS_DESTROYED,
            StatSqliteHelper.COLUMN_DISTANCE_WALKED,
            StatSqliteHelper.COLUMN_MAX_TIME_PORTAL_HELD,
            StatSqliteHelper.COLUMN_MAX_TIME_LINK_MAINTAINED,
            StatSqliteHelper.COLUMN_MAX_LINK_LENGTH_X_DAYS,
            StatSqliteHelper.COLUMN_MAX_TIME_FIELD_HELD,
            StatSqliteHelper.COLUMN_LARGEST_FIELD_MU_X_DAYS
    };

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
        values.put(StatSqliteHelper.COLUMN_UPVS, stats.getUpvs());
        values.put(StatSqliteHelper.COLUMN_PORTALS_DISCOVERED, stats.getPortalsDiscovered());
        values.put(StatSqliteHelper.COLUMN_XM_COLLECTED, stats.getXmCollected());
        values.put(StatSqliteHelper.COLUMN_HACKS, stats.getHacks());
        values.put(StatSqliteHelper.COLUMN_RESONATORS_DEPLOYED, stats.getResonatorsDeployed());
        values.put(StatSqliteHelper.COLUMN_LINKS_CREATED, stats.getLinksCreated());
        values.put(StatSqliteHelper.COLUMN_CONTROL_FIELDS_CREATED, stats.getControlFieldsCreated());
        values.put(StatSqliteHelper.COLUMN_MIND_UNITS_CAPTURED, stats.getMindUnitsCaptured());
        values.put(StatSqliteHelper.COLUMN_LONGEST_LINK, stats.getLongestLink());
        values.put(StatSqliteHelper.COLUMN_LARGEST_CONTROL_FIELD, stats.getLargestControlField());
        values.put(StatSqliteHelper.COLUMN_XM_RECHARGED, stats.getXmRecharged());
        values.put(StatSqliteHelper.COLUMN_PORTALS_CAPTURED, stats.getPortalsCaptured());
        values.put(StatSqliteHelper.COLUMN_UPCS, stats.getUpcs());
        values.put(StatSqliteHelper.COLUMN_RESONATORS_DESTROYED, stats.getResonatorsDestroyed());
        values.put(StatSqliteHelper.COLUMN_PORTALS_NEUTRALIZED, stats.getPortalsNeutralized());
        values.put(StatSqliteHelper.COLUMN_LINKS_DESTROYED, stats.getLinksDestroyed());
        values.put(StatSqliteHelper.COLUMN_CONTROL_FIELDS_DESTROYED, stats.getControlFieldsDestroyed());
        values.put(StatSqliteHelper.COLUMN_DISTANCE_WALKED, stats.getDistanceWalked());
        values.put(StatSqliteHelper.COLUMN_MAX_TIME_PORTAL_HELD, stats.getMaxTimePortalHeld());
        values.put(StatSqliteHelper.COLUMN_MAX_TIME_LINK_MAINTAINED, stats.getMaxTimeLinkMaintained());
        values.put(StatSqliteHelper.COLUMN_MAX_LINK_LENGTH_X_DAYS, stats.getMaxLinkLengthxDays());
        values.put(StatSqliteHelper.COLUMN_MAX_TIME_FIELD_HELD, stats.getMaxTimeFieldHeld());
        values.put(StatSqliteHelper.COLUMN_LARGEST_FIELD_MU_X_DAYS, stats.getLargestFieldMuxDays());

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
            int idx = 0;
            stats.setId(cursor.getLong(idx++));
            stats.setTimestamp(cursor.getString(idx++));
            stats.setAgent(cursor.getString(idx++));
            stats.setAp(cursor.getLong(idx++));
            stats.setUpvs(cursor.getInt(idx++));
            stats.setPortalsDiscovered(cursor.getInt(idx++));
            stats.setXmCollected(cursor.getLong(idx++));
            stats.setHacks(cursor.getInt(idx++));
            stats.setResonatorsDeployed(cursor.getInt(idx++));
            stats.setLinksCreated(cursor.getInt(idx++));
            stats.setControlFieldsCreated(cursor.getInt(idx++));
            stats.setMindUnitsCaptured(cursor.getInt(idx++));
            stats.setLongestLink(cursor.getInt(idx++));
            stats.setLargestControlField(cursor.getInt(idx++));
            stats.setXmRecharged(cursor.getLong(idx++));
            stats.setPortalsCaptured(cursor.getInt(idx++));
            stats.setUpcs(cursor.getInt(idx++));
            stats.setResonatorsDestroyed(cursor.getInt(idx++));
            stats.setPortalsNeutralized(cursor.getInt(idx++));
            stats.setLinksDestroyed(cursor.getInt(idx++));
            stats.setControlFieldsDestroyed(cursor.getInt(idx++));
            stats.setDistanceWalked(cursor.getInt(idx++));
            stats.setMaxTimePortalHeld(cursor.getInt(idx++));
            stats.setMaxTimeLinkMaintained(cursor.getInt(idx++));
            stats.setMaxLinkLengthxDays(cursor.getLong(idx++));
            stats.setMaxTimeFieldHeld(cursor.getInt(idx++));
            stats.setLargestFieldMuxDays(cursor.getInt(idx++));
        } catch (ParseException pe)
        {
            throw new SQLException(String.format("Database corrupt - timestamp %s on id %d invalid",
                    cursor.getString(3), cursor.getLong(0)));
        }

        return stats;
    }

}
