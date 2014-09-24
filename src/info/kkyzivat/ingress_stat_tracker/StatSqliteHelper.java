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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StatSqliteHelper extends SQLiteOpenHelper
{
    public static final String TABLE_AGENT_STATS = "agent_stats";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_AGENT = "agent";
    public static final String COLUMN_AP = "ap";
    public static final String COLUMN_UPVS = "upvs";
    public static final String COLUMN_PORTALS_DISCOVERED = "portalsDiscovered";
    public static final String COLUMN_XM_COLLECTED = "xmCollected";
    public static final String COLUMN_HACKS = "hacks";
    public static final String COLUMN_RESONATORS_DEPLOYED = "resonatorsDeployed";
    public static final String COLUMN_LINKS_CREATED = "linksCreated";
    public static final String COLUMN_CONTROL_FIELDS_CREATED = "controlFieldsCreated";
    public static final String COLUMN_MIND_UNITS_CAPTURED = "mindUnitsCaptured";
    public static final String COLUMN_LONGEST_LINK = "longestLink";
    public static final String COLUMN_LARGEST_CONTROL_FIELD = "largestControlField";
    public static final String COLUMN_XM_RECHARGED = "xmRecharged";
    public static final String COLUMN_PORTALS_CAPTURED = "portalsCaptured";
    public static final String COLUMN_UPCS = "upcs";
    public static final String COLUMN_RESONATORS_DESTROYED = "resonatorsDestroyed";
    public static final String COLUMN_PORTALS_NEUTRALIZED = "portalsNeutralized";
    public static final String COLUMN_LINKS_DESTROYED = "linksDestroyed";
    public static final String COLUMN_CONTROL_FIELDS_DESTROYED = "controlFieldsDestroyed";
    public static final String COLUMN_DISTANCE_WALKED = "distanceWalked";
    public static final String COLUMN_MAX_TIME_PORTAL_HELD = "maxTimePortalHeld";
    public static final String COLUMN_MAX_TIME_LINK_MAINTAINED = "maxTimeLinkMaintained";
    public static final String COLUMN_MAX_LINK_LENGTH_X_DAYS = "maxLinkLengthxDays";
    public static final String COLUMN_MAX_TIME_FIELD_HELD = "maxTimeFieldHeld";
    public static final String COLUMN_LARGEST_FIELD_MU_X_DAYS = "largestFieldMuxDays";

    private static final String DATABASE_NAME = "agent_stats.db";
    private static final int DATABASE_VERSION = 3;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
        + TABLE_AGENT_STATS + "(" + COLUMN_ID
        + " integer primary key autoincrement, " + COLUMN_TIMESTAMP
        + " date, " + COLUMN_AGENT + " text not null, "
        + COLUMN_AP + " integer, " + COLUMN_UPVS + " integer, "
        + COLUMN_PORTALS_DISCOVERED + " integer, "
        + COLUMN_XM_COLLECTED + " integer, "
        + COLUMN_HACKS + " integer, "
        + COLUMN_RESONATORS_DEPLOYED + " integer, "
        + COLUMN_LINKS_CREATED + " integer, "
        + COLUMN_CONTROL_FIELDS_CREATED + " integer, "
        + COLUMN_MIND_UNITS_CAPTURED + " integer, "
        + COLUMN_LONGEST_LINK + " integer, "
        + COLUMN_LARGEST_CONTROL_FIELD + " integer, "
        + COLUMN_XM_RECHARGED + " integer, "
        + COLUMN_PORTALS_CAPTURED + " integer, "
        + COLUMN_UPCS + " integer, "
        + COLUMN_RESONATORS_DESTROYED + " integer, "
        + COLUMN_PORTALS_NEUTRALIZED + " integer, "
        + COLUMN_LINKS_DESTROYED + " integer, "
        + COLUMN_CONTROL_FIELDS_DESTROYED + " integer, "
        + COLUMN_DISTANCE_WALKED + " integer, "
        + COLUMN_MAX_TIME_PORTAL_HELD + " integer, "
        + COLUMN_MAX_TIME_LINK_MAINTAINED + " integer, "
        + COLUMN_MAX_LINK_LENGTH_X_DAYS + " integer, "
        + COLUMN_MAX_TIME_FIELD_HELD + " integer, "
        + COLUMN_LARGEST_FIELD_MU_X_DAYS + " integer"
        + ");";

    public StatSqliteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer)
    {
        Log.w(StatSqliteHelper.class.getName(), "Upgrading database from version " + oldVer + " to "
                + newVer + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AGENT_STATS);
        onCreate(db);
    }

}
