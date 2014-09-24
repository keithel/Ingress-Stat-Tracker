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
        _portalsDiscovered = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.portals_discovered))));
        _xmCollected = Long.parseLong(prepIntegerString(statsMap.get(res.getString(R.string.xm_collected))));
        _hacks = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.hacks))));
        _resonatorsDeployed = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.resonators_deployed))));
        _linksCreated = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.links_created))));
        _controlFieldsCreated = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.control_fields_created))));
        _mindUnitsCaptured = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.mu))));
        _longestLink = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.longest_link))));
        _largestControlField = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.largest_control_field))));
        _xmRecharged = Long.parseLong(prepIntegerString(statsMap.get(res.getString(R.string.xm_recharged))));
        _portalsCaptured = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.portals_captured))));
        _upcs = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.upc))));
        _resonatorsDestroyed = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.resonators_destroyed))));
        _portalsNeutralized = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.portals_neutralized))));
        _linksDestroyed = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.links_destroyed))));
        _controlFieldsDestroyed = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.control_fields_destroyed))));
        _distanceWalked = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.distance_walked))));
        _maxTimePortalHeld = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.max_time_portal_held))));
        _maxTimeLinkMaintained = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.max_time_link_maintained))));
        _maxLinkLengthxDays = Long.parseLong(prepIntegerString(statsMap.get(res.getString(R.string.max_link_lengthxdays))));
        _maxTimeFieldHeld = Integer.parseInt(prepIntegerString(statsMap.get(res.getString(R.string.max_time_field_held))));
        _largestFieldMuxDays = Long.parseLong(prepIntegerString(statsMap.get(res.getString(R.string.largest_field_muxdays))));
    }

    private String prepIntegerString(String str)
    {
        if (str == null)
            return "0";

        StringBuilder preparedStr = new StringBuilder();
        for (int idx = 0; idx < str.length(); idx++)
        {
            char curChar = str.charAt(idx);
            if(Character.isDigit(curChar))
            {
                preparedStr.append(curChar);
            }
            else if (curChar == ',')
            {
                continue;
            }
            else if (curChar == 'i' &&
                     (idx+1 == str.length() || Character.isDigit(str.charAt(idx+1)) || Character.isWhitespace(str.charAt(idx+1)) ) )
            {
                preparedStr.append('1');
            }
            else
            {
                break;
            }
        }

        if (preparedStr.length() == 0)
            return "-1";

        return preparedStr.toString();
    }

    public Long getId() { return _id; }
    public void setId(long id) { this._id = Long.valueOf(id); }

    public Date getTimestamp() { return _timestamp; }

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

    public String getAgent() { return _agent; }
    public void setAgent(String agent) { this._agent = agent; }

    public long getAp() { return _ap; }
    public void setAp(long ap) { this._ap = ap; }

    public int getUpvs() { return _upvs; }
    public void setUpvs(int upvs) { this._upvs = upvs; }

    public int getPortalsDiscovered() { return _portalsDiscovered; }
    public void setPortalsDiscovered(int _portalsDiscovered) { this._portalsDiscovered = _portalsDiscovered; }

    public long getXmCollected() { return _xmCollected; }
    public void setXmCollected(long _xmCollected) { this._xmCollected = _xmCollected; }

    public int getHacks() { return _hacks; }
    public void setHacks(int _hacks) { this._hacks = _hacks; }

    public int getResonatorsDeployed() { return _resonatorsDeployed; }
    public void setResonatorsDeployed(int _resonatorsDeployed) { this._resonatorsDeployed = _resonatorsDeployed; }

    public int getLinksCreated() { return _linksCreated; }
    public void setLinksCreated(int _linksCreated) { this._linksCreated = _linksCreated; }

    public int getControlFieldsCreated() { return _controlFieldsCreated; }
    public void setControlFieldsCreated(int _controlFieldsCreated) { this._controlFieldsCreated = _controlFieldsCreated; }

    public int getMindUnitsCaptured() { return _mindUnitsCaptured; }
    public void setMindUnitsCaptured(int _mindUnitsCaptured) { this._mindUnitsCaptured = _mindUnitsCaptured; }

    public int getLongestLink() { return _longestLink; }
    public void setLongestLink(int _longestLink) { this._longestLink = _longestLink; }

    public int getLargestControlField() { return _largestControlField; }
    public void setLargestControlField(int _largestControlField) { this._largestControlField = _largestControlField; }

    public long getXmRecharged() { return _xmRecharged; }
    public void setXmRecharged(long _xmRecharged) { this._xmRecharged = _xmRecharged; }

    public int getPortalsCaptured() { return _portalsCaptured; }
    public void setPortalsCaptured(int _portalsCaptured) { this._portalsCaptured = _portalsCaptured; }

    public int getUpcs() { return _upcs; }
    public void setUpcs(int _upcs) { this._upcs = _upcs; }

    public int getResonatorsDestroyed() { return _resonatorsDestroyed; }
    public void setResonatorsDestroyed(int _resonatorsDestroyed) { this._resonatorsDestroyed = _resonatorsDestroyed; }

    public int getPortalsNeutralized() { return _portalsNeutralized; }
    public void setPortalsNeutralized(int _portalsNeutralized) { this._portalsNeutralized = _portalsNeutralized; }

    public int getLinksDestroyed() { return _linksDestroyed; }
    public void setLinksDestroyed(int _linksDestroyed) { this._linksDestroyed = _linksDestroyed; }

    public int getControlFieldsDestroyed() { return _controlFieldsDestroyed; }
    public void setControlFieldsDestroyed(int _controlFieldsDestroyed) { this._controlFieldsDestroyed = _controlFieldsDestroyed; }

    public int getDistanceWalked() { return _distanceWalked; }
    public void setDistanceWalked(int _distanceWalked) { this._distanceWalked = _distanceWalked; }

    public int getMaxTimePortalHeld() { return _maxTimePortalHeld; }
    public void setMaxTimePortalHeld(int _maxTimePortalHeld) { this._maxTimePortalHeld = _maxTimePortalHeld; }

    public int getMaxTimeLinkMaintained() { return _maxTimeLinkMaintained; }
    public void setMaxTimeLinkMaintained(int _maxTimeLinkMaintained) { this._maxTimeLinkMaintained = _maxTimeLinkMaintained; }

    public long getMaxLinkLengthxDays() { return _maxLinkLengthxDays; }
    public void setMaxLinkLengthxDays(long _maxLinkLengthxDays) { this._maxLinkLengthxDays = _maxLinkLengthxDays; }

    public int getMaxTimeFieldHeld() { return _maxTimeFieldHeld; }
    public void setMaxTimeFieldHeld(int _maxTimeFieldHeld) { this._maxTimeFieldHeld = _maxTimeFieldHeld; }

    public long getLargestFieldMuxDays() { return _largestFieldMuxDays; }
    public void setLargestFieldMuxDays(long _largestFieldMuxDays) { this._largestFieldMuxDays = _largestFieldMuxDays; }
}
