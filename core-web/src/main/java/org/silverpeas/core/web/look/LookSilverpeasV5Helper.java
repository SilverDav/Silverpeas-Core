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
package org.silverpeas.core.web.look;

import org.silverpeas.core.admin.component.model.ComponentInstLight;
import org.silverpeas.core.admin.component.model.PersonalComponentInstance;
import org.silverpeas.core.admin.service.OrganizationController;
import org.silverpeas.core.admin.service.OrganizationControllerProvider;
import org.silverpeas.core.admin.service.SpaceProfile;
import org.silverpeas.core.admin.space.SpaceInst;
import org.silverpeas.core.admin.space.SpaceInstLight;
import org.silverpeas.core.admin.user.model.SilverpeasRole;
import org.silverpeas.core.admin.user.model.UserDetail;
import org.silverpeas.core.admin.user.model.UserFull;
import org.silverpeas.core.contribution.publication.model.PublicationDetail;
import org.silverpeas.core.contribution.publication.service.PublicationService;
import org.silverpeas.core.node.model.NodePK;
import org.silverpeas.core.personalization.UserMenuDisplay;
import org.silverpeas.core.personalization.service.PersonalizationService;
import org.silverpeas.core.security.session.SessionManagement;
import org.silverpeas.core.security.session.SessionManagementProvider;
import org.silverpeas.core.util.Charsets;
import org.silverpeas.kernel.bundle.LocalizationBundle;
import org.silverpeas.kernel.bundle.ResourceLocator;
import org.silverpeas.kernel.bundle.SettingBundle;
import org.silverpeas.kernel.util.StringUtil;
import org.silverpeas.core.util.URLUtil;
import org.silverpeas.kernel.logging.SilverLogger;
import org.silverpeas.core.web.look.proxy.SpaceHomepageProxy;
import org.silverpeas.core.web.look.proxy.SpaceHomepageProxyManager;
import org.silverpeas.core.web.mvc.controller.MainSessionController;
import org.silverpeas.core.web.util.viewgenerator.html.GraphicElementFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;

import static org.silverpeas.core.admin.space.SpaceInst.PERSONAL_SPACE_ID;
import static org.silverpeas.kernel.util.StringUtil.*;

public class LookSilverpeasV5Helper extends LookHelper {

  private static final Object MUTEX = new Object();
  private static final String TO_BE_DEFINED = "toBeDefined";
  private OrganizationController organizationController;
  private SettingBundle resources = null;
  private LocalizationBundle messages = null;
  private LocalizationBundle defaultMessages = null;
  private MainSessionController mainSC = null;
  private boolean displayPDCInNav = false;
  private boolean shouldDisplayPDCFrame = false;
  private boolean shouldDisplayContextualPDC = true;
  private boolean shouldDisplaySpaceIcons = true;
  private boolean shouldDisplayConnectedUsers = true;
  private boolean displayPDCInHomePage = true;
  private List<String> topSpaceIds = null; // sublist of topItems
  private String mainFrame = "silverpeas-main.jsp";
  private String spaceId = null;
  private String subSpaceId = null;
  private String componentId = null;
  private SimpleDateFormat formatter = null;
  private PublicationHelper kmeliaTransversal = null;
  private PublicationService publicationService = null;
  // Attribute used to manage user favorite space look
  private UserMenuDisplay displayUserMenu = UserMenuDisplay.DISABLE;
  private boolean enableUFSContainsState = false;
  private final HttpSession session;
  private String currentLookName = null;
  private LayoutConfiguration layoutConfiguration;

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getSpaceId()
   */
  @Override
  public String getSpaceId() {
    return spaceId;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#setSpaceId(java.lang.String)
   */
  @Override
  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getSubSpaceId()
   */
  @Override
  public String getSubSpaceId() {
    return subSpaceId;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#setSubSpaceId(java.lang.String)
   */
  @Override
  public void setSubSpaceId(String subSpaceId) {
    this.subSpaceId = subSpaceId;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getComponentId()
   */
  @Override
  public String getComponentId() {
    return componentId;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#setComponentId(java.lang.String)
   */
  @Override
  public void setComponentId(String componentId) {
    this.componentId = componentId;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#setSpaceIdAndSubSpaceId(java.lang.String)
   */
  @Override
  public void setSpaceIdAndSubSpaceId(String spaceId) {
    synchronized (MUTEX) {
      if (StringUtil.isDefined(spaceId)) {
        List<SpaceInstLight> spacePath = organizationController.getPathToSpace(spaceId);
        if (!spacePath.isEmpty()) {
          SpaceInstLight space = spacePath.get(spacePath.size() - 1);
          setSpaceAndSubSpaceContext(space.getId(), space.isPersonalSpace(), space.getId());
        }
        setComponentId(null);
      } else {
        setSpaceAndSubSpaceContext(null, false, null);
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#setComponentIdAndSpaceIds(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public void setComponentIdAndSpaceIds(final String spaceId, final String subSpaceId,
      final String componentId) {
    synchronized (MUTEX) {
      boolean isPersonalSpace = false;
      String finalSpaceId = EMPTY;
      String finalSubSpaceId = EMPTY;
      if (!StringUtil.isDefined(spaceId) && PersonalComponentInstance.from(componentId).isEmpty()) {
        List<SpaceInstLight> spacePath = organizationController.getPathToComponent(componentId);
        if (!spacePath.isEmpty()) {
          SpaceInstLight space = spacePath.get(spacePath.size() - 1);
          isPersonalSpace = space.isPersonalSpace();
          finalSpaceId = space.getId();
          finalSubSpaceId = space.getId();
        }
      } else {
        finalSpaceId = spaceId;
        finalSubSpaceId = subSpaceId;
      }
      setSpaceAndSubSpaceContext(finalSpaceId, isPersonalSpace, finalSubSpaceId);
      setComponentId(componentId);
      if (StringUtil.isDefined(componentId)) {
        getGraphicElementFactory().ifPresent(f -> f.setComponentIdForCurrentRequest(componentId));
      }
    }
  }

  private void setSpaceAndSubSpaceContext(final String spaceId, final boolean isPersonalSpace,
      final String subSpaceId) {
    final boolean isPortletHomePage = getGraphicElementFactory().map(f -> {
      Optional.ofNullable(defaultStringIfNotDefined(subSpaceId, spaceId))
          .filter(StringUtil::isDefined)
          .ifPresent(f::setSpaceIdForCurrentRequest);
      return f.isPortletMainPage();
    }).orElse(false);
    if (isPersonalSpace || PERSONAL_SPACE_ID.equalsIgnoreCase(spaceId) || isPortletHomePage) {
      reloadProperties(null, true);
      setSpaceId(null);
      setSubSpaceId(null);
    } else {
      reloadProperties(spaceId, false);
      setSpaceId(spaceId);
      setSubSpaceId(subSpaceId);
    }
  }

  /**
   * Constructs a new LookSilverpeasV5Helper instance for the specified session.
   * @param session the session of a user.
   */
  protected LookSilverpeasV5Helper(HttpSession session) {
    this.session = session;
    getGraphicElementFactory()
        .map(GraphicElementFactory::getFavoriteLookSettings)
        .ifPresent(this::init);
  }

  /*
   * (non-Javadoc)
   *
   * org.silverpeas.core.web.look.LookHelper#init(org.silverpeas.core.web.mvc.controller.MainSessionController,
   * org.silverpeas.kernel.bundle.ResourceLocator, org.silverpeas.kernel.bundle.ResourceLocator)
   */
  @Override
  public final void init(MainSessionController mainSessionController, SettingBundle resources) {
    this.mainSC = mainSessionController;
    init(resources);
  }

  private void init(SettingBundle resources) {
    this.organizationController = OrganizationControllerProvider.getOrganisationController();
    this.publicationService = PublicationService.get();
    this.resources = resources;
    this.defaultMessages = ResourceLocator.getLocalizationBundle(
        "org.silverpeas.lookSilverpeasV5.multilang.lookBundle",
        getMainSessionController().getFavoriteLanguage());
    if (StringUtil.isDefined(resources.getString("MessageBundle", ""))) {
      this.messages = ResourceLocator.getLocalizationBundle(resources.getString("MessageBundle"),
          getMainSessionController().getFavoriteLanguage());
    }
    initProperties();
    initLayoutConfiguration();
    getTopItems();
  }

  private void initProperties() {
    displayPDCInNav = resources.getBoolean("displayPDCInNav", false);
    shouldDisplayPDCFrame = resources.getBoolean("displayPDCFrame", false);
    shouldDisplayContextualPDC = resources.getBoolean("displayContextualPDC", true);
    shouldDisplaySpaceIcons = resources.getBoolean("displaySpaceIcons", true);
    shouldDisplayConnectedUsers = resources.getBoolean("displayConnectedUsers", true);
    displayPDCInHomePage = resources.getBoolean("displayPDCInHomePage", true);
    if (isAnonymousUser()) {
      displayUserMenu = UserMenuDisplay.DISABLE;
    } else {
      displayUserMenu = UserMenuDisplay.valueOf(resources.getString("displayUserFavoriteSpace",
          PersonalizationService.DEFAULT_MENU_DISPLAY_MODE.name()).toUpperCase());
      if (isMenuPersonalisationEnabled() && getMainSessionController().getPersonalization().getDisplay().isNotDefault()) {
        this.displayUserMenu = getMainSessionController().getPersonalization().getDisplay();
      }
      enableUFSContainsState = resources.getBoolean("enableUFSContainsState", false);
    }
  }

  private void reloadProperties(String spaceId, final boolean force) {
    if (isDefined(spaceId) || force) {
      final String spaceLook = Optional.ofNullable(spaceId)
          .filter(StringUtil::isDefined)
          .map(SilverpeasLook.getSilverpeasLook()::getSpaceLook)
          .filter(StringUtil::isDefined)
          // no look defined for this space (or its parent),
          // use user's favorite look or look by default
          .orElseGet(() -> Optional.ofNullable(getMainSessionController())
              .map(MainSessionController::getFavoriteLook)
              .orElse(null));
      if (spaceLook != null && !spaceLook.equals(currentLookName)) {
        currentLookName = getGraphicElementFactory().map(f -> {
          final String look = f.setLook(spaceLook);
          init(f.getFavoriteLookSettings());
          return look;
        }).orElse(null);
      }
    }
  }

  @Override
  public void initLayoutConfiguration() {
    this.layoutConfiguration = new DefaultLayoutConfiguration(this.resources);
  }

  @Override
  public LayoutConfiguration getLayoutConfiguration() {
    return layoutConfiguration;
  }

  @Override
  public boolean isMenuPersonalisationEnabled() {
    return UserMenuDisplay.DISABLE != UserMenuDisplay.valueOf(resources.getString(
        "displayUserFavoriteSpace", PersonalizationService.DEFAULT_MENU_DISPLAY_MODE.name()).
        toUpperCase());
  }

  protected MainSessionController getMainSessionController() {
    if (session != null) {
      return (MainSessionController) session
          .getAttribute(MainSessionController.MAIN_SESSION_CONTROLLER_ATT);
    }
    return mainSC;
  }

  protected OrganizationController getOrganisationController() {
    return organizationController;
  }

  protected Optional<GraphicElementFactory> getGraphicElementFactory() {
    if (session != null) {
      return Optional.ofNullable((GraphicElementFactory) session.getAttribute(
          GraphicElementFactory.GE_FACTORY_SESSION_ATT));
    }
    return Optional.empty();
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getUserFullName(java.lang.String)
   */
  @Override
  public String getUserFullName(String userId) {
    return organizationController.getUserDetail(userId).getDisplayedName();
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getUserFullName()
   */
  @Override
  public String getUserFullName() {
    return getUserDetail().getDisplayedName();
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getUserId()
   */
  @Override
  public String getUserId() {
    return getMainSessionController().getUserId();
  }

  public UserDetail getUserDetail() {
    return UserDetail.getById(getUserId());
  }

  public UserFull getUserFull() {
    return organizationController.getUserFull(getUserId());
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getLanguage()
   */
  @Override
  public String getLanguage() {
    return getMainSessionController().getFavoriteLanguage();
  }

  @Override
  public ZoneId getZoneId() {
    return getMainSessionController().getFavoriteZoneId();
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#isAnonymousUser()
   */
  @Override
  public boolean isAnonymousUser() {
    return UserDetail.isAnonymousUser(getUserId());
  }

  @Override
  public boolean isAccessGuest() {
    return getUserDetail().isAccessGuest();
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#displayPDCInNavigationFrame()
   */
  @Override
  public boolean displayPDCInNavigationFrame() {
    return displayPDCInNav;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#displayPDCFrame()
   */
  @Override
  public boolean displayPDCFrame() {
    return shouldDisplayPDCFrame;
  }

  @Override
  public boolean displayContextualPDC() {
    return shouldDisplayContextualPDC;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#shouldDisplaySpaceIcons()
   */
  @Override
  public boolean displaySpaceIcons() {
    return shouldDisplaySpaceIcons;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getSpaceId(java.lang.String)
   */
  @Override
  public String getSpaceId(String componentId) {
    ComponentInstLight component = organizationController.getComponentInstLight(componentId);
    if (component != null) {
      return component.getDomainFatherId();
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getWallPaper(java.lang.String)
   */
  @Override
  public String getWallPaper(String spaceId) {
    String hasWallpaper = "0";
    if (StringUtil.isDefined(spaceId) && SilverpeasLook.getSilverpeasLook().hasSpaceWallpaper(
        spaceId)) {
      hasWallpaper = "1";
    }
    return hasWallpaper;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getNBConnectedUsers()
   */
  @Override
  public int getNBConnectedUsers() {
    int nbConnectedUsers = 0;
    if (shouldDisplayConnectedUsers) {
      // Remove the current user
      SessionManagement sessionManagement = SessionManagementProvider.getSessionManagement();
      nbConnectedUsers = sessionManagement.getNbConnectedUsersList(getMainSessionController().
          getCurrentUserDetail()) - 1;
    }
    return nbConnectedUsers;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#isAnonymousAccess()
   */
  @Override
  public boolean isAnonymousAccess() {
    return isAnonymousUser();
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getSettings(java.lang.String)
   */
  @Override
  public boolean getSettings(String key) {
    return resources.getBoolean(key, false);
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getSettings(java.lang.String, boolean)
   */
  @Override
  public boolean getSettings(String key, boolean defaultValue) {
    return resources.getBoolean(key, defaultValue);
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getSettings(java.lang.String, java.lang.String)
   */
  @Override
  public String getSettings(String key, String defaultValue) {
    return resources.getString(key, defaultValue);
  }

  @Override
  public int getSettings(String key, int defaultValue) {
    return resources.getInteger(key, defaultValue);
  }

  public LocalizationBundle getLocalizedBundle() {
    return messages != null ? messages : defaultMessages;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getString(java.lang.String)
   */
  @Override
  public String getString(String key) {
    if (key.startsWith("lookSilverpeasV5")) {
      return defaultMessages.getString(key);
    }
    return messages.getString(key);
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#isBackOfficeVisible()
   */
  @Override
  public boolean isBackOfficeVisible() {
    return getMainSessionController().isBackOfficeVisible();
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getTopItems()
   */
  @Override
  public List<TopItem> getTopItems() {
    final List<TopItem> topItems = new ArrayList<>();
    topSpaceIds = new ArrayList<>();
    StringTokenizer tokenizer = new StringTokenizer(resources.getString("componentsTop", ""), ",");
    while (tokenizer.hasMoreTokens()) {
      String itemId = tokenizer.nextToken();

      if (itemId.startsWith(SpaceInst.SPACE_KEY_PREFIX)) {
        if (organizationController.isSpaceAvailable(itemId, getUserId())) {
          SpaceInstLight space = organizationController.getSpaceInstLightById(itemId);
          SpaceInstLight rootSpace = organizationController.getRootSpace(itemId);
          TopItem item = new TopItem();
          item.setLabel(space.getName(getLanguage()));
          item.setSpaceId(rootSpace.getId());
          item.setSubSpaceId(itemId);
          topItems.add(item);
          topSpaceIds.add(item.getSpaceId());
        }
      } else {
        if (organizationController.isComponentAvailableToUser(itemId, getUserId())) {
          ComponentInstLight component = organizationController.getComponentInstLight(itemId);
          String currentSpaceId = component.getDomainFatherId();
          SpaceInstLight rootSpace = organizationController.getRootSpace(currentSpaceId);
          TopItem item = new TopItem();
          item.setLabel(component.getLabel(getLanguage()));
          item.setComponentId(itemId);
          item.setSpaceId(rootSpace.getId());
          item.setSubSpaceId(currentSpaceId);

          topItems.add(item);
        }
      }
    }
    return topItems;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getTopSpaceIds()
   */
  @Override
  public List<String> getTopSpaceIds() {
    return topSpaceIds;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#getMainFrame()
   */
  @Override
  public String getMainFrame() {
    return mainFrame;
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.core.web.look.LookHelper#setMainFrame(java.lang.String)
   */
  @Override
  public void setMainFrame(String newMainFrame) {
    if (StringUtil.isDefined(newMainFrame)) {
      this.mainFrame = newMainFrame;
    }
  }

  /*
   * (non-Javadoc) @see org.silverpeas.core.web.look.LookHelper#getSpaceWallPaper()
   */
  @Override
  public String getSpaceWallPaper() {
    String theSpaceId = getCurrentDeepestSpaceId();
    if (StringUtil.isDefined(theSpaceId)) {
      return SilverpeasLook.getSilverpeasLook().getWallpaperOfSpace(theSpaceId);
    }
    return null;
  }

  @Override
  public String getSpaceWithCSSToApply() {
    String theSpaceId = getCurrentDeepestSpaceId();
    if (StringUtil.isDefined(theSpaceId)) {
      return SilverpeasLook.getSilverpeasLook().getSpaceWithCSS(theSpaceId);
    }
    return null;
  }

  private String getCurrentDeepestSpaceId() {
    String theSpaceId = getSpaceId();
    if (StringUtil.isDefined(theSpaceId) && StringUtil.isDefined(getSubSpaceId())) {
      theSpaceId = getSubSpaceId();
    }
    return theSpaceId;
  }

  public String getComponentURL(String key, String function) {
    String currentFunction = function;
    String currentComponentId = resources.getString(key);
    if (!StringUtil.isDefined(function)) {
      currentFunction = "Main";
    }
    return URLUtil.getApplicationURL() + URLUtil.getURL("useless", currentComponentId)
        + currentFunction;
  }

  @Override
  public String getComponentURL(String key) {
    return getComponentURL(key, "Main");
  }

  @Override
  public String getDate() {
    if (formatter == null) {
      formatter = new SimpleDateFormat(resources.getString("DateFormat", "dd/MM/yyyy"),
          new Locale(getMainSessionController().getFavoriteLanguage()));
    }
    return formatter.format(new Date());
  }

  @Override
  public String getDefaultSpaceId() {
    String defaultSpaceId = resources.getString("DefaultSpaceId", "");
    if (!StringUtil.isDefined(defaultSpaceId)) {
      defaultSpaceId = getMainSessionController().getFavoriteSpace();
    }
    return defaultSpaceId;
  }

  @SuppressWarnings("unchecked")
  protected PublicationHelper getPublicationHelper() throws ClassNotFoundException,
      InstantiationException, IllegalAccessException {
    if (kmeliaTransversal == null) {
      String helperClassName = getSettings("publicationHelper",
          "org.silverpeas.components.kmelia.KmeliaTransversal");
      Class<?> helperClass = Class.forName(helperClassName);
      try {
        Constructor<PublicationHelper> constructor =
            (Constructor<PublicationHelper>) helperClass.getConstructor();
        kmeliaTransversal = constructor.newInstance();
      } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException e) {
        throw new InstantiationException(e.getMessage());
      }
      kmeliaTransversal.setMainSessionController(getMainSessionController());
    }
    return kmeliaTransversal;
  }

  @Override
  public List<PublicationDetail> getLatestPublications(String spaceId, int nbPublis) {
    try {
      return getPublicationHelper().getPublications(spaceId, nbPublis);
    } catch (Exception ex) {
      SilverLogger.getLogger(this).error(ex);
      return new ArrayList<>();
    }
  }

  @Override
  public List<PublicationDetail> getLatestPublications(String spaceId,
      List<String> excludedComponents, int nbPublis) {
    try {
      return getPublicationHelper().getPublications(spaceId, excludedComponents, nbPublis);
    } catch (Exception ex) {
      SilverLogger.getLogger(this).error(ex);
      return new ArrayList<>();
    }
  }

  @Override
  public List<PublicationDetail> getValidPublications(NodePK nodePK) {
    List<PublicationDetail> publis = (List<PublicationDetail>) getPublicationService().
        getDetailsByFatherPK(nodePK, null, true);
    List<PublicationDetail> filteredPublis = new ArrayList<>();
    PublicationDetail publi;
    for (int i = 0; publis != null && i < publis.size(); i++) {
      publi = publis.get(i);
      if (PublicationDetail.VALID_STATUS.equalsIgnoreCase(publi.getStatus())) {
        filteredPublis.add(publi);
      }
    }
    return filteredPublis;
  }

  public PublicationService getPublicationService() {
    return publicationService;
  }

  public String getSpaceHomePage(String spaceId, HttpServletRequest request)
      throws UnsupportedEncodingException {
    final SpaceHomepageProxy spaceStruct = SpaceHomepageProxyManager.get()
        .getProxyOf(getOrganisationController().getSpaceInstById(spaceId));
    // Page d'accueil de l'espace = Composant
    if (isComponentAsSpaceHome(spaceStruct) &&
        getOrganisationController().isComponentAvailableToUser(spaceStruct.getFirstPageExtraParam(),
            getUserId())) {
        return URLUtil.getSimpleURL(URLUtil.URL_COMPONENT,
            spaceStruct.getFirstPageExtraParam());
    }

    // Page d'accueil de l'espace = URL
    if (spaceStruct != null
        && (spaceStruct.getFirstPageType() == SpaceInst.FP_TYPE_HTML_PAGE)
        && (spaceStruct.getFirstPageExtraParam() != null)
        && (spaceStruct.getFirstPageExtraParam().length() > 0)) {
      String destination = spaceStruct.getFirstPageExtraParam();
      destination = getParsedDestination(destination, "%ST_USER_LOGIN%",
          getMainSessionController().getCurrentUserDetail().getLogin());
      destination = getParsedDestination(destination, "%ST_USER_FULLNAME%",
          URLEncoder.encode(getMainSessionController().getCurrentUserDetail().getDisplayedName(),
              Charsets.UTF_8.name()));
      destination = getParsedDestination(destination, "%ST_USER_ID%",
          URLEncoder.encode(getMainSessionController().getUserId(), Charsets.UTF_8.name()));
      destination = getParsedDestination(destination, "%ST_SESSION_ID%",
          URLEncoder.encode(request.getSession().getId(), Charsets.UTF_8.name()));

      // !!!! Add the password : this is an uggly patch that use a session
      // variable set in the "AuthenticationServlet" servlet
    HttpSession theSession = request.getSession();
      return getParsedDestination(destination, "%ST_USER_PASSWORD%",
          (String) theSession.getAttribute("Silverpeas_pwdForHyperlink"));
    }
    return null;
  }

  private boolean isComponentAsSpaceHome(final SpaceHomepageProxy spaceStruct) {
    return spaceStruct != null &&
        (spaceStruct.getFirstPageType() == SpaceInst.FP_TYPE_COMPONENT_INST) &&
        spaceStruct.getFirstPageExtraParam() != null &&
        spaceStruct.getFirstPageExtraParam().length() > 0;
  }

  private String getParsedDestination(String sDestination, String sKeyword, String sValue) {
    String parsedDestination = sDestination;
    int nLoginIndex = sDestination.indexOf(sKeyword);
    if (nLoginIndex != -1) {
      // Replace the keyword with the actual value
      String sParsed = sDestination.substring(0, nLoginIndex);
      sParsed = sParsed + sValue;
      if (sDestination.length() > nLoginIndex + sKeyword.length()) {
        sParsed += sDestination.substring(nLoginIndex + sKeyword.length());
      }
      parsedDestination = sParsed;
    }
    return parsedDestination;
  }

  /**
   * @return user favorite space menu display mode
   */
  @Override
  public UserMenuDisplay getDisplayUserMenu() {
    return displayUserMenu;
  }

  @Override
  public void setDisplayUserMenu(UserMenuDisplay displayUserMenu) {
    this.displayUserMenu = displayUserMenu;
  }

  /**
   * @return true if displaying three states, false if displaying two states
   */
  @Override
  public boolean isEnableUFSContainsState() {
    return enableUFSContainsState;
  }

  /**
   * Returns a list of shortcuts to display on a page (home page, heading page...)
   *
   * @param id identify the area of shorcuts
   * @param nb the number of shortcuts to retrieve
   * @return a List of Shorcut
   */
  public List<Shortcut> getShortcuts(String id, int nb) {
    List<Shortcut> shortcuts = new ArrayList<>();
    for (int i = 1; i <= nb; i++) {
      String prefix = "Shortcut." + id + "." + i;
      String url = getSettings(prefix + ".Url", TO_BE_DEFINED);
      String target = getSettings(prefix + ".Target", TO_BE_DEFINED);
      String altText = getSettings(prefix + ".AltText", TO_BE_DEFINED);
      String iconUrl = getSettings(prefix + ".IconUrl", TO_BE_DEFINED);
      Shortcut shortcut = new Shortcut(iconUrl, target, url, altText);
      shortcuts.add(shortcut);
    }
    return shortcuts;
  }

  /**
   * @return the displayPDCInHomePage
   */
  @Override
  public boolean isDisplayPDCInHomePage() {
    return displayPDCInHomePage;
  }

  @Override
  public DefaultSpaceHomePage getSpaceHomePage(String spaceId) {
    setSpaceIdAndSubSpaceId(spaceId);
    String currentSpaceId = getSubSpaceId();
    DefaultSpaceHomePage homepage = new DefaultSpaceHomePage();

    // get main information of space
    SpaceInstLight space = organizationController.getSpaceInstLightById(currentSpaceId);
    homepage.setSpace(space);

    // get latest publications
    if (resources.getBoolean("space.homepage.latestpublications", true)) {
      setLatestPublicationsInHomePage(currentSpaceId, homepage);
    }

    if (resources.getBoolean("space.homepage.news", true)) {
      // get visible news from 'quickinfo' apps
      homepage.setNews(getNews(currentSpaceId));
    }

    if (resources.getBoolean("space.homepage.subspaces", true)) {
      // get allowed subspaces
      String[] subspaceIds =
          getOrganisationController().getAllowedSubSpaceIds(getUserId(), currentSpaceId);
      List<SpaceInstLight> subspaces = new ArrayList<>();
      for (String subspaceId : subspaceIds) {
        subspaces.add(getOrganisationController().getSpaceInstLightById(subspaceId));
      }
      homepage.setSubSpaces(subspaces);
    }

    boolean displayApps = resources.getBoolean("space.homepage.apps", true);
    boolean displayEvents = resources.getBoolean("space.homepage.events", true);
    if (displayApps || displayEvents) {
      setApplicationsInHomePage(currentSpaceId, homepage, displayApps, displayEvents);
    }

    if (resources.getBoolean("space.homepage.admins", true)) {
      // get space admins (not global admins)
      homepage.setAdmins(getSpaceAdmins(currentSpaceId));
    }

    return homepage;
  }

  private void setApplicationsInHomePage(final String currentSpaceId,
      final DefaultSpaceHomePage homepage, final boolean displayApps, final boolean displayEvents) {
    // get allowed apps
    String[] appIds =
        getOrganisationController().getAvailCompoIdsAtRoot(currentSpaceId, getUserId());
    List<ComponentInstLight> apps = new ArrayList<>();
    for (String appId : appIds) {
      ComponentInstLight app = getOrganisationController().getComponentInstLight(appId);
      if (displayApps && !app.isHidden()) {
        apps.add(app);
      }
      if (displayEvents && app.getName().equals("almanach") &&
          !StringUtil.isDefined(homepage.getNextEventsURL())) {
        homepage.setNextEventsURL(
            URLUtil.getApplicationURL() + URLUtil.getURL(null, appId) + "portlet");
      }
    }
    homepage.setApps(apps);
  }

  private void setLatestPublicationsInHomePage(final String currentSpaceId,
      final DefaultSpaceHomePage homepage) {
    try {
      homepage.setPublications(getPublicationHelper().getUpdatedPublications(currentSpaceId, 0,
          resources.getInteger("space.homepage.latestpublications.nb", 5)));
    } catch (Exception e) {
      SilverLogger.getLogger(this).error(e.getMessage(), e);
    }
  }

  public List<UserDetail> getSpaceAdmins(String spaceId) {
    List<UserDetail> admins = new ArrayList<>();
      SpaceProfile spaceProfile =
          getOrganisationController().getSpaceProfile(spaceId, SilverpeasRole.MANAGER);
      Set<String> userIds = spaceProfile.getAllUserIdsIncludingAllGroups();
      for (String userId : userIds) {
        admins.add(UserDetail.getById(userId));
    }
    return admins;
  }

  public List<PublicationDetail> getNews(String spaceId) {
    List<String> appIds = new ArrayList<>();
    String[] cIds = getOrganisationController().getAvailCompoIds(spaceId, getUserId());
    for (String id : cIds) {
      if (StringUtil.startsWithIgnoreCase(id, "quickinfo")) {
        appIds.add(id);
      }
    }

    List<PublicationDetail> news = new ArrayList<>();
    for (String appId : appIds) {
      Collection<PublicationDetail> someNews =
          getPublicationService().getOrphanPublications(appId);
      for (PublicationDetail aNews : someNews) {
        if (isVisibleNews(aNews)) {
          news.add(aNews);
        }
      }
    }

    news.sort(PublicationUpdateDateComparator.comparator);

    int nbNews = getSettings("space.homepage.news.nb", 10);
    if (news.size() > nbNews) {
      return news.subList(0, nbNews);
    }
    return news;
  }

  private boolean isVisibleNews(PublicationDetail news) {
    return news.isValid() && news.getVisibility().isActive();
  }

  @Override
  public TickerSettings getTickerSettings() {
    TickerSettings tickerSettings = new TickerSettings(resources);
    String labelParam = getSettings("ticker.label", "");
    if (labelParam.equalsIgnoreCase("default")) {
      tickerSettings.setLabel(getString("lookSilverpeasV5.ticker.label"));
    }
    return tickerSettings;
  }

  @Override
  public String getURLOfLastVisitedCollaborativeSpace() {
    String theSpaceId = getSpaceId();
    if (StringUtil.isDefined(getSubSpaceId())) {
      theSpaceId = getSubSpaceId();
    }
    if (StringUtil.isDefined(theSpaceId)) {
      return URLUtil.getSimpleURL(URLUtil.URL_SPACE, theSpaceId) + "?Fallback=true";
    }
    return null;
  }

  public SettingBundle getSettingsBundle() {
    return this.resources;
  }
}