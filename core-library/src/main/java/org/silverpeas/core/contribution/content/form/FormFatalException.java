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
package org.silverpeas.core.contribution.content.form;

import org.silverpeas.core.exception.SilverpeasException;

/**
 * Thrown when a fatal error occured in a form component.
 */
public class FormFatalException extends FormException {

  private static final long serialVersionUID = 1351471356522040519L;

  /**
   * Set the caller and the error message
   */
  public FormFatalException(String caller, String message) {
    super(caller, SilverpeasException.ERROR, message);
  }

  /**
   * Set the caller, the error message and the nested exception.
   */
  public FormFatalException(String caller, String message,
      Exception nestedException) {
    super(caller, SilverpeasException.ERROR, message, nestedException);
  }

  /**
   * Set the caller, infos and the error message
   */
  public FormFatalException(String caller, String message, String infos) {
    super(caller, SilverpeasException.ERROR, message, infos);
  }

  /**
   * Set the caller, the error message, infos and the nested exception.
   */
  public FormFatalException(String caller, String message, String infos,
      Exception nestedException) {
    super(caller, SilverpeasException.ERROR, message, infos, nestedException);
  }
}
