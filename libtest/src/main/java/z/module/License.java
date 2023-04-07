/**
 * Copyright 2013, Landz and its contributors. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package z.module;

/**
 *
 * refs:
 *   http://en.wikipedia.org/wiki/Comparison_of_free_software_licenses
 */
public enum License {

  APLv2(
      "http://www.apache.org/licenses/LICENSE-2.0",
      "Apache License, Version 2.0"),
  NewBSD(
      "http://opensource.org/licenses/BSD-3-Clause",
      "BSD 3-Clause License"),
  SimplifiedBSD(
      "http://opensource.org/licenses/BSD-2-Clause",
      "BSD 2-Clause License"),
  MIT(
      "http://opensource.org/licenses/mit-license.html",
      "MIT License"),
  EPLv1(
      "http://www.eclipse.org/org/documents/epl-v10.php",
      "Eclipse Public License - v 1.0"),
  GPLv3(
      "http://www.gnu.org/licenses/gpl.html",
      "GNU GENERAL PUBLIC LICENSE"),
  LGPLv3(
      "http://www.gnu.org/licenses/lgpl.html",
      "GNU LESSER GENERAL PUBLIC LICENSE"),

  UNKNOWN(
      "UNKNOWN",
      "UNKNOWN");

  private final String url;
  private final String description;

  License(String url, String description) {
    this.url = url;
    this.description = description;
  }

  public String getUrl() {
    return url;
  }
  public String getDescription() {
    return description;
  }

}
