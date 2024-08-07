/*
 * Copyright (C) 2000 - 2024 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "https://www.silverpeas.org/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.silverpeas.web.jobstartpage;

import java.io.File;
import java.io.Serializable;

import org.silverpeas.core.util.file.FileUtil;
import org.silverpeas.core.util.file.FileRepositoryManager;
import org.silverpeas.core.util.file.FileServerUtils;

public class SpaceLookItem implements Serializable {

  private static final long serialVersionUID = 1L;
  private String name = null;
  private String size = null;
  private String url = null;

  public SpaceLookItem(File file, String spaceId) {
    name = file.getName();
    size = FileRepositoryManager.formatFileSize(file.length());

    url = FileServerUtils.getOnlineURL(spaceId, name, name, FileUtil.getMimeType(name), "look");
  }

  public String getName() {
    return name;
  }

  public String getSize() {
    return size;
  }

  public String getURL() {
    return url;
  }
}
