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

import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.googlecode.tesseract.android.TessBaseAPI.PageSegMode;
import com.koushikdutta.ion.Ion;


public class StatTrackerActivity extends Activity {

    enum Direction {
        PREVIOUS,
        NEXT,
        NONE
    };

    private static final String DEFAULT_LANGUAGE = "eng";
    private static Future<String> sTessBasePath;

    private String mAgentStr;
    private String mApStr;
    private String mUpvStr;
    private String mDiscoversStr;
    private String mXmStr;
    private String mHacksStr;
    private String mResonatorsDeployedStr;
    private String mLinksCreatedStr;
    private String mControlFieldsCreatedStr;
    private String mMuStr;
    private String mLongestLinkStr;
    private String mLargestControlFieldStr;
    private String mXmRechargedStr;
    private String mXmRecharged2Str;
    private String mCapturesStr;
    private String mUpcStr;
    private String mResonatorsDestroyedStr;
    private String mPortalsNeutralizedStr;
    private String mLinksDestroyedStr;
    private String mControlFieldsDestroyedStr;
    private String mDistanceWalkedStr;
    private String mMaxTimePortalHeldStr;
    private String mMaxTimeLinkMaintainedStr;
    private String mMaxLinkLengthxDaysStr;
    private String mMaxTimeFieldHeldStr;
    private String mLargestFieldMuxDaysStr;
    private Set<String> mStatNames;

    private AgentStatsDataSource mDataSource;
    private List<AgentStats> mAgentStats;
    private int mAgentStatsPos;
    private HashMap<String, String> mStatsMap;

    public StatTrackerActivity()
    {
        mStatsMap = new HashMap<String, String>();
        mDataSource = new AgentStatsDataSource(this);
    }

    protected void populateMap(Bitmap bmp) throws ExecutionException, InterruptedException
    {
        Log.i("info", String.format("Image size: %d x %d", bmp.getWidth(), bmp.getHeight()));

        // Attempt to initialize the API.
        final TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(sTessBasePath.get(), DEFAULT_LANGUAGE);
        baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);
        baseApi.setImage(bmp);
        Pixa allLinesPixa = baseApi.getTextlines();
        Log.i("info", String.format("allLinesPixa.size() == %d", allLinesPixa.size()));

        baseApi.setPageSegMode(PageSegMode.PSM_SINGLE_LINE);
        Rect boxRect = allLinesPixa.getBoxRect(1);
        baseApi.setRectangle(boxRect);
        mStatsMap.put(mAgentStr, baseApi.getUTF8Text().replaceFirst(" .*$", ""));
        Log.i("info", "Recognized Agent Name: " + mStatsMap.get(mAgentStr));

        baseApi.setRectangle(allLinesPixa.getBoxRect(3));
        mStatsMap.put(mApStr, baseApi.getUTF8Text());
        Log.i("info", "AP: " + mStatsMap.get(mApStr));

        for (Rect curRect : allLinesPixa.getBoxRects())
        {
            baseApi.setRectangle(curRect);
            String text = baseApi.getUTF8Text();
            for (String statName : mStatNames)
            {
                if (text.startsWith(statName))
                {
                    mStatsMap.put(statName, baseApi.getUTF8Text().replaceFirst(statName + " ", ""));
                }
            }
        }
        baseApi.end();
    }

    protected void ocrImage(Bitmap bmp)
    {
        TextView agentNameView = (TextView) findViewById(R.id.agentNameText);
        try
        {
            populateMap(bmp);
            AgentStats stats = new AgentStats(getResources(), mStatsMap);
            mDataSource.appendAgentStats(stats);
            mAgentStats = mDataSource.getAllStats();
            mAgentStatsPos = mAgentStats.size()-1;

            agentNameView.setText(mStatsMap.get(mAgentStr));
            setTextViewText(R.id.apText, mStatsMap.get(mApStr));

            // Discovery
            setTextViewText(R.id.upvText, mStatsMap.get(mUpvStr));
            setTextViewText(R.id.portalsDiscoveredText, mStatsMap.get(mDiscoversStr));
            setTextViewText(R.id.xmCollectedText, mStatsMap.get(mXmStr));

            // Building
            setTextViewText(R.id.hacksText, mStatsMap.get(mHacksStr));
            setTextViewText(R.id.resonatorsDeployedText, mStatsMap.get(mResonatorsDeployedStr));
            setTextViewText(R.id.linksCreatedText, mStatsMap.get(mLinksCreatedStr));
            setTextViewText(R.id.controlFieldsCreatedText, mStatsMap.get(mControlFieldsCreatedStr));
            setTextViewText(R.id.muText, mStatsMap.get(mMuStr));
            setTextViewText(R.id.longestLinkText, mStatsMap.get(mLongestLinkStr));
            setTextViewText(R.id.largestControlFieldText, mStatsMap.get(mLargestControlFieldStr));
            setTextViewText(R.id.xmRechargedText, mStatsMap.get(mXmRechargedStr));
            setTextViewText(R.id.portalsCapturedText, mStatsMap.get(mCapturesStr));
            setTextViewText(R.id.upcText, mStatsMap.get(mUpcStr));

            // Combat
            setTextViewText(R.id.resonatorsDestroyedText, mStatsMap.get(mResonatorsDestroyedStr));
            setTextViewText(R.id.portalsNeutralizedText, mStatsMap.get(mPortalsNeutralizedStr));
            setTextViewText(R.id.linksDestroyedText, mStatsMap.get(mLinksDestroyedStr));
            setTextViewText(R.id.controlFieldsDestroyedText, mStatsMap.get(mControlFieldsDestroyedStr));

            // Health
            setTextViewText(R.id.distanceWalkedText, mStatsMap.get(mDistanceWalkedStr));

            // Defense
            setTextViewText(R.id.maxTimePortalHeldText, mStatsMap.get(mMaxTimePortalHeldStr));
            setTextViewText(R.id.maxTimeLinkMaintainedText, mStatsMap.get(mMaxTimeLinkMaintainedStr));
            setTextViewText(R.id.maxLinkLengthxDaysText, mStatsMap.get(mMaxLinkLengthxDaysStr));
            setTextViewText(R.id.maxTimeFieldHeldText, mStatsMap.get(mMaxTimeFieldHeldStr));
            setTextViewText(R.id.largestFieldMuxDaysText, mStatsMap.get(mLargestFieldMuxDaysStr));
        }
        catch (ExecutionException ee)
        {
            String msg = "Unable to initialize Tesseract data files";
            if (ee.getCause() instanceof IOException)
                msg = msg + " - IOException";
            Log.i("info", Log.getStackTraceString(ee));
            agentNameView.setText(msg);
        }
        catch (InterruptedException ie)
        {
            Log.i("info", Log.getStackTraceString(ie));
            agentNameView.setText("Interrupted while trying to initialize Tesseract data files");
        }
    }

    private void clearTextView(int id)
    {
        setTextViewText(id, "");
    }

    private void setTextViewText(int id, String text)
    {
        ((TextView)findViewById(id)).setText(text);
    }

    private void clearStats()
    {
        // Basic
        clearTextView(R.id.timestampText);
        clearTextView(R.id.agentNameText);
        clearTextView(R.id.apText);

        // Discovery
        clearTextView(R.id.upvText);
        clearTextView(R.id.portalsDiscoveredText);
        clearTextView(R.id.xmCollectedText);

        // Building
        clearTextView(R.id.hacksText);
        clearTextView(R.id.resonatorsDeployedText);
        clearTextView(R.id.linksCreatedText);
        clearTextView(R.id.controlFieldsCreatedText);
        clearTextView(R.id.muText);
        clearTextView(R.id.longestLinkText);
        clearTextView(R.id.largestControlFieldText);
        clearTextView(R.id.xmRechargedText);
        clearTextView(R.id.portalsCapturedText);
        clearTextView(R.id.upcText);

        // Combat
        clearTextView(R.id.resonatorsDestroyedText);
        clearTextView(R.id.portalsNeutralizedText);
        clearTextView(R.id.linksDestroyedText);
        clearTextView(R.id.controlFieldsDestroyedText);

        // Health
        clearTextView(R.id.distanceWalkedText);

        // Defense
        clearTextView(R.id.maxTimePortalHeldText);
        clearTextView(R.id.maxTimeLinkMaintainedText);
        clearTextView(R.id.maxLinkLengthxDaysText);
        clearTextView(R.id.maxTimeFieldHeldText);
        clearTextView(R.id.largestFieldMuxDaysText);
    }

    public void displayNextStats(View view)
    {
        displayStats(Direction.NEXT);
    }

    public void displayPrevStats(View view)
    {
        displayStats(Direction.PREVIOUS);
    }

    public void deleteStats(View view)
    {
        mDataSource.deleteAgentStats(mAgentStats.get(mAgentStatsPos));
        clearStats();
        mAgentStats = mDataSource.getAllStats();
        if (mAgentStatsPos >= mAgentStats.size())
            mAgentStatsPos = mAgentStats.size()-1;
        updateStatButtons();
        displayStats(Direction.NONE);
    }

    private void displayStats(Direction dir)
    {
        try
        {
            int newPos = mAgentStatsPos;
            if (dir == Direction.NEXT)
                newPos += 1;
            else if (dir == Direction.PREVIOUS)
                newPos -= 1;
            AgentStats currentStats = mAgentStats.get(newPos);
            clearStats();

            setTextViewText(R.id.timestampText, currentStats.getIso8601Timestamp());
            setTextViewText(R.id.agentNameText, currentStats.getAgent());
            setTextViewText(R.id.apText, NumberFormat.getNumberInstance().format(currentStats.getAp()));
            setTextViewText(R.id.upvText, NumberFormat.getNumberInstance().format(currentStats.getUpvs()));
            setTextViewText(R.id.portalsDiscoveredText, NumberFormat.getNumberInstance().format(currentStats.getPortalsDiscovered()));
            setTextViewText(R.id.xmCollectedText, NumberFormat.getNumberInstance().format(currentStats.getXmCollected()));
            setTextViewText(R.id.hacksText, NumberFormat.getNumberInstance().format(currentStats.getHacks()));
            setTextViewText(R.id.resonatorsDeployedText, NumberFormat.getNumberInstance().format(currentStats.getResonatorsDeployed()));
            setTextViewText(R.id.linksCreatedText, NumberFormat.getNumberInstance().format(currentStats.getLinksCreated()));
            setTextViewText(R.id.controlFieldsCreatedText, NumberFormat.getNumberInstance().format(currentStats.getControlFieldsCreated()));
            setTextViewText(R.id.muText, NumberFormat.getNumberInstance().format(currentStats.getMindUnitsCaptured()));
            setTextViewText(R.id.longestLinkText, NumberFormat.getNumberInstance().format(currentStats.getLongestLink()));
            setTextViewText(R.id.largestControlFieldText, NumberFormat.getNumberInstance().format(currentStats.getLargestControlField()));
            setTextViewText(R.id.xmRechargedText, NumberFormat.getNumberInstance().format(currentStats.getXmRecharged()));
            setTextViewText(R.id.portalsCapturedText, NumberFormat.getNumberInstance().format(currentStats.getPortalsCaptured()));
            setTextViewText(R.id.upcText, NumberFormat.getNumberInstance().format(currentStats.getUpcs()));
            setTextViewText(R.id.resonatorsDestroyedText, NumberFormat.getNumberInstance().format(currentStats.getResonatorsDestroyed()));
            setTextViewText(R.id.portalsNeutralizedText, NumberFormat.getNumberInstance().format(currentStats.getPortalsNeutralized()));
            setTextViewText(R.id.linksDestroyedText, NumberFormat.getNumberInstance().format(currentStats.getLinksDestroyed()));
            setTextViewText(R.id.controlFieldsDestroyedText, NumberFormat.getNumberInstance().format(currentStats.getControlFieldsDestroyed()));
            setTextViewText(R.id.distanceWalkedText, NumberFormat.getNumberInstance().format(currentStats.getDistanceWalked()));
            setTextViewText(R.id.maxTimePortalHeldText, NumberFormat.getNumberInstance().format(currentStats.getMaxTimePortalHeld()));
            setTextViewText(R.id.maxTimeLinkMaintainedText, NumberFormat.getNumberInstance().format(currentStats.getMaxTimeLinkMaintained()));
            setTextViewText(R.id.maxLinkLengthxDaysText, NumberFormat.getNumberInstance().format(currentStats.getMaxLinkLengthxDays()));
            setTextViewText(R.id.maxTimeFieldHeldText, NumberFormat.getNumberInstance().format(currentStats.getMaxTimeFieldHeld()));
            setTextViewText(R.id.largestFieldMuxDaysText, NumberFormat.getNumberInstance().format(currentStats.getLargestFieldMuxDays()));

            mAgentStatsPos = newPos;
            updateStatButtons();
        }
        catch (IndexOutOfBoundsException e)
        {
            Log.w("info", String.format("Tried to display %s stats when there are no more", dir.toString()));
        }
    }

    private void updateStatButtons()
    {
        ((ImageButton) findViewById(R.id.nextStatsButton)).setEnabled(mAgentStatsPos + 1 < mAgentStats.size());
        ((ImageButton) findViewById(R.id.prevStatsButton)).setEnabled(mAgentStatsPos > 0);
        ((ImageButton) findViewById(R.id.deleteButton)).setEnabled(mAgentStats.size() > 0);
    }

    private void initResourceStrings()
    {
        mStatNames = new HashSet<String>();
        Resources res = getResources();

        mAgentStr = res.getString(R.string.agent_name);
        mStatNames.add(mAgentStr);
        mApStr = res.getString(R.string.ap);
        mStatNames.add(mApStr);
        mUpvStr = res.getString(R.string.upv);
        mStatNames.add(mUpvStr);
        mDiscoversStr = res.getString(R.string.portals_discovered);
        mStatNames.add(mDiscoversStr);
        mXmStr = res.getString(R.string.xm_collected);
        mStatNames.add(mXmStr);
        mHacksStr = res.getString(R.string.hacks);
        mStatNames.add(mHacksStr);
        mResonatorsDeployedStr = res.getString(R.string.resonators_deployed);
        mStatNames.add(mResonatorsDeployedStr);
        mLinksCreatedStr = res.getString(R.string.links_created);
        mStatNames.add(mLinksCreatedStr);
        mControlFieldsCreatedStr = res.getString(R.string.control_fields_created);
        mStatNames.add(mControlFieldsCreatedStr);
        mMuStr = res.getString(R.string.mu);
        mStatNames.add(mMuStr);
        mLongestLinkStr = res.getString(R.string.longest_link);
        mStatNames.add(mLongestLinkStr);
        mLargestControlFieldStr = res.getString(R.string.largest_control_field);
        mStatNames.add(mLargestControlFieldStr);
        mXmRechargedStr = res.getString(R.string.xm_recharged);
        mStatNames.add(mXmRechargedStr);
        mXmRecharged2Str = res.getString(R.string.xm_recharged2);
        mStatNames.add(mXmRecharged2Str);
        mCapturesStr = res.getString(R.string.portals_captured);
        mStatNames.add(mCapturesStr);
        mUpcStr = res.getString(R.string.upc);
        mStatNames.add(mUpcStr);
        mResonatorsDestroyedStr = res.getString(R.string.resonators_destroyed);
        mStatNames.add(mResonatorsDestroyedStr);
        mPortalsNeutralizedStr = res.getString(R.string.portals_neutralized);
        mStatNames.add(mPortalsNeutralizedStr);
        mLinksDestroyedStr = res.getString(R.string.links_destroyed);
        mStatNames.add(mLinksDestroyedStr);
        mControlFieldsDestroyedStr = res.getString(R.string.control_fields_destroyed);
        mStatNames.add(mControlFieldsDestroyedStr);
        mDistanceWalkedStr = res.getString(R.string.distance_walked);
        mStatNames.add(mDistanceWalkedStr);
        mMaxTimePortalHeldStr = res.getString(R.string.max_time_portal_held);
        mStatNames.add(mMaxTimePortalHeldStr);
        mMaxTimeLinkMaintainedStr = res.getString(R.string.max_time_link_maintained);
        mStatNames.add(mMaxTimeLinkMaintainedStr);
        mMaxLinkLengthxDaysStr = res.getString(R.string.max_link_lengthxdays);
        mStatNames.add(mMaxLinkLengthxDaysStr);
        mMaxTimeFieldHeldStr = res.getString(R.string.max_time_field_held);
        mStatNames.add(mMaxTimeFieldHeldStr);
        mLargestFieldMuxDaysStr = res.getString(R.string.largest_field_muxdays);
        mStatNames.add(mLargestFieldMuxDaysStr);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initResourceStrings();
        setContentView(R.layout.activity_data_receiver);

        mDataSource.open();
        mAgentStats = mDataSource.getAllStats();
        mAgentStatsPos = mAgentStats.size();
        updateStatButtons();

        ImageView picView = (ImageView) findViewById(R.id.picture);
        TextView txtView = (TextView) findViewById(R.id.agentNameText);
        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        if (receivedAction.equals(Intent.ACTION_SEND))
        {
            String receivedType = receivedIntent.getType();
            if (receivedType.startsWith("text/"))
            {
                String receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
                if (receivedText != null)
                {
                    Log.i("info", "Text received: " + receivedText);
                    txtView.setText(receivedText);
                }
            }
            else if (receivedType.startsWith("image/"))
            {
                ExecutorService executor = Executors.newFixedThreadPool(1);
                sTessBasePath = executor.submit(new TessDataInitializer(this));

                txtView.setText("Waiting for image");
                Uri receivedUri = (Uri)receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);

                if (receivedUri != null)
                {
                    Log.i("info", "Image received with type " + receivedType + ", URI " + receivedUri.toString());
                    Ion.with(this).load(receivedUri.toString()).withBitmap().intoImageView(picView);
                    txtView.setText("Stats image loaded");

                    com.koushikdutta.async.future.Future<Bitmap> future = Ion.with(this).load(receivedUri.toString()).asBitmap();
                    future.setCallback(new com.koushikdutta.async.future.FutureCallback<Bitmap>()
                    {
                        @Override
                        public void onCompleted(Exception e, Bitmap bmp)
                        {
                            if (e != null)
                            {
                                e.printStackTrace();
                                return;
                            }

                            try
                            {
                                ocrImage(bmp);
                            }
                            catch (Throwable thr)
                            {
                                Log.i("info", "ocrImage failed:\n" + Log.getStackTraceString(thr));
                                ((TextView) findViewById(R.id.agentNameText)).setText(thr.getMessage());
                                return;
                            }
                        }
                    });
                }
                else
                {
                    txtView.setText("No image was sent!");
                }
            }
        }
        else if (receivedAction.equals(Intent.ACTION_MAIN))
        {
            txtView.setText("Nothing has been shared!");
            displayStats(Direction.PREVIOUS);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.data_receiver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        mDataSource.open();
        Log.i("info", "Ingress Stat Tracker resumed - DB reopened.");
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        mDataSource.close();
        Log.i("info", "Ingress Stat Tracker paused - DB closed.");
        super.onPause();
    }

}
