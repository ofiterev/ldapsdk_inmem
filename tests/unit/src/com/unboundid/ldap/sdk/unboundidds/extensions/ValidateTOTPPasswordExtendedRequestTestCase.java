/*
 * Copyright 2012-2022 Ping Identity Corporation
 * All Rights Reserved.
 */
/*
 * Copyright 2012-2022 Ping Identity Corporation
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
 * Copyright (C) 2012-2022 Ping Identity Corporation
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
package com.unboundid.ldap.sdk.unboundidds.extensions;



import org.testng.annotations.Test;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSDKTestCase;



/**
 * This class provides a set of test cases for the
 * {@code ValidateTOTPPasswordExtendedRequest} class.
 */
public final class ValidateTOTPPasswordExtendedRequestTestCase
       extends LDAPSDKTestCase
{
  /**
   * Provides test coverage for the request without any controls.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testWithoutControls()
         throws Exception
  {
    ValidateTOTPPasswordExtendedRequest r =
         new ValidateTOTPPasswordExtendedRequest(
              "uid=test.user,ou=People,dc=example,dc=com",
              "123456");
    r = new ValidateTOTPPasswordExtendedRequest(r);
    r = r.duplicate();

    assertNotNull(r);

    assertNotNull(r.getOID());
    assertEquals(r.getOID(), "1.3.6.1.4.1.30221.2.6.15");

    assertNotNull(r.getValue());

    assertNotNull(r.getUserDN());
    assertEquals(new DN(r.getUserDN()),
         new DN("uid=test.user,ou=People,dc=example,dc=com"));

    assertNotNull(r.getTOTPPassword());
    assertEquals(r.getTOTPPassword(), "123456");

    assertNotNull(r.getExtendedRequestName());

    assertNotNull(r.getControls());
    assertEquals(r.getControls().length, 0);

    assertNotNull(r.toString());
  }



  /**
   * Provides test coverage for the request with controls.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testWithControls()
         throws Exception
  {
    ValidateTOTPPasswordExtendedRequest r =
         new ValidateTOTPPasswordExtendedRequest(
              "uid=test.user,ou=People,dc=example,dc=com",
              "123456",
              new Control("1.2.3.4"),
              new Control("1.2.3.5", true));
    r = new ValidateTOTPPasswordExtendedRequest(r);
    r = r.duplicate();

    assertNotNull(r);

    assertNotNull(r.getOID());
    assertEquals(r.getOID(), "1.3.6.1.4.1.30221.2.6.15");

    assertNotNull(r.getValue());

    assertNotNull(r.getUserDN());
    assertEquals(new DN(r.getUserDN()),
         new DN("uid=test.user,ou=People,dc=example,dc=com"));

    assertNotNull(r.getTOTPPassword());
    assertEquals(r.getTOTPPassword(), "123456");

    assertNotNull(r.getExtendedRequestName());

    assertNotNull(r.getControls());
    assertEquals(r.getControls().length, 2);

    assertNotNull(r.toString());
  }



  /**
   * Tests the behavior when trying to decode an extended request which does
   * not have a value.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { LDAPException.class })
  public void testDecodeWithoutValue()
         throws Exception
  {
    new ValidateTOTPPasswordExtendedRequest(
         new ExtendedRequest("1.3.6.1.4.1.30221.2.6.15"));
  }



  /**
   * Tests the behavior when trying to decode an extended request which has a
   * value that is not valid for a validate TOTP request.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { LDAPException.class })
  public void testDecodeWithMalformedValue()
         throws Exception
  {
    new ValidateTOTPPasswordExtendedRequest(
         new ExtendedRequest("1.3.6.1.4.1.30221.2.6.15",
              new ASN1OctetString("malformed")));
  }
}
