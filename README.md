Amphitheatre
============

Amphitheatre is an Android TV app that connects with network shares, organizes and serves videos to any Android capable media player.

![](https://lh6.googleusercontent.com/-q_OSJf3AKMs/U-JpQmNmWuI/AAAAAAACIdE/RFg-EXZhlUs/w2228-h1254-no/amphitheatre_screenshot.png)

**Features**
* Indexes movie files on SMB or CIFS shares.
* View movie poster art, details and quickly search through your video collection. 

**On To-Do List**
* Implement batch movie scanning processing
* Add periodic scanning support
* Add TV Show support

Dependencies
------------

* Amphitheatre uses The Movie Database (TMDb) in order to fetch movie information. You'll need to sign up as a developer and add your TMDb API Key to the ApiConstants.java class.
* Amphitheatre does not play the actual video file but serves it to a capable media player application. So you'll need to install a media player as well. MXPlayer is a great player worth checking out.

Contributing
------------

All contributions are welcome! 

License
-------

    Copyright 2014 Jerrell Mardis

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
