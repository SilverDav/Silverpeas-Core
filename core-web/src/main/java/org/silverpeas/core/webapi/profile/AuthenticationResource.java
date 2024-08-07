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
package org.silverpeas.core.webapi.profile;

import org.silverpeas.core.admin.user.model.User;
import org.silverpeas.core.annotation.WebService;
import org.silverpeas.core.web.rs.RESTWebService;
import org.silverpeas.core.web.rs.UserPrivilegeValidation;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A REST-based Web service to authenticate a user in Silverpeas.
 * <p>
 * If the authentication matches the {@code Basic} authentication scheme, then a session is opened
 * in Silverpeas for the user behind the request and this session can be used both for browsing
 * the Silverpeas web portal or to access the REST API by the same client. If the authentication
 * matches a {@code Bearer} authentication scheme, then a session is created just for the HTTP
 * request. Latter is hence pertinent only to check the the user can access the REST API of
 * Silverpeas.
 * </p>
 * <p>
 * This service is only interesting for opening an HTTP session before performing several tasks
 * in Silverpeas. It avoids to authenticate the user by its login and password for each HTTP request
 * against the REST API of Silverpeas. Nevertheless, the recommended (and better) way to consume the
 * REST API is to use the Bearer authentication scheme with the API token of the user; the token
 * can be passed in each request against the REST API in the {@code Authorization} HTTP header
 * without passing through the heaver authentication mechanism by user credentials
 * (login/domain/password).
 * </p>
 */
@WebService
@Path(AuthenticationResource.PATH)
public class AuthenticationResource extends RESTWebService {

  static final String PATH = "authentication";

  @Inject
  private UserPrivilegeValidation privilegeValidation;

  /**
   * Authenticates the user from his credentials passed through the {@code Authorization}
   * HTTP header, opens a new HTTP session in Silverpeas in the case of a Basic authentication
   * scheme, or throws a {@link javax.ws.rs.WebApplicationException} exception. If the user has
   * already opened a session in Silverpeas and the request carries the session identifier, then
   * nothing is done.
   * @return The profile of the user once authenticated.
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public UserProfileEntity authenticate() {
    validateUserAuthentication(privilegeValidation);
    User user = getUser();
    return UserProfileEntity.fromUser(user)
        .withAsUri(ProfileResourceBaseURIs.uriOfUser(user.getId()));
  }


  @Override
  protected String getResourceBasePath() {
    return PATH;
  }

  @Override
  public String getComponentId() {
    return null;
  }
}
