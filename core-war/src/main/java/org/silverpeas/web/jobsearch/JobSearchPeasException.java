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
/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent)
 ---*/

/*
 * CVS Informations
 *
 * $Id: JobOrganizationPeasException.java,v 1.1.1.1 2002/08/06 14:47:55 nchaix Exp $
 *
 * $Log: JobOrganizationPeasException.java,v $
 * Revision 1.1.1.1  2002/08/06 14:47:55  nchaix
 * no message
 *
 * Revision 1.1  2002/04/05 05:42:40  tleroi
 * no message
 *
 *
 */

package org.silverpeas.web.jobsearch;

import org.silverpeas.core.exception.SilverpeasException;

/**
 * Class declaration
 * @author Cécile Bonin
 */
public class JobSearchPeasException extends SilverpeasException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor declaration
   * @param callingClass
   * @param errorLevel
   * @param message
   *
   */
  public JobSearchPeasException(String callingClass, int errorLevel,
      String message) {
    super(callingClass, errorLevel, message);
  }

  /**
   * Constructor declaration
   * @param callingClass
   * @param errorLevel
   * @param message
   * @param extraParams
   *
   */
  public JobSearchPeasException(String callingClass, int errorLevel,
      String message, String extraParams) {
    super(callingClass, errorLevel, message, extraParams);
  }

  /**
   * Constructor declaration
   * @param callingClass
   * @param errorLevel
   * @param message
   * @param nested
   *
   */
  public JobSearchPeasException(String callingClass, int errorLevel,
      String message, Exception nested) {
    super(callingClass, errorLevel, message, nested);
  }

  /**
   * Constructor declaration
   * @param callingClass
   * @param errorLevel
   * @param message
   * @param extraParams
   * @param nested
   *
   */
  public JobSearchPeasException(String callingClass, int errorLevel,
      String message, String extraParams, Exception nested) {
    super(callingClass, errorLevel, message, extraParams, nested);
  }

  /**
   * Method declaration
   * @return
   *
   */
  public String getModule() {
    return "jobSearchPeas";
  }

}
