/*
 * Copyright 2014-2019 Ping Identity Corporation
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2014-2019 Ping Identity Corporation
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
package com.unboundid.ldap.listener.interceptor;



import org.testng.annotations.Test;

import com.unboundid.ldap.protocol.DeleteRequestProtocolOp;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPSDKTestCase;
import com.unboundid.ldap.sdk.ResultCode;



/**
 * This class provides test coverage for the intercepted in-memory delete
 * operation.
 */
public final class InterceptedDeleteOperationTestCase
       extends LDAPSDKTestCase
{
  /**
   * Provides basic test coverage for an intercepted delete operation.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testBasics()
         throws Exception
  {
    // Create an intercepted delete operation.  We'll use a null connection,
    // which shouldn't happen naturally but will be sufficient for this test.
    final DeleteRequestProtocolOp requestOp =
         new DeleteRequestProtocolOp(new DeleteRequest(
              "ou=a,dc=example,dc=com"));

    final InterceptedDeleteOperation o = new InterceptedDeleteOperation(
         null, 1, requestOp);
    assertNotNull(o.toString());


    // Test methods for a generic intercepted operation.
    assertNull(o.getClientConnection());

    assertEquals(o.getConnectionID(), -1L);

    assertNull(o.getConnectedAddress());

    assertEquals(o.getConnectedPort(), -1);

    assertEquals(o.getMessageID(), 1);

    assertNull(o.getProperty("propX"));

    o.setProperty("propX", "valX");
    assertNotNull(o.getProperty("propX"));
    assertEquals(o.getProperty("propX"), "valX");
    assertNotNull(o.toString());

    o.setProperty("propX", null);
    assertNull(o.getProperty("propX"));


    // Test methods specific to an intercepted delete operation.
    assertNotNull(o.getRequest());
    assertDNsEqual(o.getRequest().getDN(), "ou=a,dc=example,dc=com");
    assertNotNull(o.toString());

    final DeleteRequest r = o.getRequest().duplicate();
    r.setDN("ou=b,dc=example,dc=com");
    o.setRequest(r);
    assertNotNull(o.toString());

    assertNotNull(o.getRequest());
    assertDNsEqual(o.getRequest().getDN(), "ou=b,dc=example,dc=com");

    assertNull(o.getResult());

    o.setResult(new LDAPResult(o.getMessageID(), ResultCode.SUCCESS));
    assertNotNull(o.getResult());
    assertNotNull(o.toString());
  }
}
