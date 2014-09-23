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

    private static final String DATABASE_NAME = "agent_stats.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
        + TABLE_AGENT_STATS + "(" + COLUMN_ID
        + " integer primary key autoincrement, " + COLUMN_TIMESTAMP
        + " date, " + COLUMN_AGENT + " text not null, "
        + COLUMN_AP + " integer);";

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
