/**
 * Copyright (C) 2000 - 2013 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of the GPL, you may
 * redistribute this Program in connection with Free/Libre Open Source Software ("FLOSS")
 * applications as described in Silverpeas's FLOSS exception. You should have received a copy of the
 * text describing the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.silverpeas.notation.ejb;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.silverpeas.notation.model.Notation;
import com.silverpeas.notation.model.NotationDAO;
import com.silverpeas.notation.model.NotationDetail;
import com.silverpeas.notation.model.NotationPK;
import com.silverpeas.notation.model.comparator.NotationDetailComparator;

import com.stratelia.webactiv.util.DBUtil;
import com.stratelia.webactiv.util.JNDINames;
import com.stratelia.webactiv.util.exception.SilverpeasRuntimeException;

@Stateless(name="Notation", description="Stateless session bean to manage notation of content.")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class NotationBmEJB implements NotationBm {

  private static final long serialVersionUID = 4906158721388935209L;

  @Override
  public void updateNotation(NotationPK pk, int note) {
    Connection con = openConnection();
    try {
      if (hasUserNotation(pk)) {
        NotationDAO.updateNotation(con, pk, note);
      } else {
        NotationDAO.createNotation(con, pk, note);
      }
    } catch (Exception e) {
      throw new NotationRuntimeException("NotationBmEJB.updateNotation()",
          SilverpeasRuntimeException.ERROR,
          "notation.CREATING_NOTATION_FAILED", e);
    } finally {
      DBUtil.close(con);
    }
  }

  @Override
  public void deleteNotation(NotationPK pk) {
    Connection con = openConnection();
    try {
      NotationDAO.deleteNotation(con, pk);
    } catch (Exception e) {
      throw new NotationRuntimeException("NotationBmEJB.deleteNotation()",
          SilverpeasRuntimeException.ERROR, "notation.DELETE_NOTATION_FAILED",
          e);
    } finally {
      DBUtil.close(con);
    }
  }

  @Override
  public NotationDetail getNotation(NotationPK pk) {
    NotationDetail notationDetail = new NotationDetail(pk);
    Collection<Notation> notations = null;
    Connection con = openConnection();
    try {
      notations = NotationDAO.getNotations(con, pk);
    } catch (Exception e) {
      throw new NotationRuntimeException("NotationBmEJB.getNotation()",
          SilverpeasRuntimeException.ERROR, "notation.GET_NOTE_FAILED", e);
    } finally {
      DBUtil.close(con);
    }

    String userId = pk.getUserId();
    int notesCount = 0;
    float globalNote = 0;
    int userNote = 0;
    if (notations != null && !notations.isEmpty()) {
      notesCount = notations.size();
      float sum = 0;
      for (Notation notation : notations) {
        if (userId != null && userId.equals(notation.getAuthor())) {
          userNote = notation.getNote();
        }
        sum += notation.getNote();
      }
      globalNote = sum / notesCount;
    }
    notationDetail.setNotesCount(notesCount);
    notationDetail.setGlobalNote(globalNote);
    notationDetail.setUserNote(userNote);
    return notationDetail;
  }

  @Override
  public int countNotations(NotationPK pk) {
    Connection con = openConnection();
    try {
      return NotationDAO.countNotations(con, pk);
    } catch (Exception e) {
      throw new NotationRuntimeException("NotationBmEJB.countNotations()",
          SilverpeasRuntimeException.ERROR, "notation.COUNT_NOTATIONS_FAILED", e);
    } finally {
      DBUtil.close(con);
    }
  }

  @Override
  public boolean hasUserNotation(NotationPK pk) {
    Connection con = openConnection();
    try {
      return NotationDAO.hasUserNotation(con, pk);
    } catch (Exception e) {
      throw new NotationRuntimeException("NotationBmEJB.hasUserNotation()",
          SilverpeasRuntimeException.ERROR, "notation.HAS_USER_NOTATION_FAILED", e);
    } finally {
      DBUtil.close(con);
    }
  }

  @Override
  public Collection<NotationDetail> getBestNotations(NotationPK pk, int notationsCount) {
    Connection con = openConnection();
    Collection<NotationPK> notationPKs = null;
    try {
      notationPKs = NotationDAO.getNotationPKs(con, pk);
    } catch (Exception e) {
      throw new NotationRuntimeException("NotationBmEJB.hasUserNotation()",
          SilverpeasRuntimeException.ERROR, "notation.HAS_USER_NOTATION_FAILED", e);
    } finally {
      DBUtil.close(con);
    }
    return getBestNotations(notationPKs, notationsCount);
  }

  @Override
  public Collection<NotationDetail> getBestNotations(Collection<NotationPK> pks, int notationsCount) {
    List<NotationDetail> notations = new ArrayList<NotationDetail>();
    if (pks != null && !pks.isEmpty()) {
      for (NotationPK pk : pks) {
        notations.add(getNotation(pk));
      }
      Collections.sort(notations, new NotationDetailComparator());
      if (notations.size() > notationsCount) {
        return notations.subList(0, notationsCount);
      }
    }
    return notations;
  }

  private Connection openConnection() {
    try {
      return DBUtil.makeConnection(JNDINames.NODE_DATASOURCE);
    } catch (Exception e) {
      throw new NotationRuntimeException("NotationBmEJB.getConnection()",
          SilverpeasRuntimeException.ERROR, "root.EX_CONNECTION_OPEN_FAILED", e);
    }
  }
}