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
import java.util.HashMap;
import java.util.HashSet;
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
import android.widget.ImageView;
import android.widget.TextView;

//import com.squareup.picasso.Picasso;
import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.googlecode.tesseract.android.TessBaseAPI.PageSegMode;
import com.koushikdutta.ion.Ion;


public class StatTrackerActivity extends Activity {

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

    private HashMap<String, String> mStatsMap;

    public StatTrackerActivity()
    {
        mStatsMap = new HashMap<String, String>();
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

            agentNameView.setText(mStatsMap.get(mAgentStr));
            ((TextView) findViewById(R.id.apText)).setText(mStatsMap.get(mApStr));

    //        baseApi.setPageSegMode(PageSegMode.PSM_SINGLE_BLOCK);
    //        String restOfStats = baseApi.getUTF8Text();
    //        ((TextView) findViewById(R.id.restOfStatsText)).setText(restOfStats);

            // Discovery
            ((TextView)findViewById(R.id.upvText)).setText(mStatsMap.get(mUpvStr));
            ((TextView)findViewById(R.id.portalsDiscoveredText)).setText(mStatsMap.get(mDiscoversStr));
            ((TextView)findViewById(R.id.xmCollectedText)).setText(mStatsMap.get(mXmStr));

            // Building
            ((TextView)findViewById(R.id.hacksText)).setText(mStatsMap.get(mHacksStr));
            ((TextView)findViewById(R.id.resonatorsDeployedText)).setText(mStatsMap.get(mResonatorsDeployedStr));
            ((TextView)findViewById(R.id.linksCreatedText)).setText(mStatsMap.get(mLinksCreatedStr));
            ((TextView)findViewById(R.id.controlFieldsCreatedText)).setText(mStatsMap.get(mControlFieldsCreatedStr));
            ((TextView)findViewById(R.id.muText)).setText(mStatsMap.get(mMuStr));
            ((TextView)findViewById(R.id.longestLinkText)).setText(mStatsMap.get(mLongestLinkStr));
            ((TextView)findViewById(R.id.largestControlFieldText)).setText(mStatsMap.get(mLargestControlFieldStr));
            ((TextView)findViewById(R.id.xmRechargedText)).setText(mStatsMap.get(mXmRechargedStr));
            ((TextView)findViewById(R.id.portalsCapturedText)).setText(mStatsMap.get(mCapturesStr));
            ((TextView)findViewById(R.id.upcText)).setText(mStatsMap.get(mUpcStr));

            // Combat
            ((TextView)findViewById(R.id.resonatorsDestroyedText)).setText(mStatsMap.get(mResonatorsDestroyedStr));
            ((TextView)findViewById(R.id.portalsNeutralizedText)).setText(mStatsMap.get(mPortalsNeutralizedStr));
            ((TextView)findViewById(R.id.linksDestroyedText)).setText(mStatsMap.get(mLinksDestroyedStr));
            ((TextView)findViewById(R.id.controlFieldsDestroyedText)).setText(mStatsMap.get(mControlFieldsDestroyedStr));

            // Health
            ((TextView)findViewById(R.id.distanceWalkedText)).setText(mStatsMap.get(mDistanceWalkedStr));

            // Defense
            ((TextView)findViewById(R.id.maxTimePortalHeldText)).setText(mStatsMap.get(mMaxTimePortalHeldStr));
            ((TextView)findViewById(R.id.maxTimeLinkMaintainedText)).setText(mStatsMap.get(mMaxTimeLinkMaintainedStr));
            ((TextView)findViewById(R.id.maxLinkLengthxDaysText)).setText(mStatsMap.get(mMaxLinkLengthxDaysStr));
            ((TextView)findViewById(R.id.maxTimeFieldHeldText)).setText(mStatsMap.get(mMaxTimeFieldHeldStr));
            ((TextView)findViewById(R.id.largestFieldMuxDaysText)).setText(mStatsMap.get(mLargestFieldMuxDaysStr));
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
//                  if (receivedUri.getPath().startsWith("/storage/emulated/0"))
//                  {
//                      String newUriStr = receivedUri.toString().replace("/storage/emulated/0", "/storage/emulated/legacy");
//                      receivedUri = Uri.parse(newUriStr);
//                  }

                    Log.i("info", "Image received with type " + receivedType + ", URI " + receivedUri.toString());
//                  Ion.with(this).load(receivedUri.toString()).withBitmap().resize(1000, 1000).centerInside().intoImageView(picView);
                    Ion.with(this).load(receivedUri.toString()).withBitmap().intoImageView(picView);
                    txtView.setText("Stats image loaded");

//                  Picasso.with(this).load(receivedUri).centerInside().fit().into(picView);
//                  picView.setImageURI(receivedUri);
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

}
