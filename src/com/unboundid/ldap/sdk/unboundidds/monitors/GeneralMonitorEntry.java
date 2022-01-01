/*
 * Copyright 2008-2022 Ping Identity Corporation
 * All Rights Reserved.
 */
/*
 * Copyright 2008-2022 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright (C) 2008-2022 Ping Identity Corporation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPLv2 only)
 * or the terms of the GNU Lesser General Public License (LGPLv2.1 only)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */
package com.unboundid.ldap.sdk.unboundidds.monitors;



import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotNull;
import com.unboundid.util.Nullable;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.ThreadSafetyLevel;

import static com.unboundid.ldap.sdk.unboundidds.monitors.MonitorMessages.*;



/**
 * This class defines a monitor entry that provides general information about
 * the state of the Directory Server.  The general monitor entry is the
 * top-level monitor entry that is generated by the monitor backend and is the
 * parent of all monitor entries generated by the registered monitor providers.
 * <BR>
 * <BLOCKQUOTE>
 *   <B>NOTE:</B>  This class, and other classes within the
 *   {@code com.unboundid.ldap.sdk.unboundidds} package structure, are only
 *   supported for use against Ping Identity, UnboundID, and
 *   Nokia/Alcatel-Lucent 8661 server products.  These classes provide support
 *   for proprietary functionality or for external specifications that are not
 *   considered stable or mature enough to be guaranteed to work in an
 *   interoperable way with other types of LDAP servers.
 * </BLOCKQUOTE>
 * <BR>
 * Information that may be included in the general monitor entry includes:
 * <UL>
 *   <LI>The number of connections currently established to the server.</LI>
 *   <LI>The maximum number of connections that have been established at any one
 *       time.</LI>
 *   <LI>The total number of connections established to the server since
 *       startup.</LI>
 *   <LI>The time that the directory server was started.</LI>
 *   <LI>The current time on the server.</LI>
 *   <LI>The length of time in milliseconds that the server has been
 *       online.</LI>
 *   <LI>A user-friendly string that describes the length of time that the
 *       server has been online.</LI>
 *   <LI>The name of the directory server product.</LI>
 *   <LI>The name of the vendor that provides the directory server.</LI>
 *   <LI>The server version string.</LI>
 *   <LI>The DNs of the configuration entries for any third-party extensions
 *       loaded in the server.</LI>
 * </UL>
 * The server should present at most one general monitor entry.  It can be
 * retrieved using the {@link MonitorManager#getGeneralMonitorEntry} method.
 * This entry provides specific methods for accessing information about the
 * server (e.g., the
 * {@link GeneralMonitorEntry#getCurrentConnections} method can be used
 * to retrieve the number of connections currently established).  Alternately,
 * this information may be accessed using the generic API.  See the
 * {@link MonitorManager} class documentation for an example that demonstrates
 * the use of the generic API for accessing monitor data.
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GeneralMonitorEntry
       extends MonitorEntry
{
  /**
   * The structural object class used in general monitor entries.
   */
  @NotNull static final String GENERAL_MONITOR_OC = "ds-general-monitor-entry";



  /**
   * The name of the attribute that contains the name of the cluster in which
   * the server is a member.
   */
  @NotNull private static final String ATTR_CLUSTER_NAME = "clusterName";



  /**
   * The name of the attribute that contains the number of connections currently
   * established to the server.
   */
  @NotNull private static final String ATTR_CURRENT_CONNECTIONS =
       "currentConnections";



  /**
   * The name of the attribute that contains the Directory Server's current
   * time.
   */
  @NotNull private static final String ATTR_CURRENT_TIME = "currentTime";



  /**
   * The name of the attribute that contains the names of any alert types that
   * have caused the server to be classified as "degraded".
   */
  @NotNull private static final String ATTR_DEGRADED_ALERT_TYPE =
       "degraded-alert-type";



  /**
   * The name of the attribute that contains the server instance name.
   */
  @NotNull private static final String ATTR_INSTANCE_NAME = "instanceName";



  /**
   * The name of the attribute that contains the DN of the server's location
   * config entry.
   */
  @NotNull private static final String ATTR_LOCATION_DN = "locationDN";



  /**
   * The name of the attribute that contains the name of the server's location
   * config entry.
   */
  @NotNull private static final String ATTR_LOCATION_NAME = "locationName";



  /**
   * The name of the attribute that contains the maximum number of concurrent
   * client connections established since startup.
   */
  @NotNull private static final String ATTR_MAX_CONNECTIONS = "maxConnections";



  /**
   * The name of the attribute that contains the Directory Server product name.
   */
  @NotNull private static final String ATTR_PRODUCT_NAME = "productName";



  /**
   * The name of the attribute that contains the UUID value that was generated
   * when the server instance was initially created.
   */
  @NotNull private static final String ATTR_SERVER_UUID = "serverUUID";



  /**
   * The name of the attribute that contains the Directory Server start time.
   */
  @NotNull private static final String ATTR_START_TIME = "startTime";



  /**
   * The name of the attribute that contains the Directory Server startup ID.
   */
  @NotNull private static final String ATTR_STARTUP_ID = "startupID";



  /**
   * The name of the attribute that contains the Directory Server startup UUID.
   */
  @NotNull private static final String ATTR_STARTUP_UUID = "startupUUID";



  /**
   * The name of the attribute that holds the DNs of the configuration entries
   * for any third-party extensions loaded in the server.
   */
  @NotNull private static final String ATTR_THIRD_PARTY_EXTENSION_DN =
       "thirdPartyExtensionDN";



  /**
   * The name of the attribute that contains the total number of connections
   * that have been established since startup.
   */
  @NotNull private static final String ATTR_TOTAL_CONNECTIONS =
       "totalConnections";



  /**
   * The name of the attribute that contains the Directory Server's uptime.
   */
  @NotNull private static final String ATTR_UP_TIME = "upTime";



  /**
   * The name of the attribute that contains the Directory Server vendor name.
   */
  @NotNull private static final String ATTR_VENDOR_NAME = "productVendor";



  /**
   * The name of the attribute that contains the Directory Server version
   * string.
   */
  @NotNull private static final String ATTR_VERSION = "productVersion";



  /**
   * The name of the attribute that contains the names of any alert types that
   * have caused the server to be classified as "unavailable".
   */
  @NotNull private static final String ATTR_UNAVAILABLE_ALERT_TYPE =
       "unavailable-alert-type";



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 4262569940859462743L;



  // The server's current time.
  @Nullable private final Date currentTime;

  // The server's start time.
  @Nullable private final Date startTime;

  // The names of the alert types that have caused the server to be classified
  // as "degraded".
  @NotNull private final List<String> degradedAlertTypes;

  // The DNs of the config entries for any third-party extensions loaded in the
  // server.
  @NotNull private final List<String> thirdPartyExtensionDNs;

  // The names of the alert types that have caused the server to be classified
  // as "unavailable".
  @NotNull private final List<String> unavailableAlertTypes;

  // The number connections currently established.
  @Nullable private final Long currentConnections;

  // The maximum number connections established at any time since startup.
  @Nullable private final Long maxConnections;

  // The total number of connections that have been established since startup.
  @Nullable private final Long totalConnections;

  // The Directory Server cluster name.
  @Nullable private final String clusterName;

  // The Directory Server instance name.
  @Nullable private final String instanceName;

  // The DN of the Directory Server's location config entry.
  @Nullable private final String locationDN;

  // The name of the Directory Server's location config entry.
  @Nullable private final String locationName;

  // The Directory Server product name.
  @Nullable private final String productName;

  // The UUID value that was generated when the server instance was initially
  // created.
  @Nullable private final String serverUUID;

  // The Directory Server startup ID.
  @Nullable private final String startupID;

  // The Directory Server startup UUID.
  @Nullable private final String startupUUID;

  // The string representation of the uptime.
  @Nullable private final String uptime;

  // The Directory Server vendor name.
  @Nullable private final String vendorName;

  // The Directory Server version string.
  @Nullable private final String versionString;



  /**
   * Creates a new general monitor entry from the provided entry.
   *
   * @param  entry  The entry to be parsed as a general monitor entry.  It must
   *                not be {@code null}.
   */
  public GeneralMonitorEntry(@NotNull final Entry entry)
  {
    super(entry);

    currentConnections     = getLong(ATTR_CURRENT_CONNECTIONS);
    currentTime            = getDate(ATTR_CURRENT_TIME);
    maxConnections         = getLong(ATTR_MAX_CONNECTIONS);
    productName            = getString(ATTR_PRODUCT_NAME);
    startTime              = getDate(ATTR_START_TIME);
    clusterName            = getString(ATTR_CLUSTER_NAME);
    instanceName           = getString(ATTR_INSTANCE_NAME);
    locationDN             = getString(ATTR_LOCATION_DN);
    locationName           = getString(ATTR_LOCATION_NAME);
    serverUUID             = getString(ATTR_SERVER_UUID);
    startupID              = getString(ATTR_STARTUP_ID);
    startupUUID            = getString(ATTR_STARTUP_UUID);
    totalConnections       = getLong(ATTR_TOTAL_CONNECTIONS);
    uptime                 = getString(ATTR_UP_TIME);
    vendorName             = getString(ATTR_VENDOR_NAME);
    versionString          = getString(ATTR_VERSION);
    degradedAlertTypes     = getStrings(ATTR_DEGRADED_ALERT_TYPE);
    unavailableAlertTypes  = getStrings(ATTR_UNAVAILABLE_ALERT_TYPE);
    thirdPartyExtensionDNs = getStrings(ATTR_THIRD_PARTY_EXTENSION_DN);
  }



  /**
   * Retrieves the number of connections currently established.
   *
   * @return  The number of connections currently established, or {@code null}
   *          if it was not included in the monitor entry.
   */
  @Nullable()
  public Long getCurrentConnections()
  {
    return currentConnections;
  }



  /**
   * Retrieves the maximum number of concurrent connections established at any
   * time since startup.
   *
   * @return  The maximum number of concurrent connections established at any
   *          time since startup, or {@code null} if it was not included in the
   *          monitor entry.
   */
  @Nullable()
  public Long getMaxConnections()
  {
    return maxConnections;
  }



  /**
   * Retrieves the total number of connections established since startup.
   *
   * @return  The total number of connections established since startup, or
   *          {@code null} if it was not included in the monitor entry.
   */
  @Nullable()
  public Long getTotalConnections()
  {
    return totalConnections;
  }



  /**
   * Retrieves the current time as reported by the Directory Server.
   *
   * @return  The current time as reported by the Directory Server, or
   *          {@code null} if it was not included in the monitor entry.
   */
  @Nullable()
  public Date getCurrentTime()
  {
    return currentTime;
  }



  /**
   * Retrieves the time that the Directory Server was started.
   *
   * @return  The time that the Directory Server was started, or {@code null} if
   *          it was not included in the monitor entry.
   */
  @Nullable()
  public Date getStartTime()
  {
    return startTime;
  }



  /**
   * Retrieves the name of the cluster in which the server is a member.
   *
   * @return  The name of the cluster in which the server is a member, or
   *          {@code null} if it was not included in the monitor entry.
   */
  @Nullable()
  public String getClusterName()
  {
    return clusterName;
  }



  /**
   * Retrieves the name assigned to the Directory Server instance.
   *
   * @return  The name assigned to the Directory Server instance, or
   *          {@code null} if it was not included in the monitor entry.
   */
  @Nullable()
  public String getInstanceName()
  {
    return instanceName;
  }



  /**
   * Retrieves the name of the configuration entry that defines the server's
   * location.
   *
   * @return  The name of the configuration entry that defines the server's
   *          location, or {@code null} if it was not included in the monitor
   *          entry.
   */
  @Nullable()
  public String getLocationName()
  {
    return locationName;
  }



  /**
   * Retrieves the DN of the configuration entry that defines the server's
   * location.
   *
   * @return  The DN of the configuration entry that defines the server's
   *          location, or {@code null} if it was not included in the monitor
   *          entry.
   */
  @Nullable()
  public String getLocationDN()
  {
    return locationDN;
  }



  /**
   * Retrieves the UUID value that was generated when the server instance was
   * initially created.
   *
   * @return  The UUID value that was generated when the server instance was
   *          initially created, or {@code null} if it was not included in the
   *          monitor entry.
   */
  @Nullable()
  public String getServerUUID()
  {
    return serverUUID;
  }



  /**
   * Retrieves a relatively compact identifier generated at the time the
   * Directory Server was started.
   *
   * @return  A relatively compact identifier generated at the time the
   *          Directory Server was started, or {@code null} if it was not
   *          included in the monitor entry.
   */
  @Nullable()
  public String getStartupID()
  {
    return startupID;
  }



  /**
   * Retrieves the UUID that was generated when the Directory Server was
   * started.
   *
   * @return  The UUID that was generated when the Directory Server was started,
   *          or {@code null} if it was not included in the monitor entry.
   */
  @Nullable()
  public String getStartupUUID()
  {
    return startupUUID;
  }



  /**
   * Retrieves the Directory Server uptime in milliseconds.
   *
   * @return  The Directory Server uptime in milliseconds, or {@code null} if
   *          either the current time or the start time was not available.
   */
  @Nullable()
  public Long getUptimeMillis()
  {
    if ((currentTime == null) || (startTime == null))
    {
      return null;
    }

    return currentTime.getTime() - startTime.getTime();
  }



  /**
   * Retrieves the human-readable string representation of the Directory Server
   * uptime.
   *
   * @return  The human-readable string representation of the Directory Server
   *          uptime, or {@code null} if it was not included in the monitor
   *          entry.
   */
  @Nullable()
  public String getUptimeString()
  {
    return uptime;
  }



  /**
   * Retrieves the Directory Server product name.
   *
   * @return  The Directory Serve product name, or {@code null} if it was not
   *          included in the monitor entry.
   */
  @Nullable()
  public String getProductName()
  {
    return productName;
  }



  /**
   * Retrieves the Directory Server vendor name string.
   *
   * @return  The Directory Server vendor name string, or {@code null} if it
   *          was not included in the monitor entry.
   */
  @Nullable()
  public String getVendorName()
  {
    return vendorName;
  }



  /**
   * Retrieves the Directory Server version string.
   *
   * @return  The Directory Server version string, or {@code null} if it was not
   *          included in the monitor entry.
   */
  @Nullable()
  public String getVersionString()
  {
    return versionString;
  }



  /**
   * Retrieves the names of any alert types which may have caused the server to
   * be currently classified as "degraded".
   *
   * @return  The names of any alert types which may have caused the server to
   *          be currently classified as "degraded", or an empty list if it was
   *          not included in the monitor entry (which likely indicates that the
   *          server is not classified as "degraded").
   */
  @NotNull()
  public List<String> getDegradedAlertTypes()
  {
    return degradedAlertTypes;
  }



  /**
   * Retrieves the names of any alert types which may have caused the server to
   * be currently classified as "unavailable".
   *
   * @return  The names of any alert types which may have caused the server to
   *          be currently classified as "unavailable", or an empty list if it
   *          was not included in the monitor entry (which likely indicates that
   *          the server is not classified as "unavailable").
   */
  @NotNull()
  public List<String> getUnavailableAlertTypes()
  {
    return unavailableAlertTypes;
  }



  /**
   * Retrieves the DNs of the configuration entries for any third-party
   * extensions currently loaded in the server.
   *
   * @return  The DNs of the configuration entries for any third-party
   *          extensions currently loaded in the server, or an empty list if it
   *          was not included in the monitor entry.
   */
  @NotNull()
  public List<String> getThirdPartyExtensionDNs()
  {
    return thirdPartyExtensionDNs;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  @NotNull()
  public String getMonitorDisplayName()
  {
    return INFO_GENERAL_MONITOR_DISPNAME.get();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  @NotNull()
  public String getMonitorDescription()
  {
    return INFO_GENERAL_MONITOR_DESC.get();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  @NotNull()
  public Map<String,MonitorAttribute> getMonitorAttributes()
  {
    final LinkedHashMap<String,MonitorAttribute> attrs =
         new LinkedHashMap<>(StaticUtils.computeMapCapacity(30));

    if (productName != null)
    {
      addMonitorAttribute(attrs,
           ATTR_PRODUCT_NAME,
           INFO_GENERAL_DISPNAME_PRODUCT_NAME.get(),
           INFO_GENERAL_DESC_PRODUCT_NAME.get(),
           productName);
    }

    if (vendorName != null)
    {
      addMonitorAttribute(attrs,
           ATTR_VENDOR_NAME,
           INFO_GENERAL_DISPNAME_VENDOR_NAME.get(),
           INFO_GENERAL_DESC_VENDOR_NAME.get(),
           vendorName);
    }

    if (versionString != null)
    {
      addMonitorAttribute(attrs,
           ATTR_VERSION,
           INFO_GENERAL_DISPNAME_VERSION.get(),
           INFO_GENERAL_DESC_VERSION.get(),
           versionString);
    }

    if (clusterName != null)
    {
      addMonitorAttribute(attrs,
           ATTR_CLUSTER_NAME,
           INFO_GENERAL_DISPNAME_CLUSTER_NAME.get(),
           INFO_GENERAL_DESC_CLUSTER_NAME.get(),
           clusterName);
    }

    if (instanceName != null)
    {
      addMonitorAttribute(attrs,
           ATTR_INSTANCE_NAME,
           INFO_GENERAL_DISPNAME_INSTANCE_NAME.get(),
           INFO_GENERAL_DESC_INSTANCE_NAME.get(),
           instanceName);
    }

    if (locationName != null)
    {
      addMonitorAttribute(attrs,
           ATTR_LOCATION_NAME,
           INFO_GENERAL_DISPNAME_LOCATION_NAME.get(),
           INFO_GENERAL_DESC_LOCATION_NAME.get(),
           locationName);
    }

    if (locationDN != null)
    {
      addMonitorAttribute(attrs,
           ATTR_LOCATION_DN,
           INFO_GENERAL_DISPNAME_LOCATION_DN.get(),
           INFO_GENERAL_DESC_LOCATION_DN.get(),
           locationDN);
    }

    if (startTime != null)
    {
      addMonitorAttribute(attrs,
           ATTR_START_TIME,
           INFO_GENERAL_DISPNAME_START_TIME.get(),
           INFO_GENERAL_DESC_START_TIME.get(),
           startTime);
    }

    if (serverUUID != null)
    {
      addMonitorAttribute(attrs,
           ATTR_SERVER_UUID,
           INFO_GENERAL_DISPNAME_SERVER_UUID.get(),
           INFO_GENERAL_DESC_SERVER_UUID.get(),
           serverUUID);
    }

    if (startupID != null)
    {
      addMonitorAttribute(attrs,
           ATTR_STARTUP_ID,
           INFO_GENERAL_DISPNAME_STARTUP_ID.get(),
           INFO_GENERAL_DESC_STARTUP_ID.get(),
           startupID);
    }

    if (startupUUID != null)
    {
      addMonitorAttribute(attrs,
           ATTR_STARTUP_UUID,
           INFO_GENERAL_DISPNAME_STARTUP_UUID.get(),
           INFO_GENERAL_DESC_STARTUP_UUID.get(),
           startupUUID);
    }

    if (currentTime != null)
    {
      addMonitorAttribute(attrs,
           ATTR_CURRENT_TIME,
           INFO_GENERAL_DISPNAME_CURRENT_TIME.get(),
           INFO_GENERAL_DESC_CURRENT_TIME.get(),
           currentTime);
    }

    if (uptime != null)
    {
      addMonitorAttribute(attrs,
           ATTR_UP_TIME,
           INFO_GENERAL_DISPNAME_UPTIME.get(),
           INFO_GENERAL_DESC_UPTIME.get(),
           uptime);
    }

    if ((startTime != null) && (currentTime != null))
    {
      addMonitorAttribute(attrs,
           "upTimeMillis",
           INFO_GENERAL_DISPNAME_UPTIME_MILLIS.get(),
           INFO_GENERAL_DESC_UPTIME_MILLIS.get(),
           Long.valueOf(currentTime.getTime() - startTime.getTime()));
    }

    if (currentConnections != null)
    {
      addMonitorAttribute(attrs,
           ATTR_CURRENT_CONNECTIONS,
           INFO_GENERAL_DISPNAME_CURRENT_CONNECTIONS.get(),
           INFO_GENERAL_DESC_CURRENT_CONNECTIONS.get(),
           currentConnections);
    }

    if (maxConnections != null)
    {
      addMonitorAttribute(attrs,
           ATTR_MAX_CONNECTIONS,
           INFO_GENERAL_DISPNAME_MAX_CONNECTIONS.get(),
           INFO_GENERAL_DESC_MAX_CONNECTIONS.get(),
           maxConnections);
    }

    if (totalConnections != null)
    {
      addMonitorAttribute(attrs,
           ATTR_TOTAL_CONNECTIONS,
           INFO_GENERAL_DISPNAME_TOTAL_CONNECTIONS.get(),
           INFO_GENERAL_DESC_TOTAL_CONNECTIONS.get(),
           totalConnections);
    }

    if (! degradedAlertTypes.isEmpty())
    {
      addMonitorAttribute(attrs,
           ATTR_DEGRADED_ALERT_TYPE,
           INFO_GENERAL_DISPNAME_DEGRADED_ALERT_TYPE.get(),
           INFO_GENERAL_DESC_DEGRADED_ALERT_TYPE.get(),
           degradedAlertTypes);
    }

    if (! unavailableAlertTypes.isEmpty())
    {
      addMonitorAttribute(attrs,
           ATTR_UNAVAILABLE_ALERT_TYPE,
           INFO_GENERAL_DISPNAME_UNAVAILABLE_ALERT_TYPE.get(),
           INFO_GENERAL_DESC_UNAVAILABLE_ALERT_TYPE.get(),
           unavailableAlertTypes);
    }

    if (! thirdPartyExtensionDNs.isEmpty())
    {
      addMonitorAttribute(attrs,
           ATTR_THIRD_PARTY_EXTENSION_DN,
           INFO_GENERAL_DISPNAME_THIRD_PARTY_EXTENSION_DN.get(),
           INFO_GENERAL_DESC_THIRD_PARTY_EXTENSION_DN.get(),
           thirdPartyExtensionDNs);
    }

    return Collections.unmodifiableMap(attrs);
  }
}
