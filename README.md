#Ingress Stats Tracker
* * *

A historical stats tracker for Ingress
This utilizes Tess Two ([tess-two](https://github.com/rmtheis/tess-two)) to OCR the Ingress All Time stats page screenshot.
Tess Two is a fork of Tesseract Tools for Android ([tesseract-android-tools](http://code.google.com/p/tesseract-android-tools/)).

## Requires

* Android api rev 14 or higher
* tess-two [sources](https://github.com/rmtheis/tess-two) built and included in your android workspace.
* A Tesseract v3.02 [trained data file](https://code.google.com/p/tesseract-ocr/downloads/list) for a language. Data files must be extracted to a phone subdirectory named `tessdata`.

Build
=====
* Import Ingress Stats Tracker into your Eclipse workspace (tested on Luna)
* Clone and build tess-two sources.
* Import tess-two project into eclipse

License
=======

This project is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

    /*
     * Copyright 2014 Keith Kyzivat
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */

