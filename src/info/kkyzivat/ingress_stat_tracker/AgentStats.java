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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import android.content.res.Resources;

public class AgentStats
{
    private Long _id;
    private Date _timestamp;
    private String _agent;
    private long _ap;
    private int _upvs;
    private int _portalsDiscovered;
    private long _xmCollected;
    private int _hacks;
    private int _resonatorsDeployed;
    private int _linksCreated;
    private int _controlFieldsCreated;
    private int _mindUnitsCaptured;
    private int _longestLink;
    private int _largestControlField;
    private long _xmRecharged;
    private int _portalsCaptured;
    private int _upcs;
    private int _resonatorsDestroyed;
    private int _portalsNeutralized;
    private int _linksDestroyed;
    private int _controlFieldsDestroyed;
    private int _distanceWalked;
    private int _maxTimePortalHeld;
    private int _maxTimeLinkMaintained;
    private long _maxLinkLengthxDays;
    private int _maxTimeFieldHeld;
    private long _largestFieldMuxDays;

    private static final String ISO8601_NOTZ_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public AgentStats()
    {
    }

    public AgentStats(Resources res, Map<String, String> statsMap)
    {
        _timestamp = new Date();
        _agent = statsMap.get(res.getString(R.string.agent_name));
        _ap = Long.parseLong(prepIntegerString(statsMap.get(res.getString(R.string.ap))));
        _upvs = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.upv))));
    }

    private String prepIntegerString(String str)
    {
        String s = new String(str);
        s = s.replaceAll(",", "");
        int idxNonNum;
        for (idxNonNum = 0; idxNonNum < s.length() && s.substring(idxNonNum, idxNonNum+1).matches("[0-9]"); idxNonNum++);
        s = s.substring(0, idxNonNum);
        return s;
    }

    public Long getId()
    {
        return _id;
    }

    public void setId(long id)
    {
        this._id = Long.valueOf(id);
    }

    public Date getTimestamp()
    {
        return _timestamp;
    }

    public String getIso8601Timestamp()
    {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO8601_NOTZ_FORMAT, Locale.US);
        return sdf.format(_timestamp);
    }

    public void setTimestamp(String iso8601) throws ParseException
    {
        // Locale.US was used to shut up Eclipse.. says for ASCII (which this will be), to use Locale.US..
        // FIXME: Is Locale.US right? (see above)
        SimpleDateFormat sdf = new SimpleDateFormat(ISO8601_NOTZ_FORMAT, Locale.US);
        _timestamp = sdf.parse(iso8601);
    }

    public String getAgent()
    {
        return _agent;
    }

    public void setAgent(String agent)
    {
        this._agent = agent;
    }

    public long getAp()
    {
        return _ap;
    }

    public void setAp(long ap)
    {
        this._ap = ap;
    }

    public int getUpvs()
    {
        return _upvs;
    }

    public void setUpvs(int upvs)
    {
        this._upvs = upvs;
    }
}
