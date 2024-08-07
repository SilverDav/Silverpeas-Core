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
package org.silverpeas.core.admin.component.model;

import org.silverpeas.core.util.file.FileUtil;
import org.silverpeas.core.admin.component.constant.ComponentInstanceParameterName;
import org.silverpeas.core.admin.component.exception.ComponentFileFilterException;
import org.silverpeas.core.contribution.attachment.model.DocumentType;
import org.silverpeas.core.notification.message.MessageManager;
import org.silverpeas.core.notification.message.MessageNotifier;
import org.silverpeas.core.util.error.SilverpeasTransverseErrorUtil;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import static org.silverpeas.kernel.util.StringUtil.isDefined;

/**
 * This class handles component file filters (authorized or forbidden files).
 * @author Yohann Chastagnier
 * Date: 17/12/12
 */
public class ComponentFileFilterParameter {
  /* Global settings (authorized or forbidden files) */
  static String defaultAuthorizedFiles =
      ComponentInstanceParameterName.authorizedFileExtension.getDefaultValue();
  static String defaultForbiddenFiles =
      ComponentInstanceParameterName.forbiddenFileExtension.getDefaultValue();

  /* Source component */
  private final SilverpeasComponentInstance component;
  /* By default, the authorizations are given priority over forbidden */
  private boolean isAuthorization = true;
  /* Defined file filters */
  private String fileFilters = null;
  /* By default, the authorizations are given priority over forbidden */
  private boolean isFileFilterGloballySet = false;
  /* Collection of MIME-TYPE authorized or forbidden */
  private Collection<String> mimeTypes = null;

  /**
   * Default hidden constructor.
   * @param component the component instance
   */
  private ComponentFileFilterParameter(final SilverpeasComponentInstance component) {
    this.component = component;
  }

  /**
   * Getting component file filter from component instance.
   * @param component the component instance
   * @return the file filter
   */
  public static ComponentFileFilterParameter from(final SilverpeasComponentInstance component) {
    return new ComponentFileFilterParameter(component).initialize();
  }

  /**
   * Gets MIME-TYPES authorized or not.
   * @return a collection of mime-types
   */
  private Collection<String> getMimeTypes() {
    return mimeTypes;
  }

  /**
   * Initialize component file filter variables. By default, the authorizations are given priority
   * over forbidden.
   */
  private ComponentFileFilterParameter initialize() {
    if (mimeTypes == null) {
      mimeTypes = new HashSet<>();

      /* Excluding or including files ? */

      // Authorized file parameter of component has the priority
      fileFilters = component
          .getParameterValue(ComponentInstanceParameterName.authorizedFileExtension.name());
      if (isDefined(fileFilters)) {
        // Authorization and parameterized on component instance
        parseFileFilters(fileFilters);
        return this;
      }

      // If no filters previously defined, forbidden file parameter of component becomes the
      // priority
      fileFilters =
          component.getParameterValue(ComponentInstanceParameterName.forbiddenFileExtension.name());
      if (isDefined(fileFilters)) {
        // forbidden and parameterized on component instance
        isAuthorization = false;
        parseFileFilters(fileFilters);
        return this;
      }

      // Global settings
      isFileFilterGloballySet = true;

      // If no filters previously defined, authorized file parameter from server settings becomes
      // the priority
      fileFilters = defaultAuthorizedFiles;
      if (isDefined(fileFilters)) {
        // Authorization and parameterized globally
        parseFileFilters(fileFilters);
        return this;
      }

      // If no filters previously defined, forbidden file parameter from server settings becomes
      // the priority
      fileFilters = defaultForbiddenFiles;
      if (isDefined(fileFilters)) {
        // forbidden and parameterized globally
        isAuthorization = false;
        parseFileFilters(fileFilters);
      }
    }
    return this;
  }

  /**
   * Parse file filters.
   * @param definedFileFilters file filters separated by semicolons
   */
  private void parseFileFilters(final String definedFileFilters) {
    fileFilters = "";
    if (isDefined(definedFileFilters)) {
      fileFilters =
          definedFileFilters.trim().replaceAll("[* ;,]+[.]", ",").replaceAll("[, ]+", ", ")
              .replaceAll("^, ", "");
      for (String fileFilter : fileFilters.split(",")) {
        mimeTypes.add(FileUtil.getMimeType("file." + fileFilter.trim()));
      }
    }
  }

  /**
   * Gets the component instance.
   * @return the component instance.
   */
  public SilverpeasComponentInstance getComponent() {
    return component;
  }

  /**
   * Indicates if getMimeTypes returns authorized or forbidden files.
   * @return true for authorized files, false for forbidden files.
   */
  public boolean isAuthorization() {
    return isAuthorization;
  }

  /**
   * Gets the current file filter.
   * @return the current filter filename.
   */
  public String getFileFilters() {
    return fileFilters;
  }

  /**
   * Indicates if the file filter is set globally.
   * @return true if the file filter is set globally, false otherwise.
   */
  public boolean isFileFilterGloballySet() {
    return isFileFilterGloballySet;
  }

  /**
   * Checks if the given file is authorized. If parameter is null, it is considered as forbidden.
   * @param file the file
   * @return true if the specified file is authorized, false otherwise.
   */
  public boolean isFileAuthorized(final File file) {
    return file != null && isMimeTypeAuthorized(FileUtil.getMimeType(file.getPath()));
  }

  /**
   * Throwing the component file filter exception (RuntimeException) if file is forbidden.
   * @param file the file
   */
  public void verifyFileAuthorized(final File file) {
    if (!isFileAuthorized(file)) {
      ComponentFileFilterException exception =
          new ComponentFileFilterException(this, file != null ? file.getName() : "");
      MessageNotifier.addSevere(SilverpeasTransverseErrorUtil
          .performExceptionMessage(exception, MessageManager.getLanguage()));
      throw exception;
    }
  }

  /**
   * Checks if the given mime type is authorized. If parameter is null, it is considered as
   * forbidden.
   * @param mimeType the mime-type
   * @return true if the mime-type is authorized, false otherwise.
   */
  public boolean isMimeTypeAuthorized(final String mimeType) {
    if (!getMimeTypes().isEmpty()) {

      // If the mime-type corresponds to a technical file, it is authorized.
      if (DocumentType.decode(mimeType) != null) {
        return true;
      }

      // File mime-type has to be found
      if (!isDefined(mimeType)) {
        return false;
      }

      // On authorized check, fileMimeType has to be contained in authorized files defined.
      // On forbidden check, fileMimeType has not to be contained in forbidden files defined.
      return isAuthorization() == getMimeTypes().contains(mimeType);
    }
    return true;
  }
}
