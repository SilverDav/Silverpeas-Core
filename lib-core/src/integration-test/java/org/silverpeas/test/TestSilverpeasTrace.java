/*
 * Copyright (C) 2000 - 2014 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception. You should have recieved a copy of the text describing
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

package org.silverpeas.test;

import com.stratelia.silverpeas.silvertrace.SilverpeasTrace;

import javax.inject.Singleton;
import java.util.Properties;

/**
 * Empty implementation for integration tests.
 * It can be improved by adding some counters or anything else to verify id Silvertrace methods are
 * called.
 * @author Yohann Chastagnier
 */
@Singleton
public class TestSilverpeasTrace implements SilverpeasTrace {

  @Override
  public void debug(final String module, final String classe, final String message) {
    debug(module, classe, message, null, null);
  }

  @Override
  public void debug(final String module, final String classe, final String message,
      final String extraInfos) {
    debug(module, classe, message, extraInfos, null);
  }

  @Override
  public void debug(final String module, final String classe, final String message,
      final Throwable ex) {
    debug(module, classe, message, null, ex);
  }

  @Override
  public void debug(final String module, final String classe, final String message,
      final String extraInfos, final Throwable ex) {

  }

  @Override
  public void info(final String module, final String classe, final String messageID) {
    info(module, classe, messageID, null, null);
  }

  @Override
  public void info(final String module, final String classe, final String messageID,
      final String extraInfos) {
    info(module, classe, messageID, extraInfos, null);
  }

  @Override
  public void info(final String module, final String classe, final String messageID,
      final Throwable ex) {
    info(module, classe, messageID, null, ex);
  }

  @Override
  public void info(final String module, final String classe, final String messageID,
      final String extraInfos, final Throwable ex) {

  }

  @Override
  public void warn(final String module, final String classe, final String messageID) {
    warn(module, classe, messageID, null, null);
  }

  @Override
  public void warn(final String module, final String classe, final String messageID,
      final String extraInfos) {
    warn(module, classe, messageID, extraInfos, null);
  }

  @Override
  public void warn(final String module, final String classe, final String messageID,
      final Throwable ex) {
    warn(module, classe, messageID, null, ex);
  }

  @Override
  public void warn(final String module, final String classe, final String messageID,
      final String extraInfos, final Throwable ex) {

  }

  @Override
  public void error(final String module, final String classe, final String messageID) {
    error(module, classe, messageID, null, null);
  }

  @Override
  public void error(final String module, final String classe, final String messageID,
      final String extraInfos) {
    error(module, classe, messageID, extraInfos, null);
  }

  @Override
  public void error(final String module, final String classe, final String messageID,
      final Throwable ex) {
    error(module, classe, messageID, null, ex);
  }

  @Override
  public void error(final String module, final String classe, final String messageID,
      final String extraInfos, final Throwable ex) {

  }

  @Override
  public void fatal(final String module, final String classe, final String messageID) {
    fatal(module, classe, messageID, null, null);
  }

  @Override
  public void fatal(final String module, final String classe, final String messageID,
      final String extraInfos) {
    fatal(module, classe, messageID, extraInfos, null);
  }

  @Override
  public void fatal(final String module, final String classe, final String messageID,
      final Throwable ex) {
    fatal(module, classe, messageID, null, ex);
  }

  @Override
  public void fatal(final String module, final String classe, final String messageID,
      final String extraInfos, final Throwable ex) {

  }

  @Override
  public void spy(final String module, final String classe, final String spaceId,
      final String instanceId, final String objectId, final String userId, final String actionId) {

  }

  @Override
  public void resetAll() {

  }

  @Override
  public void applyProperties(final String filePath) {

  }

  @Override
  public void initFromProperties(final Properties fileProperties) {

  }

  @Override
  public void setTraceLevel(final String module, final int val) {

  }

  @Override
  public int getTraceLevel(final String module, final boolean chained) {
    return 0;
  }

  @Override
  public void addAppenderConsole(final String module, final String patternLayout,
      final String consoleName) {

  }

  @Override
  public void addAppenderFile(final String module, final String patternLayout,
      final String fileName, final boolean appendOnFile) {

  }

  @Override
  public void addAppenderRollingFile(final String module, final String patternLayout,
      final String fileName, final String rollingMode) {

  }

  @Override
  public void addAppenderMail(final String module, final String patternLayout,
      final String mailHost, final String mailFrom, final String mailTo, final String mailSubject) {

  }

  @Override
  public void removeAppender(final String module, final int typeOfAppender) {

  }

  @Override
  public Properties getModuleList() {
    return null;
  }

  @Override
  public int getAvailableAppenders(final String module) {
    return 0;
  }

  @Override
  public Properties getAppender(final String module, final int typeOfAppender) {
    return null;
  }

  @Override
  public String getTraceMessage(final String messageId) {
    return null;
  }

  @Override
  public String[] getEndFileTrace(final String nbLines) {
    return new String[0];
  }

  @Override
  public String getTraceMessage(final String messageId, final String language) {
    return null;
  }
}