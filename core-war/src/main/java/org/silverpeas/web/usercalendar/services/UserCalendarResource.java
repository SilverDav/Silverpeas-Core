/*
 * Copyright (C) 2000 - 2016 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception. You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.silverpeas.web.usercalendar.services;

import org.silverpeas.core.admin.component.model.PersonalComponentInstance;
import org.silverpeas.core.admin.user.model.User;
import org.silverpeas.core.annotation.RequestScoped;
import org.silverpeas.core.annotation.Service;
import org.silverpeas.core.webapi.base.UserPrivilegeValidation;
import org.silverpeas.core.webapi.base.annotation.Authorized;
import org.silverpeas.core.webapi.calendar.CalendarResource;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.silverpeas.web.usercalendar.services.UserCalendarResource.USER_CALENDAR_BASE_URI;

/**
 * A REST Web resource giving calendar data.
 * @author Yohann Chastagnier
 */
@Service
@RequestScoped
@Path(USER_CALENDAR_BASE_URI + "/{componentInstanceId}")
@Authorized
public class UserCalendarResource extends CalendarResource {

  static final String USER_CALENDAR_BASE_URI = "usercalendar";

  @Override
  public void validateUserAuthorization(final UserPrivilegeValidation validation) {
    if (!PersonalComponentInstance.from(getComponentId()).isPresent() ||
        User.getCurrentRequester() == null || User.getCurrentRequester().isAnonymous()) {
      throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }
  }
}