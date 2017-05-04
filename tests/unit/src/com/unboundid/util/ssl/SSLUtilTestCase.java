/*
 * Copyright 2008-2017 Ping Identity Corporation
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2008-2017 Ping Identity Corporation
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
package com.unboundid.util.ssl;



import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.extensions.StartTLSExtendedRequest;



/**
 * This class provides test coverage for the SSLUtil class.
 */
public class SSLUtilTestCase
       extends SSLTestCase
{
  // The default SSL protocol configured when the SSLUtils class was loaded by
  // the JVM.
  private final String originalDefaultSSLProtocol =
       SSLUtil.getDefaultSSLProtocol();

  // The original value for the property used to specify the default SSL
  // protocol when this class was loaded by the JVM.
  private final String originalPropertyDefaultSSLProtocol =
       System.getProperty(SSLUtil.PROPERTY_DEFAULT_SSL_PROTOCOL);

  // The original value for the property used to specify the enabled SSL
  // protocols when this class was loaded by the JVM.
  private final String originalPropertyEnabledSSLProtocols =
       System.getProperty(SSLUtil.PROPERTY_ENABLED_SSL_PROTOCOLS);

  // The default enabled SSL protocols configured when the SSLUtils class was
  // loaded by the JVM.
  private final Set<String> originalEnabledSSLProtocols =
       SSLUtil.getEnabledSSLProtocols();



  /**
   * Resets the SSL configuration after each method to ensure that any changes
   * made in the method do not have a lasting effect.
   */
  @AfterMethod()
  public void resetSSLConfig()
  {
    SSLUtil.setDefaultSSLProtocol(originalDefaultSSLProtocol);
    SSLUtil.setEnabledSSLProtocols(originalEnabledSSLProtocols);

    if (originalPropertyDefaultSSLProtocol == null)
    {
      System.clearProperty(SSLUtil.PROPERTY_DEFAULT_SSL_PROTOCOL);
    }
    else
    {
      System.setProperty(SSLUtil.PROPERTY_DEFAULT_SSL_PROTOCOL,
           originalPropertyDefaultSSLProtocol);
    }

    if (originalPropertyEnabledSSLProtocols == null)
    {
      System.clearProperty(SSLUtil.PROPERTY_ENABLED_SSL_PROTOCOLS);
    }
    else
    {
      System.setProperty(SSLUtil.PROPERTY_ENABLED_SSL_PROTOCOLS,
           originalPropertyEnabledSSLProtocols);
    }
  }



  /**
   * Tests the first constructor.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructor1()
         throws Exception
  {
    SSLUtil sslUtil = new SSLUtil();

    assertNull(sslUtil.getKeyManagers());

    assertNull(sslUtil.getTrustManagers());

    assertNotNull(sslUtil.createSSLContext());

    assertNotNull(sslUtil.createSSLContext("TLSv1"));

    SSLContext c = SSLContext.getInstance("TLSv1");
    String p = c.getProvider().getName();

    assertNotNull(sslUtil.createSSLContext("TLSv1", p));

    assertNotNull(sslUtil.createSSLSocketFactory());

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1"));

    if (originalEnabledSSLProtocols.contains("TLSv1.1"))
    {
      assertNotNull(sslUtil.createSSLContext("TLSv1.1"));

      c = SSLContext.getInstance("TLSv1.1");
      p = c.getProvider().getName();

      assertNotNull(sslUtil.createSSLContext("TLSv1.1", p));

      assertNotNull(sslUtil.createSSLSocketFactory());

      assertNotNull(sslUtil.createSSLSocketFactory("TLSv1.1"));

      assertNotNull(sslUtil.createSSLSocketFactory("TLSv1.1", p));
    }

    if (originalEnabledSSLProtocols.contains("TLSv1.2"))
    {
      assertNotNull(sslUtil.createSSLContext("TLSv1.2"));

      c = SSLContext.getInstance("TLSv1.2");
      p = c.getProvider().getName();

      assertNotNull(sslUtil.createSSLContext("TLSv1.2", p));

      assertNotNull(sslUtil.createSSLSocketFactory());

      assertNotNull(sslUtil.createSSLSocketFactory("TLSv1.2"));

      assertNotNull(sslUtil.createSSLSocketFactory("TLSv1.2", p));
    }
  }



  /**
   * Tests the second constructor with a {@code null} trust manager.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructor2Null()
         throws Exception
  {
    SSLUtil sslUtil = new SSLUtil((TrustManager) null);

    assertNull(sslUtil.getKeyManagers());

    assertNull(sslUtil.getTrustManagers());

    assertNotNull(sslUtil.createSSLContext());

    assertNotNull(sslUtil.createSSLContext("TLSv1"));

    SSLContext c = SSLContext.getInstance("TLSv1");
    String p = c.getProvider().getName();

    assertNotNull(sslUtil.createSSLContext("TLSv1", p));

    assertNotNull(sslUtil.createSSLSocketFactory());

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1"));

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1", p));
  }



  /**
   * Tests the second constructor with a non-{@code null} trust manager.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructor2NotNull()
         throws Exception
  {
    SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());

    assertNull(sslUtil.getKeyManagers());

    assertNotNull(sslUtil.getTrustManagers());

    assertNotNull(sslUtil.createSSLContext());

    assertNotNull(sslUtil.createSSLContext("TLSv1"));

    SSLContext c = SSLContext.getInstance("TLSv1");
    String p = c.getProvider().getName();

    assertNotNull(sslUtil.createSSLContext("TLSv1", p));

    assertNotNull(sslUtil.createSSLSocketFactory());

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1"));

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1", p));
  }



  /**
   * Tests the third constructor with a {@code null} set of trust managers.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructor3Null()
         throws Exception
  {
    SSLUtil sslUtil = new SSLUtil((TrustManager[]) null);

    assertNull(sslUtil.getKeyManagers());

    assertNull(sslUtil.getTrustManagers());

    assertNotNull(sslUtil.createSSLContext());

    assertNotNull(sslUtil.createSSLContext("TLSv1"));

    SSLContext c = SSLContext.getInstance("TLSv1");
    String p = c.getProvider().getName();

    assertNotNull(sslUtil.createSSLContext("TLSv1", p));

    assertNotNull(sslUtil.createSSLSocketFactory());

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1"));

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1", p));
  }



  /**
   * Tests the third constructor with an empty set of trust managers.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructor3Empty()
         throws Exception
  {
    SSLUtil sslUtil = new SSLUtil(new TrustManager[0]);

    assertNull(sslUtil.getKeyManagers());

    assertNull(sslUtil.getTrustManagers());

    assertNotNull(sslUtil.createSSLContext());

    assertNotNull(sslUtil.createSSLContext("TLSv1"));

    SSLContext c = SSLContext.getInstance("TLSv1");
    String p = c.getProvider().getName();

    assertNotNull(sslUtil.createSSLContext("TLSv1", p));

    assertNotNull(sslUtil.createSSLSocketFactory());

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1"));

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1", p));
  }



  /**
   * Tests the third constructor with a non-empty set of trust managers.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructor3NotEmpty()
         throws Exception
  {
    SSLUtil sslUtil =
         new SSLUtil(new TrustManager[] { new TrustAllTrustManager() });

    assertNull(sslUtil.getKeyManagers());

    assertNotNull(sslUtil.getTrustManagers());

    assertNotNull(sslUtil.createSSLContext());

    assertNotNull(sslUtil.createSSLContext("TLSv1"));

    SSLContext c = SSLContext.getInstance("TLSv1");
    String p = c.getProvider().getName();

    assertNotNull(sslUtil.createSSLContext("TLSv1", p));

    assertNotNull(sslUtil.createSSLSocketFactory());

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1"));

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1", p));
  }



  /**
   * Tests the fourth constructor with {@code null} key and trust managers.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructor4Null()
         throws Exception
  {
    SSLUtil sslUtil = new SSLUtil((KeyManager) null, (TrustManager) null);

    assertNull(sslUtil.getKeyManagers());

    assertNull(sslUtil.getTrustManagers());

    assertNotNull(sslUtil.createSSLContext());

    assertNotNull(sslUtil.createSSLContext("TLSv1"));

    SSLContext c = SSLContext.getInstance("TLSv1");
    String p = c.getProvider().getName();

    assertNotNull(sslUtil.createSSLContext("TLSv1", p));

    assertNotNull(sslUtil.createSSLSocketFactory());

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1"));

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1", p));
  }



  /**
   * Tests the fourth constructor with non-{@code null} key and trust managers.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructor4NotNull()
         throws Exception
  {
    SSLUtil sslUtil = new SSLUtil(
         new KeyStoreKeyManager(getJKSKeyStorePath(), getJKSKeyStorePIN()),
         new TrustAllTrustManager());

    assertNotNull(sslUtil.getKeyManagers());

    assertNotNull(sslUtil.getTrustManagers());

    assertNotNull(sslUtil.createSSLContext());

    assertNotNull(sslUtil.createSSLContext("TLSv1"));

    SSLContext c = SSLContext.getInstance("TLSv1");
    String p = c.getProvider().getName();

    assertNotNull(sslUtil.createSSLContext("TLSv1", p));

    assertNotNull(sslUtil.createSSLSocketFactory());

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1"));

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1", p));

    assertNotNull(sslUtil.createSSLServerSocketFactory());

    assertNotNull(sslUtil.createSSLServerSocketFactory("TLSv1"));

    assertNotNull(sslUtil.createSSLServerSocketFactory("TLSv1", p));
  }



  /**
   * Tests the fifth constructor with a {@code null} set of trust managers.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructor5Null()
         throws Exception
  {
    SSLUtil sslUtil = new SSLUtil((KeyManager[]) null, (TrustManager[]) null);

    assertNull(sslUtil.getKeyManagers());

    assertNull(sslUtil.getTrustManagers());

    assertNotNull(sslUtil.createSSLContext());

    assertNotNull(sslUtil.createSSLContext("TLSv1"));

    SSLContext c = SSLContext.getInstance("TLSv1");
    String p = c.getProvider().getName();

    assertNotNull(sslUtil.createSSLContext("TLSv1", p));

    assertNotNull(sslUtil.createSSLSocketFactory());

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1"));

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1", p));
  }



  /**
   * Tests the fifth constructor with an empty set of trust managers.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructor5Empty()
         throws Exception
  {
    SSLUtil sslUtil = new SSLUtil(new KeyManager[0], new TrustManager[0]);

    assertNull(sslUtil.getKeyManagers());

    assertNull(sslUtil.getTrustManagers());

    assertNotNull(sslUtil.createSSLContext());

    assertNotNull(sslUtil.createSSLContext("TLSv1"));

    SSLContext c = SSLContext.getInstance("TLSv1");
    String p = c.getProvider().getName();

    assertNotNull(sslUtil.createSSLContext("TLSv1", p));

    assertNotNull(sslUtil.createSSLSocketFactory());

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1"));

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1", p));
  }



  /**
   * Tests the fifth constructor with a non-empty set of trust managers.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructor5NotEmpty()
         throws Exception
  {
    SSLUtil sslUtil = new SSLUtil(
         new KeyManager[] { new KeyStoreKeyManager(getJKSKeyStorePath(),
                                                   getJKSKeyStorePIN()) },
         new TrustManager[] { new TrustAllTrustManager() });

    assertNotNull(sslUtil.getKeyManagers());

    assertNotNull(sslUtil.getTrustManagers());

    assertNotNull(sslUtil.createSSLContext());

    assertNotNull(sslUtil.createSSLContext("TLSv1"));

    SSLContext c = SSLContext.getInstance("TLSv1");
    String p = c.getProvider().getName();

    assertNotNull(sslUtil.createSSLContext("TLSv1", p));

    assertNotNull(sslUtil.createSSLSocketFactory());

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1"));

    assertNotNull(sslUtil.createSSLSocketFactory("TLSv1", p));

    assertNotNull(sslUtil.createSSLServerSocketFactory());

    assertNotNull(sslUtil.createSSLServerSocketFactory("TLSv1"));

    assertNotNull(sslUtil.createSSLServerSocketFactory("TLSv1", p));
  }



  /**
   * Uses the SSL helper to establish an SSL-based connection to the Directory
   * Server.
   * <BR><BR>
   * Access to an SSL-enabled Directory Server instance is required for complete
   * processing.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConnectUsingSSL()
         throws Exception
  {
    if (! isSSLEnabledDirectoryInstanceAvailable())
    {
      return;
    }

    SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());

    LDAPConnection conn = new LDAPConnection(sslUtil.createSSLSocketFactory());

    conn.connect(getTestHost(), getTestSSLPort());
    conn.bind(getTestBindDN(), getTestBindPassword());

    assertNotNull(conn.getRootDSE());

    conn.close();
  }



  /**
   * Uses the SSL helper to use StartTLS to secure an existing insecure
   * connection.
   * <BR><BR>
   * Access to an SSL-enabled Directory Server instance is required for complete
   * processing.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testUseStartTLS()
         throws Exception
  {
    if (! isSSLEnabledDirectoryInstanceAvailable())
    {
      return;
    }

    LDAPConnection conn = new LDAPConnection();
    conn.connect(getTestHost(), getTestPort());
    conn.bind(getTestBindDN(), getTestBindPassword());
    assertNotNull(conn.getRootDSE());

    SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
    ExtendedResult r = conn.processExtendedOperation(
         new StartTLSExtendedRequest(sslUtil.createSSLContext()));
    assertEquals(r.getResultCode(), ResultCode.SUCCESS);

    assertNotNull(conn.getRootDSE());

    conn.close();
  }



  /**
   * Tests methods involving the default SSL protocol.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testDefaultSSLProtocol()
         throws Exception
  {
    final String defaultProtocol = SSLUtil.getDefaultSSLProtocol();
    assertNotNull(defaultProtocol);

    SSLUtil.setDefaultSSLProtocol("SSL");
    assertEquals(SSLUtil.getDefaultSSLProtocol(), "SSL");

    SSLUtil.setDefaultSSLProtocol("SSLv3");
    assertEquals(SSLUtil.getDefaultSSLProtocol(), "SSLv3");

    SSLUtil.setDefaultSSLProtocol("TLSv1");
    assertEquals(SSLUtil.getDefaultSSLProtocol(), "TLSv1");

    SSLUtil.setDefaultSSLProtocol(defaultProtocol);
    assertEquals(SSLUtil.getDefaultSSLProtocol(), defaultProtocol);
  }



  /**
   * Tests methods involving the enabled SSL protocols.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testEnabledSSLProtocols()
         throws Exception
  {
    final Set<String> enabledProtocols = SSLUtil.getEnabledSSLProtocols();

    SSLUtil.setEnabledSSLProtocols(null);
    assertNotNull(SSLUtil.getEnabledSSLProtocols());
    assertTrue(SSLUtil.getEnabledSSLProtocols().isEmpty());

    SSLUtil.setEnabledSSLProtocols(Arrays.asList("foo"));
    assertNotNull(SSLUtil.getEnabledSSLProtocols());
    assertFalse(SSLUtil.getEnabledSSLProtocols().isEmpty());
    assertEquals(SSLUtil.getEnabledSSLProtocols(),
         new HashSet<String>(Arrays.asList("foo")));

    SSLUtil.setEnabledSSLProtocols(new HashSet<String>(0));
    assertNotNull(SSLUtil.getEnabledSSLProtocols());
    assertTrue(SSLUtil.getEnabledSSLProtocols().isEmpty());

    SSLUtil.setEnabledSSLProtocols(Arrays.asList("bar", "baz"));
    assertNotNull(SSLUtil.getEnabledSSLProtocols());
    assertFalse(SSLUtil.getEnabledSSLProtocols().isEmpty());
    assertEquals(SSLUtil.getEnabledSSLProtocols(),
         new HashSet<String>(Arrays.asList("bar", "baz")));

    SSLUtil.setEnabledSSLProtocols(enabledProtocols);
    assertNotNull(SSLUtil.getEnabledSSLProtocols());
    assertFalse(SSLUtil.getEnabledSSLProtocols().isEmpty());
    assertEquals(SSLUtil.getEnabledSSLProtocols(), enabledProtocols);
  }



  /**
   * Tests the behavior of the {@code configureSSLDefault} method when no
   * properties are set.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConfigureSSLDefaultsNoProperties()
         throws Exception
  {
    System.clearProperty(SSLUtil.PROPERTY_DEFAULT_SSL_PROTOCOL);
    System.clearProperty(SSLUtil.PROPERTY_ENABLED_SSL_PROTOCOLS);

    SSLUtil.configureSSLDefaults();

    assertNotNull(SSLUtil.getDefaultSSLProtocol());

    assertNotNull(SSLUtil.getEnabledSSLProtocols());
    assertFalse(SSLUtil.getEnabledSSLProtocols().isEmpty());

    if (SSLUtil.getDefaultSSLProtocol().equals("TLSv1.2"))
    {
      assertEquals(SSLUtil.getEnabledSSLProtocols(),
           new HashSet<String>(Arrays.asList("TLSv1", "TLSv1.1", "TLSv1.2")));
    }
    else if (SSLUtil.getDefaultSSLProtocol().equals("TLSv1.1"))
    {
      assertEquals(SSLUtil.getEnabledSSLProtocols(),
           new HashSet<String>(Arrays.asList("TLSv1", "TLSv1.1")));
    }
    else
    {
      assertEquals(SSLUtil.getEnabledSSLProtocols(),
           new HashSet<String>(Arrays.asList("TLSv1")));
    }
  }



  /**
   * Tests the behavior of the {@code configureSSLDefault} method when both
   * properties are set.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConfigureSSLDefaultsBothProperties()
         throws Exception
  {
    System.setProperty(SSLUtil.PROPERTY_DEFAULT_SSL_PROTOCOL, "def");
    System.setProperty(SSLUtil.PROPERTY_ENABLED_SSL_PROTOCOLS, "e1,e2 , e3,e4");

    SSLUtil.configureSSLDefaults();

    assertNotNull(SSLUtil.getDefaultSSLProtocol());
    assertEquals(SSLUtil.getDefaultSSLProtocol(), "def");

    assertNotNull(SSLUtil.getEnabledSSLProtocols());
    assertFalse(SSLUtil.getEnabledSSLProtocols().isEmpty());
    assertEquals(SSLUtil.getEnabledSSLProtocols(),
         new HashSet<String>(Arrays.asList("e1", "e2", "e3", "e4")));
  }



  /**
   * Tests the behavior of the {@code configureSSLDefault} method when the
   * default protocol is set via property to TLS and the enabled protocols
   * are not set with a property.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConfigureSSLDefaultsTLS()
         throws Exception
  {
    System.setProperty(SSLUtil.PROPERTY_DEFAULT_SSL_PROTOCOL, "TLS");
    System.clearProperty(SSLUtil.PROPERTY_ENABLED_SSL_PROTOCOLS);

    SSLUtil.configureSSLDefaults();

    assertNotNull(SSLUtil.getDefaultSSLProtocol());
    assertEquals(SSLUtil.getDefaultSSLProtocol(), "TLS");

    assertNotNull(SSLUtil.getEnabledSSLProtocols());
    assertFalse(SSLUtil.getEnabledSSLProtocols().isEmpty());
    assertEquals(SSLUtil.getEnabledSSLProtocols(),
         new HashSet<String>(Arrays.asList("TLSv1")));
  }



  /**
   * Tests the behavior of the {@code configureSSLDefault} method when the
   * default protocol is set via property to TLSv1 and the enabled protocols are
   * not set with a property.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConfigureSSLDefaultsTLS1()
         throws Exception
  {
    System.setProperty(SSLUtil.PROPERTY_DEFAULT_SSL_PROTOCOL, "TLSv1");
    System.clearProperty(SSLUtil.PROPERTY_ENABLED_SSL_PROTOCOLS);

    SSLUtil.configureSSLDefaults();

    assertNotNull(SSLUtil.getDefaultSSLProtocol());
    assertEquals(SSLUtil.getDefaultSSLProtocol(), "TLSv1");

    assertNotNull(SSLUtil.getEnabledSSLProtocols());
    assertFalse(SSLUtil.getEnabledSSLProtocols().isEmpty());
    assertEquals(SSLUtil.getEnabledSSLProtocols(),
         new HashSet<String>(Arrays.asList("TLSv1")));
  }



  /**
   * Tests the behavior of the {@code configureSSLDefault} method when the
   * default protocol is set via property to TLSv1.1 and the enabled protocols
   * are not set with a property.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConfigureSSLDefaultsTLS11()
         throws Exception
  {
    System.setProperty(SSLUtil.PROPERTY_DEFAULT_SSL_PROTOCOL, "TLSv1.1");
    System.clearProperty(SSLUtil.PROPERTY_ENABLED_SSL_PROTOCOLS);

    SSLUtil.configureSSLDefaults();

    assertNotNull(SSLUtil.getDefaultSSLProtocol());
    assertEquals(SSLUtil.getDefaultSSLProtocol(), "TLSv1.1");

    assertNotNull(SSLUtil.getEnabledSSLProtocols());
    assertFalse(SSLUtil.getEnabledSSLProtocols().isEmpty());
    assertEquals(SSLUtil.getEnabledSSLProtocols(),
         new HashSet<String>(Arrays.asList("TLSv1", "TLSv1.1")));
  }



  /**
   * Tests the behavior of the {@code configureSSLDefault} method when the
   * default protocol is set via property to TLSv1.2 and the enabled protocols
   * are not set with a property.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConfigureSSLDefaultsTLS12()
         throws Exception
  {
    System.setProperty(SSLUtil.PROPERTY_DEFAULT_SSL_PROTOCOL, "TLSv1.2");
    System.clearProperty(SSLUtil.PROPERTY_ENABLED_SSL_PROTOCOLS);

    SSLUtil.configureSSLDefaults();

    assertNotNull(SSLUtil.getDefaultSSLProtocol());
    assertEquals(SSLUtil.getDefaultSSLProtocol(), "TLSv1.2");

    assertNotNull(SSLUtil.getEnabledSSLProtocols());
    assertFalse(SSLUtil.getEnabledSSLProtocols().isEmpty());
    assertEquals(SSLUtil.getEnabledSSLProtocols(),
         new HashSet<String>(Arrays.asList("TLSv1", "TLSv1.1", "TLSv1.2")));
  }



  /**
   * Tests the {@code applyEnabledSSLProtocols} method with a null socket.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testApplyEnabledSSLProtocolsNull()
         throws Exception
  {
    SSLUtil.applyEnabledSSLProtocols(null);
  }



  /**
   * Tests the {@code applyEnabledSSLProtocols} method with a socket that is not
   * an SSL socket.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testApplyEnabledSSLProtocolsNonSSLSocket()
         throws Exception
  {
    final InMemoryDirectoryServer ds = getTestDS();

    final Socket s = new Socket("localhost", ds.getListenPort());
    SSLUtil.applyEnabledSSLProtocols(s);
    s.getOutputStream().write(new byte[]
         { 0x30, 0x05, 0x02, 0x01, 0x01, 0x42, 0x00 });
    s.getOutputStream().flush();
    s.close();
  }



  /**
   * Tests the {@code applyEnabledSSLProtocols} method with an SSL socket and
   * the default settings.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testApplyEnabledSSLProtocolsSSLSocketDefaults()
         throws Exception
  {
    final InMemoryDirectoryServer ds = getTestDSWithSSL();

    final SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
    final Socket s = sslUtil.createSSLSocketFactory().createSocket(
         "localhost", ds.getListenPort("LDAPS"));
    assertTrue(s instanceof SSLSocket);

    SSLUtil.applyEnabledSSLProtocols(s);

    final SSLSocket sslSocket = (SSLSocket) s;
    assertNotNull(sslSocket.getEnabledProtocols());
    assertTrue(sslSocket.getEnabledProtocols().length > 0);

    s.getOutputStream().write(new byte[]
         { 0x30, 0x05, 0x02, 0x01, 0x01, 0x42, 0x00 });
    s.getOutputStream().flush();
    s.close();
  }



  /**
   * Tests the {@code applyEnabledSSLProtocols} method with an SSL socket and
   * an empty set of enabled protocols.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testApplyEnabledSSLProtocolsEnabledEmptySet()
         throws Exception
  {
    final InMemoryDirectoryServer ds = getTestDSWithSSL();

    final SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
    final Socket s = sslUtil.createSSLSocketFactory().createSocket(
         "localhost", ds.getListenPort("LDAPS"));
    assertTrue(s instanceof SSLSocket);

    SSLUtil.setEnabledSSLProtocols(null);
    SSLUtil.applyEnabledSSLProtocols(s);

    final SSLSocket sslSocket = (SSLSocket) s;
    assertNotNull(sslSocket.getEnabledProtocols());
    assertTrue(sslSocket.getEnabledProtocols().length > 0);

    s.getOutputStream().write(new byte[]
         { 0x30, 0x05, 0x02, 0x01, 0x01, 0x42, 0x00 });
    s.getOutputStream().flush();
    s.close();
  }



  /**
   * Tests the {@code applyEnabledSSLProtocols} method with an SSL socket and
   * only "TLSv1" in the set of enabled protocols.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testApplyEnabledSSLProtocolsEnabledOnlyTLSv1()
         throws Exception
  {
    final InMemoryDirectoryServer ds = getTestDSWithSSL();

    final SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
    final Socket s = sslUtil.createSSLSocketFactory().createSocket(
         "localhost", ds.getListenPort("LDAPS"));
    assertTrue(s instanceof SSLSocket);

    SSLUtil.setEnabledSSLProtocols(Arrays.asList("TLSv1"));
    SSLUtil.applyEnabledSSLProtocols(s);

    final SSLSocket sslSocket = (SSLSocket) s;
    assertNotNull(sslSocket.getEnabledProtocols());
    assertEquals(sslSocket.getEnabledProtocols().length, 1);
    assertEquals(sslSocket.getEnabledProtocols()[0], "TLSv1");

    s.getOutputStream().write(new byte[]
         { 0x30, 0x05, 0x02, 0x01, 0x01, 0x42, 0x00 });
    s.getOutputStream().flush();
    s.close();
  }



  /**
   * Tests the {@code applyEnabledSSLProtocols} method with an SSL socket and
   * only "TLSv1.1" in the set of enabled protocols.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testApplyEnabledSSLProtocolsEnabledOnlyTLSv11()
         throws Exception
  {
    if (originalEnabledSSLProtocols.contains("TLSv1.1"))
    {
      final InMemoryDirectoryServer ds = getTestDSWithSSL();

      final SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
      final Socket s = sslUtil.createSSLSocketFactory().createSocket(
           "localhost", ds.getListenPort("LDAPS"));
      assertTrue(s instanceof SSLSocket);

      SSLUtil.setEnabledSSLProtocols(Arrays.asList("TLSv1.1"));
      SSLUtil.applyEnabledSSLProtocols(s);

      final SSLSocket sslSocket = (SSLSocket) s;
      assertNotNull(sslSocket.getEnabledProtocols());
      assertEquals(sslSocket.getEnabledProtocols().length, 1);
      assertEquals(sslSocket.getEnabledProtocols()[0], "TLSv1.1");

      s.getOutputStream().write(new byte[]
           { 0x30, 0x05, 0x02, 0x01, 0x01, 0x42, 0x00 });
      s.getOutputStream().flush();
      s.close();
    }
  }



  /**
   * Tests the {@code applyEnabledSSLProtocols} method with an SSL socket and
   * only "TLSv1.2" in the set of enabled protocols.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testApplyEnabledSSLProtocolsEnabledOnlyTLSv12()
         throws Exception
  {
    if (originalEnabledSSLProtocols.contains("TLSv1.2"))
    {
      final InMemoryDirectoryServer ds = getTestDSWithSSL();

      final SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
      final Socket s = sslUtil.createSSLSocketFactory().createSocket(
           "localhost", ds.getListenPort("LDAPS"));
      assertTrue(s instanceof SSLSocket);

      SSLUtil.setEnabledSSLProtocols(Arrays.asList("TLSv1.2"));
      SSLUtil.applyEnabledSSLProtocols(s);

      final SSLSocket sslSocket = (SSLSocket) s;
      assertNotNull(sslSocket.getEnabledProtocols());
      assertEquals(sslSocket.getEnabledProtocols().length, 1);
      assertEquals(sslSocket.getEnabledProtocols()[0], "TLSv1.2");

      s.getOutputStream().write(new byte[]
           { 0x30, 0x05, 0x02, 0x01, 0x01, 0x42, 0x00 });
      s.getOutputStream().flush();
      s.close();
    }
  }



  /**
   * Tests the {@code applyEnabledSSLProtocols} method with an SSL socket and a
   * full set of enabled protocols.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testApplyEnabledSSLProtocolsEnabledFullSet()
         throws Exception
  {
    final InMemoryDirectoryServer ds = getTestDSWithSSL();

    final SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
    final Socket s = sslUtil.createSSLSocketFactory().createSocket(
         "localhost", ds.getListenPort("LDAPS"));
    assertTrue(s instanceof SSLSocket);

    SSLUtil.setEnabledSSLProtocols(Arrays.asList("SSLv3", "TLSv1", "TLSv1.1",
         "TLSv1.2"));
    SSLUtil.applyEnabledSSLProtocols(s);

    final SSLSocket sslSocket = (SSLSocket) s;
    assertNotNull(sslSocket.getEnabledProtocols());
    assertTrue(sslSocket.getEnabledProtocols().length > 0);

    boolean sslV3Enabled = false;
    boolean tlsV1Enabled = false;
    boolean tlsV11Enabled = false;
    boolean tlsV12Enabled = false;
    for (final String p : sslSocket.getEnabledProtocols())
    {
      if (p.equals("SSLv3"))
      {
        sslV3Enabled = true;
      }
      else if (p.equals("TLSv1"))
      {
        tlsV1Enabled = true;
      }
      else if (p.equals("TLSv1.1"))
      {
        tlsV11Enabled = true;
      }
      else if (p.equals("TLSv1.2"))
      {
        tlsV12Enabled = true;
      }
      else
      {
        fail("Unexpected enabled protocol '" + p + '\'');
      }
    }

    assertTrue(sslV3Enabled);
    assertTrue(tlsV1Enabled);

    if (originalDefaultSSLProtocol.equals("TLSv1.2"))
    {
      assertTrue(tlsV11Enabled);
      assertTrue(tlsV12Enabled);
    }
    else if (originalDefaultSSLProtocol.equals("TLSv1.1"))
    {
      assertTrue(tlsV11Enabled);
    }

    s.getOutputStream().write(new byte[]
         { 0x30, 0x05, 0x02, 0x01, 0x01, 0x42, 0x00 });
    s.getOutputStream().flush();
    s.close();
  }



  /**
   * Tests the {@code applyEnabledSSLProtocols} method with an SSL socket and
   * only unsupported values in the set of enabled protocols.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testApplyEnabledSSLProtocolsEnabledBogusSet()
         throws Exception
  {
    final InMemoryDirectoryServer ds = getTestDSWithSSL();

    final SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
    final Socket s = sslUtil.createSSLSocketFactory().createSocket(
         "localhost", ds.getListenPort("LDAPS"));
    assertTrue(s instanceof SSLSocket);

    SSLUtil.setEnabledSSLProtocols(Arrays.asList("unsupported1",
         "unsupported2"));
    try
    {
      SSLUtil.applyEnabledSSLProtocols(s);
      fail("Expected an exception because of unsupported enabled protocols");
    }
    catch (final LDAPException le)
    {
      // This was expected.
    }
    finally
    {
      s.getOutputStream().write(new byte[]
           { 0x30, 0x05, 0x02, 0x01, 0x01, 0x42, 0x00 });
      s.getOutputStream().flush();
      s.close();
    }
  }
}
