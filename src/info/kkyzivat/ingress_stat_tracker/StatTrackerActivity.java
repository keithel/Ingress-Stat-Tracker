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

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.ion.Ion;
//import com.squareup.picasso.Picasso;
import com.googlecode.leptonica.android.Pixa;
//import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.googlecode.tesseract.android.TessBaseAPI.PageSegMode;
//import com.googlecode.tesseract.android.TessBaseAPI.PageIteratorLevel;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
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

    protected void ocrImage(Bitmap bmp)
    {
        // Attempt to initialize the API.
        final TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
        baseApi.setDebug(true);
        baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);
        baseApi.setImage(bmp);
        String outputText = baseApi.getUTF8Text();
        Log.i("info", String.format("Image size: %d x %d", bmp.getWidth(), bmp.getHeight()));
        Log.i("info", "Recognized text: " + outputText);

        // Locations of stats onscreen, 0,0 at top left, negative numbers from bottom.
        // Agent Name (270, 140), (900, 210) - example: Keithel [g+]
        // AP (270,300), (1000, 370) - example: 3,157,541 AP / 4,000,000 AP
        // Top of per-time-unit All Time stats - y pos: -1670px

        Resources res = getResources();
        TextView agentNameView = (TextView) findViewById(R.id.agentNameText);
        TextView apView = (TextView) findViewById(R.id.apText);
        TextView restOfStatsView = (TextView) findViewById(R.id.restOfStatsText);

        baseApi.setPageSegMode(PageSegMode.PSM_SINGLE_LINE);
        baseApi.setRectangle(270, 140, 630, 70);
        String agentName = baseApi.getUTF8Text();
        Log.i("info", "Recognized Agent Name: " + agentName);
        agentNameView.setText(agentName.replaceFirst(" EI$", ""));

        baseApi.setPageSegMode(PageSegMode.PSM_SINGLE_LINE);
        baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "o");
        baseApi.setRectangle(270, 300, 730, 70);
        apView.setText(baseApi.getUTF8Text());
        baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "");

        Bitmap restOfStatsBmp = Bitmap.createBitmap(bmp, 0, bmp.getHeight()-1670, bmp.getWidth(), 1670);
        baseApi.setImage(restOfStatsBmp);

        baseApi.setPageSegMode(PageSegMode.PSM_SINGLE_BLOCK);
        String restOfStats = baseApi.getUTF8Text();
        Pixa restOfLines = baseApi.getTextlines();
        restOfStatsView.setText(restOfStats);

        baseApi.setPageSegMode(PageSegMode.PSM_SINGLE_LINE);
        baseApi.setRectangle(restOfLines.getBoxRect(1));
        ((TextView)findViewById(R.id.upvText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.upv) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(2));
        ((TextView)findViewById(R.id.xmCollectedText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.xm_collected) + " ", ""));


        baseApi.setRectangle(restOfLines.getBoxRect(4));
        ((TextView)findViewById(R.id.hacksText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.hacks) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(5));
        ((TextView)findViewById(R.id.resonatorsDeployedText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.resonators_deployed) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(6));
        ((TextView)findViewById(R.id.linksCreatedText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.links_created) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(7));
        ((TextView)findViewById(R.id.controlFieldsCreatedText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.control_fields_created) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(8));
        ((TextView)findViewById(R.id.muText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.mu) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(9));
        ((TextView)findViewById(R.id.longestLinkText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.longest_link) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(10));
        ((TextView)findViewById(R.id.largestControlFieldText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.largest_control_field) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(11));
        ((TextView)findViewById(R.id.xmRechargedText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.xm_recharged) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(12));
        ((TextView)findViewById(R.id.portalsCapturedText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.portals_captured) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(13));
        ((TextView)findViewById(R.id.upcText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.upc) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(15));
        ((TextView)findViewById(R.id.resonatorsDestroyedText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.resonators_destroyed) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(16));
        ((TextView)findViewById(R.id.portalsNeutralizedText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.portals_neutralized) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(17));
        ((TextView)findViewById(R.id.linksDestroyedText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.links_destroyed) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(18));
        ((TextView)findViewById(R.id.controlFieldsDestroyedText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.control_fields_destroyed) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(20));
        ((TextView)findViewById(R.id.distanceWalkedText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.distance_walked) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(22));
        ((TextView)findViewById(R.id.maxTimePortalHeldText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.max_time_portal_held) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(23));
        ((TextView)findViewById(R.id.maxTimeLinkMaintainedText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.max_time_link_maintained) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(24));
        ((TextView)findViewById(R.id.maxLinkLengthxDaysText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.max_link_lengthxdays) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(25));
        ((TextView)findViewById(R.id.maxTimeFieldHeldText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.max_time_field_held) + " ", ""));

        baseApi.setRectangle(restOfLines.getBoxRect(26));
        ((TextView)findViewById(R.id.largestFieldMuxDaysText)).setText(baseApi.getUTF8Text().replaceFirst(res.getString(R.string.largest_field_muxdays) + " ", ""));

        baseApi.end();
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                            ocrImage(bmp);
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
