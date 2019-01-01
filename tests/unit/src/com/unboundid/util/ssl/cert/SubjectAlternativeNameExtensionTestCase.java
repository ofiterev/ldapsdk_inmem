/*
 * Copyright 2017-2019 Ping Identity Corporation
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2017-2019 Ping Identity Corporation
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
package com.unboundid.util.ssl.cert;



import java.net.InetAddress;

import org.testng.annotations.Test;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPSDKTestCase;
import com.unboundid.util.OID;



/**
 * This class provides a set of test cases for the
 * SubjectAlternativeNameExtension class.
 */
public final class SubjectAlternativeNameExtensionTestCase
       extends LDAPSDKTestCase
{
  /**
   * Tests a subject alternative name extension with just a single DNS name.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testSingleDNSName()
         throws Exception
  {
    SubjectAlternativeNameExtension e = new SubjectAlternativeNameExtension(
         false,
         new GeneralNamesBuilder().addDNSName("ldap.example.com").build());

    e = new SubjectAlternativeNameExtension(e);

    assertNotNull(e.getOID());
    assertEquals(e.getOID().toString(), "2.5.29.17");

    assertFalse(e.isCritical());

    assertNotNull(e.getValue());

    assertNotNull(e.getGeneralNames());

    assertNotNull(e.getOtherNames());
    assertTrue(e.getOtherNames().isEmpty());

    assertNotNull(e.getRFC822Names());
    assertTrue(e.getRFC822Names().isEmpty());

    assertNotNull(e.getDNSNames());
    assertFalse(e.getDNSNames().isEmpty());

    assertNotNull(e.getX400Addresses());
    assertTrue(e.getX400Addresses().isEmpty());

    assertNotNull(e.getDirectoryNames());
    assertTrue(e.getDirectoryNames().isEmpty());

    assertNotNull(e.getEDIPartyNames());
    assertTrue(e.getEDIPartyNames().isEmpty());

    assertNotNull(e.getUniformResourceIdentifiers());
    assertTrue(e.getUniformResourceIdentifiers().isEmpty());

    assertNotNull(e.getIPAddresses());
    assertTrue(e.getIPAddresses().isEmpty());

    assertNotNull(e.getRegisteredIDs());
    assertTrue(e.getRegisteredIDs().isEmpty());

    assertNotNull(e.getExtensionName());
    assertFalse(e.getExtensionName().equals("2.5.29.17"));

    assertNotNull(e.toString());
  }



  /**
   * Tests a subject alternative name extension with multiple values for all
   * types of names.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testMultipleValuesForAllTypesOfNames()
         throws Exception
  {
    final GeneralNames names = new GeneralNamesBuilder().
         addOtherName(new OID("1.2.3.4"), new ASN1OctetString("otherName1")).
         addOtherName(new OID("1.2.3.5"), new ASN1OctetString("otherName2")).
         addRFC822Name("user1@example.com").
         addRFC822Name("user2@example.com").
         addDNSName("ldap1.example.com").
         addDNSName("ldap2.example.com").
         addX400Address(new ASN1OctetString("x.400Address1")).
         addX400Address(new ASN1OctetString("x.400Address2")).
         addDirectoryName(new DN("dc=example,dc=com")).
         addDirectoryName(new DN("o=example.com")).
         addEDIPartyName(new ASN1OctetString("ediPartyName1")).
         addEDIPartyName(new ASN1OctetString("ediPartyName2")).
         addUniformResourceIdentifier("ldap://ds1.example.com:389/").
         addUniformResourceIdentifier("ldap://ds2.example.com:389/").
         addIPAddress(InetAddress.getByName("127.0.0.1")).
         addIPAddress(InetAddress.getByName("::1")).
         addRegisteredID(new OID("1.2.3.6")).
         addRegisteredID(new OID("1.2.3.7")).
         build();

    SubjectAlternativeNameExtension e =
         new SubjectAlternativeNameExtension(true, names);

    e = new SubjectAlternativeNameExtension(e);

    assertNotNull(e.getOID());
    assertEquals(e.getOID().toString(), "2.5.29.17");

    assertTrue(e.isCritical());

    assertNotNull(e.getValue());

    assertNotNull(e.getGeneralNames());

    assertNotNull(e.getOtherNames());
    assertFalse(e.getOtherNames().isEmpty());

    assertNotNull(e.getRFC822Names());
    assertFalse(e.getRFC822Names().isEmpty());

    assertNotNull(e.getDNSNames());
    assertFalse(e.getDNSNames().isEmpty());

    assertNotNull(e.getX400Addresses());
    assertFalse(e.getX400Addresses().isEmpty());

    assertNotNull(e.getDirectoryNames());
    assertFalse(e.getDirectoryNames().isEmpty());

    assertNotNull(e.getEDIPartyNames());
    assertFalse(e.getEDIPartyNames().isEmpty());

    assertNotNull(e.getUniformResourceIdentifiers());
    assertFalse(e.getUniformResourceIdentifiers().isEmpty());

    assertNotNull(e.getIPAddresses());
    assertFalse(e.getIPAddresses().isEmpty());

    assertNotNull(e.getRegisteredIDs());
    assertFalse(e.getRegisteredIDs().isEmpty());

    assertNotNull(e.getExtensionName());
    assertFalse(e.getExtensionName().equals("2.5.29.17"));

    assertNotNull(e.toString());
  }



  /**
   * Tests a subject alternative name extension with an invalid OID as a
   * registered ID value.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { CertException.class })
  public void testInvalidRegisteredID()
         throws Exception
  {
    new SubjectAlternativeNameExtension(false,
         new GeneralNamesBuilder().addRegisteredID(new OID("1234.56")).build());
  }



  /**
   * Tests the behavior when trying to decode a subject alternative name
   * extension from a malformed extension when using the correct OID for the
   * subject alternative name extension.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { CertException.class })
  public void testDecodeMalformedExtensionWithCorrectOID()
         throws Exception
  {
    new SubjectAlternativeNameExtension(
         new X509CertificateExtension(new OID("2.5.29.17"), false,
              "invalid value".getBytes("UTF-8")));
  }



  /**
   * Tests the behavior when trying to decode a subject alternative name
   * extension from a malformed extension when not using the correct OID for the
   * subject alternative name extension.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { CertException.class })
  public void testDecodeMalformedExtensionWithIncorrectOID()
         throws Exception
  {

    new SubjectAlternativeNameExtension(
         new X509CertificateExtension(new OID("1.2.3.4"), false,
              "invalid value".getBytes("UTF-8")));
  }
}
