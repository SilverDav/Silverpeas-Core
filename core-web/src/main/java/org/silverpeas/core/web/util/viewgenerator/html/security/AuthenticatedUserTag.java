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
package org.silverpeas.core.web.util.viewgenerator.html.security;

import org.silverpeas.core.web.mvc.controller.MainSessionController;
import org.silverpeas.core.admin.user.model.UserDetail;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Tag that checks the user is currently authenticated and that he is not an anonymous user.
 */
public class AuthenticatedUserTag extends TagSupport {
  @Override
  public int doStartTag() throws JspException {
    MainSessionController sessionController = (MainSessionController) pageContext.getSession()
        .getAttribute(MainSessionController.MAIN_SESSION_CONTROLLER_ATT);
    if (sessionController != null) {
      UserDetail user = UserDetail.getById(sessionController.getUserId());
      if (user != null && !user.isAnonymous() && !user.isAccessGuest()) {
        return EVAL_BODY_INCLUDE;
      }
    }
    return SKIP_BODY;
  }
}
