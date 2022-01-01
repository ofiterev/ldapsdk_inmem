/*
 * Copyright 2014-2022 Ping Identity Corporation
 * All Rights Reserved.
 */
/*
 * Copyright 2014-2022 Ping Identity Corporation
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
 * Copyright (C) 2014-2022 Ping Identity Corporation
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

import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPSDKTestCase;
import com.unboundid.ldap.sdk.ResultCode;



/**
 * This class provides test coverage for the intercepted in-memory extended
 * operation.
 */
public final class InterceptedExtendedOperationTestCase
       extends LDAPSDKTestCase
{
  /**
   * Provides basic test coverage for an intercepted extended operation.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testBasics()
         throws Exception
  {
    // Create an intercepted extended operation.  We'll use a null connection,
    // which shouldn't happen naturally but will be sufficient for this test.
    final ExtendedRequestProtocolOp requestOp =
         new ExtendedRequestProtocolOp(new ExtendedRequest("1.2.3.4"));

    final InterceptedExtendedOperation o = new InterceptedExtendedOperation(
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


    // Test methods specific to an intercepted extended operation.
    assertNotNull(o.getRequest());
    assertEquals(o.getRequest().getOID(), "1.2.3.4");
    assertNotNull(o.toString());

    final ExtendedRequest r = new ExtendedRequest("1.2.3.5");
    o.setRequest(r);

    assertNotNull(o.getRequest());
    assertEquals(o.getRequest().getOID(), "1.2.3.5");
    assertNotNull(o.toString());

    assertNull(o.getResult());

    o.setResult(new ExtendedResult(
         new LDAPResult(o.getMessageID(), ResultCode.SUCCESS)));
    assertNotNull(o.getResult());
    assertNotNull(o.toString());
  }
}
