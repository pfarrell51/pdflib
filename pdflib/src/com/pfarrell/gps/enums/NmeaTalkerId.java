/*
 * Copyright (C) 2010 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps.enums;

/**
 * the <code>NmeaTalkerId</code> enum lists "talker id" aka device types for the Nmea 183 format.
 * Source Wayne Simpson, Newsgroups: rec.boats Date: 7 Oct 1993 16:26:07 GMT
 * @author pfarrell
 * Created on Dec 27, 2010, 10:39:30 PM
 * Copyright (C) 2010 Patrick Farrell. All rights reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public enum NmeaTalkerId {
  LC,  // Loran-C
  GP,  //   GPS
  TR,  //   Transit SATNAV
  AP,  //   Autopilot (magnetic)
  HC,  //   Magnetic heading compass
  RA,  //   Radar
  SD,  //   Depth sounder
  VW,  //   Mechanical speed log
  ;
}
