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
 * "http://www.silverpeas.org/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
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

import org.silverpeas.core.admin.component.GroupOfParametersSorter;
import org.silverpeas.core.util.CollectionUtil;
import org.silverpeas.core.util.MemoizedSupplier;

import javax.xml.bind.annotation.XmlTransient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mmoquillon
 */
public abstract class AbstractSilverpeasComponent implements SilverpeasComponent {

  @XmlTransient
  protected Map<String, Parameter> indexedParametersByName = new HashMap<>();

  @XmlTransient
  private final MemoizedSupplier<Map<String, LocalizedComponent>> localized = new MemoizedSupplier<>(HashMap::new);

  private LocalizedComponent getLocalized(final String lang) {
    return localized.get().computeIfAbsent(lang, l -> new LocalizedComponent(this, l));
  }

  /**
   * Gets the value of the label property.
   * @return possible object is {@link Multilang }
   */
  protected abstract Map<String, String> getLabel();

  @Override
  public String getLabel(final String lang) {
    return getLocalized(lang).getLabel();
  }

  /**
   * Gets the value of the description property.
   * @return possible object is {@link Multilang }
   */
  protected abstract Map<String, String> getDescription();

  @Override
  public String getDescription(final String lang) {
    return getLocalized(lang).getDescription();
  }

  /**
   * Gets defined parameters indexed by their names.
   * @return a dictionary with all the parameters indexed by their name.
   */
  protected Map<String, Parameter> getIndexedParametersByName() {
    List<Parameter> definedParameters = getParameters();
    if (CollectionUtil.isNotEmpty(definedParameters) &&
        definedParameters.size() != indexedParametersByName.size()) {
      for (Parameter parameter : definedParameters) {
        indexedParametersByName.put(parameter.getName(), parameter);
      }
    }
    List<GroupOfParameters> theGroupsOfParameters = getGroupsOfParameters();
    for (GroupOfParameters group : theGroupsOfParameters) {
      for (Parameter parameter : group.getParameters()) {
        indexedParametersByName.put(parameter.getName(), parameter);
      }
    }
    return indexedParametersByName;
  }

  @Override
  public List<Parameter> getSortedParameters() {
    getParameters().sort(new ParameterSorter());
    return getParameters();
  }

  public List<GroupOfParameters> getSortedGroupsOfParameters() {
    getGroupsOfParameters().sort(new GroupOfParametersSorter());
    return getGroupsOfParameters();
  }
}
  