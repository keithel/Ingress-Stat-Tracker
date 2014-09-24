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
    private Long id;
    private Date timestamp;
    private String agent;
    private long ap;

    private static final String ISO8601_NOTZ_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public AgentStats()
    {
    }

    public AgentStats(Resources res, Map<String, String> statsMap)
    {
        timestamp = new Date();
        agent = statsMap.get(res.getString(R.string.agent_name));
        String apStr = statsMap.get(res.getString(R.string.ap));
        apStr = apStr.replaceAll(",", "");
        int idxNonNum;
        for (idxNonNum = 0; idxNonNum < apStr.length() && apStr.substring(idxNonNum, idxNonNum+1).matches("[0-9]"); idxNonNum++);
        apStr = apStr.substring(0, idxNonNum);
        ap = Long.parseLong(apStr);
    }

    public Long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = Long.valueOf(id);
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public String getIso8601Timestamp()
    {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO8601_NOTZ_FORMAT, Locale.US);
        return sdf.format(timestamp);
    }

    public void setTimestamp(String iso8601) throws ParseException
    {
        // Locale.US was used to shut up Eclipse.. says for ASCII (which this will be), to use Locale.US..
        // FIXME: Is Locale.US right? (see above)
        SimpleDateFormat sdf = new SimpleDateFormat(ISO8601_NOTZ_FORMAT, Locale.US);
        timestamp = sdf.parse(iso8601);
    }

    public String getAgent()
    {
        return agent;
    }

    public void setAgent(String agent)
    {
        this.agent = agent;
    }

    public long getAp()
    {
        return ap;
    }

    public void setAp(long ap)
    {
        this.ap = ap;
    }
}