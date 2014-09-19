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

import java.util.HashMap;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.ion.Ion;
//import com.squareup.picasso.Picasso;
import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.googlecode.tesseract.android.TessBaseAPI.PageSegMode;

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


public class StatTrackerActivity extends Activity {

    private static final String TESSBASE_PATH = "/mnt/sdcard/tesseract/";
    private static final String DEFAULT_LANGUAGE = "eng";

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

    private HashMap<String, String> mStatsMap;

    public StatTrackerActivity()
    {
        mStatsMap = new HashMap<String, String>();
    }

    protected void populateMap(Bitmap bmp)
    {
        Log.i("info", String.format("Image size: %d x %d", bmp.getWidth(), bmp.getHeight()));

        // Attempt to initialize the API.
        final TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
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
            if (text.startsWith(mUpvStr))
            {
                mStatsMap.put(mUpvStr, baseApi.getUTF8Text().replaceFirst(mUpvStr + " ", ""));
            }
            else if (text.startsWith(mDiscoversStr))
            {
                mStatsMap.put(mDiscoversStr, baseApi.getUTF8Text().replaceFirst(mDiscoversStr + " ", ""));
            }
            else if (text.startsWith(mXmStr))
            {
                mStatsMap.put(mXmStr, baseApi.getUTF8Text().replaceFirst(mXmStr + " ", ""));
            }
            else if (text.startsWith(mHacksStr))
            {
                mStatsMap.put(mHacksStr, baseApi.getUTF8Text().replaceFirst(mHacksStr + " ", ""));
            }
            else if (text.startsWith(mResonatorsDeployedStr))
            {
                mStatsMap.put(mResonatorsDeployedStr, baseApi.getUTF8Text().replaceFirst(mResonatorsDeployedStr + " ", ""));
            }
            else if (text.startsWith(mLinksCreatedStr))
            {
                mStatsMap.put(mLinksCreatedStr, baseApi.getUTF8Text().replaceFirst(mLinksCreatedStr + " ", ""));
            }
            else if (text.startsWith(mControlFieldsCreatedStr))
            {
                mStatsMap.put(mControlFieldsCreatedStr, baseApi.getUTF8Text().replaceFirst(mControlFieldsCreatedStr + " ", ""));
            }
            else if (text.startsWith(mMuStr))
            {
                mStatsMap.put(mMuStr, baseApi.getUTF8Text().replaceFirst(mMuStr + " ", ""));
            }
            else if (text.startsWith(mLongestLinkStr))
            {
                mStatsMap.put(mLongestLinkStr, baseApi.getUTF8Text().replaceFirst(mLongestLinkStr + " ", ""));
            }
            else if (text.startsWith(mLargestControlFieldStr))
            {
                mStatsMap.put(mLargestControlFieldStr, baseApi.getUTF8Text().replaceFirst(mLargestControlFieldStr + " ", ""));
            }
            else if (text.startsWith("XM Re"))
            {
                mStatsMap.put(mXmRechargedStr, baseApi.getUTF8Text().replaceFirst(mXmRechargedStr + " ", ""));
            }
            else if (text.startsWith(mCapturesStr))
            {
                mStatsMap.put(mCapturesStr, baseApi.getUTF8Text().replaceFirst(mCapturesStr + " ", ""));
            }
            else if (text.startsWith(mUpcStr))
            {
                mStatsMap.put(mUpcStr, baseApi.getUTF8Text().replaceFirst(mUpcStr + " ", ""));
            }
            else if (text.startsWith(mResonatorsDestroyedStr))
            {
                mStatsMap.put(mResonatorsDestroyedStr, baseApi.getUTF8Text().replaceFirst(mResonatorsDestroyedStr + " ", ""));
            }
            else if (text.startsWith(mPortalsNeutralizedStr))
            {
                mStatsMap.put(mPortalsNeutralizedStr, baseApi.getUTF8Text().replaceFirst(mPortalsNeutralizedStr + " ", ""));
            }
            else if (text.startsWith(mLinksDestroyedStr))
            {
                mStatsMap.put(mLinksDestroyedStr, baseApi.getUTF8Text().replaceFirst(mLinksDestroyedStr + " ", ""));
            }
            else if (text.startsWith(mControlFieldsDestroyedStr))
            {
                mStatsMap.put(mControlFieldsDestroyedStr, baseApi.getUTF8Text().replaceFirst(mControlFieldsDestroyedStr + " ", ""));
            }
            else if (text.startsWith(mDistanceWalkedStr))
            {
                mStatsMap.put(mDistanceWalkedStr, baseApi.getUTF8Text().replaceFirst(mDistanceWalkedStr + " ", ""));
            }
            else if (text.startsWith(mMaxTimePortalHeldStr))
            {
                mStatsMap.put(mMaxTimePortalHeldStr, baseApi.getUTF8Text().replaceFirst(mMaxTimePortalHeldStr + " ", ""));
            }
            else if (text.startsWith(mMaxTimeLinkMaintainedStr))
            {
                mStatsMap.put(mMaxTimeLinkMaintainedStr, baseApi.getUTF8Text().replaceFirst(mMaxTimeLinkMaintainedStr + " ", ""));
            }
            else if (text.startsWith(mMaxLinkLengthxDaysStr))
            {
                mStatsMap.put(mMaxLinkLengthxDaysStr, baseApi.getUTF8Text().replaceFirst(mMaxLinkLengthxDaysStr + " ", ""));
            }
            else if (text.startsWith(mMaxTimeFieldHeldStr))
            {
                mStatsMap.put(mMaxTimeFieldHeldStr, baseApi.getUTF8Text().replaceFirst(mMaxTimeFieldHeldStr + " ", ""));
            }
            else if (text.startsWith(mLargestFieldMuxDaysStr))
            {
                mStatsMap.put(mLargestFieldMuxDaysStr, baseApi.getUTF8Text().replaceFirst(mLargestFieldMuxDaysStr + " ", ""));
            }
        }
        baseApi.end();
    }

    protected void ocrImage(Bitmap bmp)
    {
        populateMap(bmp);

        ((TextView) findViewById(R.id.agentNameText)).setText(mStatsMap.get(mAgentStr));
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

    private void initResourceStrings()
    {
        Resources res = getResources();
        mAgentStr = res.getString(R.string.agent_name);
        mApStr = res.getString(R.string.ap);
        mUpvStr = res.getString(R.string.upv);
        mDiscoversStr = res.getString(R.string.portals_discovered);
        mXmStr = res.getString(R.string.xm_collected);
        mHacksStr = res.getString(R.string.hacks);
        mResonatorsDeployedStr = res.getString(R.string.resonators_deployed);
        mLinksCreatedStr = res.getString(R.string.links_created);
        mControlFieldsCreatedStr = res.getString(R.string.control_fields_created);
        mMuStr = res.getString(R.string.mu);
        mLongestLinkStr = res.getString(R.string.longest_link);
        mLargestControlFieldStr = res.getString(R.string.largest_control_field);
        mXmRechargedStr = res.getString(R.string.xm_recharged);
        mCapturesStr = res.getString(R.string.portals_captured);
        mUpcStr = res.getString(R.string.upc);
        mResonatorsDestroyedStr = res.getString(R.string.resonators_destroyed);
        mPortalsNeutralizedStr = res.getString(R.string.portals_neutralized);
        mLinksDestroyedStr = res.getString(R.string.links_destroyed);
        mControlFieldsDestroyedStr = res.getString(R.string.control_fields_destroyed);
        mDistanceWalkedStr = res.getString(R.string.distance_walked);
        mMaxTimePortalHeldStr = res.getString(R.string.max_time_portal_held);
        mMaxTimeLinkMaintainedStr = res.getString(R.string.max_time_link_maintained);
        mMaxLinkLengthxDaysStr = res.getString(R.string.max_link_lengthxdays);
        mMaxTimeFieldHeldStr = res.getString(R.string.max_time_field_held);
        mLargestFieldMuxDaysStr = res.getString(R.string.largest_field_muxdays);

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
                    Future<Bitmap> future = Ion.with(this).load(receivedUri.toString()).asBitmap();
                    future.setCallback(new FutureCallback<Bitmap>()
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
