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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.res.AssetManager;

class TessDataInitializer implements Callable<String>
{
    protected Activity mParent;
    
    public TessDataInitializer(Activity parent)
    {
        mParent = parent;
    }

    @Override
    public String call() throws IOException
    {
        AssetManager assMan = mParent.getResources().getAssets();
        File cacheDir = mParent.getCacheDir();
        String dataDirName = "tessdata";
        File tessCacheDir = new File(cacheDir, dataDirName);
        boolean succeeded = false;
        for ( int tries = 0; !succeeded; tries++ )
        {
            InputStream istream = null;
            FileOutputStream ostream = null;
            try
            {
                if (! (tessCacheDir.mkdir() || tessCacheDir.isDirectory()) )
                    throw new IOException("Couldn't create tessCacheDir " + tessCacheDir.toString());

//                List<String> cacheDirFiles = Arrays.asList(tessCacheDir.list(new FilenameFilter() {
//                    @Override public boolean accept(File dir, String filename) { return filename.endsWith(".traineddata"); }
//                }));

                String[] tessDataFilenames = assMan.list(dataDirName);
                for (String dataFilename : tessDataFilenames)
                {
//                    if (cacheDirFiles.contains(dataFilename))
//                        continue;

                    istream = assMan.open(dataDirName + "/" + dataFilename, AssetManager.ACCESS_STREAMING);
                    ostream = new FileOutputStream(new File(tessCacheDir, dataFilename));
                    byte[] buffer = new byte[0x80000];
                    int bytes_read;
                    while ((bytes_read = istream.read(buffer)) != -1)
                        ostream.write(buffer, 0, bytes_read);
                }
                succeeded = true;
            }
            catch (IOException ioe)
            {
                if (tries >= 3)
                    throw ioe;
                try { Thread.sleep(1000); } catch (InterruptedException ie) {}
            }
            finally
            {
                if (istream != null) istream.close();
                if (ostream != null) ostream.close();
            }
        }
        return cacheDir.getAbsolutePath();
    }

}
