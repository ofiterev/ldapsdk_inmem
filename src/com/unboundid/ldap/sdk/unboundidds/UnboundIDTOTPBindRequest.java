/*
 * Copyright 2012-2019 Ping Identity Corporation
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2015-2019 Ping Identity Corporation
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
package com.unboundid.ldap.sdk.unboundidds;



import java.util.ArrayList;

import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.InternalSDKHelper;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SASLBindRequest;
import com.unboundid.util.NotExtensible;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.Validator;



/**
 * This class provides support for an UnboundID-proprietary SASL mechanism that
 * uses the time-based one-time password mechanism (TOTP) as described in
 * <A HREF="http://www.ietf.org/rfc/rfc6238.txt">RFC 6238</A>, optionally (based
 * on the server configuration) in conjunction with a static password for a form
 * of multifactor authentication.
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
 * The name for this SASL mechanism is "UNBOUNDID-TOTP".  An UNBOUNDID-TOTP SASL
 * bind request MUST include SASL credentials with the following ASN.1 encoding:
 * <BR><BR>
 * <PRE>
 * UnboundIDTOTPCredentials ::= SEQUENCE {
 *   authenticationID  [0] OCTET STRING,
 *   authorizationID   [1] OCTET STRING OPTIONAL,
 *   totpPassword      [2] OCTET STRING,
 *   staticPassword    [3] OCTET STRING OPTIONAL }
 * </PRE>
 * <BR><BR>
 * Note that this class is abstract, with two different concrete
 * implementations:  the {@link SingleUseTOTPBindRequest} class may be used for
 * cases in which the one-time password will be obtained from an external source
 * (e.g., provided by the user, perhaps using the Google Authenticator
 * application), and the {@link ReusableTOTPBindRequest} class may be used for
 * cases in which the one-time password should be generated by the LDAP SDK
 * itself.  Because the {@code SingleUseTOTPBindRequest} class contains a
 * point-in-time password, it cannot be used for re-authentication (e.g., for
 * use with a connection pool, following referrals, or with the auto-reconnect
 * feature).  If TOTP authentication should be used in contexts where one or
 * more of these may be needed, then the dynamic variant should be used.
 */
@NotExtensible()
@ThreadSafety(level=ThreadSafetyLevel.NOT_THREADSAFE)
public abstract class UnboundIDTOTPBindRequest
       extends SASLBindRequest
{
  /**
   * The name for the UnboundID TOTP SASL mechanism.
   */
  public static final String UNBOUNDID_TOTP_MECHANISM_NAME = "UNBOUNDID-TOTP";



  /**
   * The BER type for the authentication ID included in the request.
   */
  static final byte TYPE_AUTHENTICATION_ID = (byte) 0x80;



  /**
   * The BER type for the authorization ID included in the request.
   */
  static final byte TYPE_AUTHORIZATION_ID = (byte) 0x81;



  /**
   * The BER type for the TOTP password included in the request.
   */
  static final byte TYPE_TOTP_PASSWORD = (byte) 0x82;



  /**
   * The BER type for the static password included in the request.
   */
  static final byte TYPE_STATIC_PASSWORD = (byte) 0x83;



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = -8751931123826994145L;



  // The static password for the target user, if provided.
  private final ASN1OctetString staticPassword;

  // The message ID from the last LDAP message sent from this request.
  private volatile int messageID = -1;

  // The authentication identity for the bind.
  private final String authenticationID;

  // The authorization identity for the bind, if provided.
  private final String authorizationID;



  /**
   * Creates a new TOTP bind request with the provided information.
   *
   * @param  authenticationID  The authentication identity for the bind request.
   *                           It must not be {@code null}, and must be in the
   *                           form "u:" followed by a username, or "dn:"
   *                           followed by a DN.
   * @param  authorizationID   The authorization identity for the bind request.
   *                           It may be {@code null} if the authorization
   *                           identity should be the same as the authentication
   *                           identity.  If an authorization identity is
   *                           specified, it must be in the form "u:" followed
   *                           by a username, or "dn:" followed by a DN.  The
   *                           value "dn:" may indicate an authorization
   *                           identity of the anonymous user.
   * @param  staticPassword    The static password for the target user.  It may
   *                           be {@code null} if only the one-time password is
   *                           to be used for authentication (which may or may
   *                           not be allowed by the server).
   * @param  controls          The set of controls to include in the bind
   *                           request.
   */
  protected UnboundIDTOTPBindRequest(final String authenticationID,
                                     final String authorizationID,
                                     final String staticPassword,
                                     final Control... controls)
  {
    super(controls);

    Validator.ensureNotNull(authenticationID);

    this.authenticationID = authenticationID;
    this.authorizationID  = authorizationID;

    if (staticPassword == null)
    {
      this.staticPassword = null;
    }
    else
    {
      this.staticPassword =
           new ASN1OctetString(TYPE_STATIC_PASSWORD, staticPassword);
    }
  }



  /**
   * Creates a new TOTP bind request with the provided information.
   *
   * @param  authenticationID  The authentication identity for the bind request.
   *                           It must not be {@code null}, and must be in the
   *                           form "u:" followed by a username, or "dn:"
   *                           followed by a DN.
   * @param  authorizationID   The authorization identity for the bind request.
   *                           It may be {@code null} if the authorization
   *                           identity should be the same as the authentication
   *                           identity.  If an authorization identity is
   *                           specified, it must be in the form "u:" followed
   *                           by a username, or "dn:" followed by a DN.  The
   *                           value "dn:" may indicate an authorization
   *                           identity of the anonymous user.
   * @param  staticPassword    The static password for the target user.  It may
   *                           be {@code null} if only the one-time password is
   *                           to be used for authentication (which may or may
   *                           not be allowed by the server).
   * @param  controls          The set of controls to include in the bind
   *                           request.
   */
  protected UnboundIDTOTPBindRequest(final String authenticationID,
                                     final String authorizationID,
                                     final byte[] staticPassword,
                                     final Control... controls)
  {
    super(controls);

    Validator.ensureNotNull(authenticationID);

    this.authenticationID = authenticationID;
    this.authorizationID  = authorizationID;

    if (staticPassword == null)
    {
      this.staticPassword = null;
    }
    else
    {
      this.staticPassword =
           new ASN1OctetString(TYPE_STATIC_PASSWORD, staticPassword);
    }
  }



  /**
   * Creates a new TOTP bind request with the provided information.
   *
   * @param  authenticationID  The authentication identity for the bind request.
   *                           It must not be {@code null}, and must be in the
   *                           form "u:" followed by a username, or "dn:"
   *                           followed by a DN.
   * @param  authorizationID   The authorization identity for the bind request.
   *                           It may be {@code null} if the authorization
   *                           identity should be the same as the authentication
   *                           identity.  If an authorization identity is
   *                           specified, it must be in the form "u:" followed
   *                           by a username, or "dn:" followed by a DN.  The
   *                           value "dn:" may indicate an authorization
   *                           identity of the anonymous user.
   * @param  staticPassword    The static password for the target user.  It may
   *                           be {@code null} if only the one-time password is
   *                           to be used for authentication (which may or may
   *                           not be allowed by the server).  If it is
   *                           non-{@code null}, then it must have the
   *                           appropriate BER type.
   * @param  controls          The set of controls to include in the bind
   *                           request.
   */
  protected UnboundIDTOTPBindRequest(final String authenticationID,
                                     final String authorizationID,
                                     final ASN1OctetString staticPassword,
                                     final Control... controls)
  {
    super(controls);

    Validator.ensureNotNull(authenticationID);

    if (staticPassword != null)
    {
      Validator.ensureTrue(staticPassword.getType() == TYPE_STATIC_PASSWORD);
    }

    this.authenticationID = authenticationID;
    this.authorizationID  = authorizationID;
    this.staticPassword   = staticPassword;
  }



  /**
   * Retrieves the authentication ID for the bind request.
   *
   * @return  The authentication ID for the bind request.
   */
  public final String getAuthenticationID()
  {
    return authenticationID;
  }



  /**
   * Retrieves the authorization ID for the bind request, if one was provided.
   *
   * @return  The authorization ID for the bind request, or {@code null} if the
   *          authorization ID should be the same as the authentication ID.
   */
  public final String getAuthorizationID()
  {
    return authorizationID;
  }



  /**
   * Retrieves the static password for the bind request, if one was provided.
   *
   * @return  The static password for the bind request, or {@code null} if no
   *          static password was provided and only the one-time password should
   *          be used for authentication.
   */
  public final ASN1OctetString getStaticPassword()
  {
    return staticPassword;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public final String getSASLMechanismName()
  {
    return UNBOUNDID_TOTP_MECHANISM_NAME;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  protected final BindResult process(final LDAPConnection connection,
                                     final int depth)
            throws LDAPException
  {
    messageID = InternalSDKHelper.nextMessageID(connection);
    return sendBindRequest(connection, "", getSASLCredentials(), getControls(),
         getResponseTimeoutMillis(connection));
  }



  /**
   * Retrieves the encoded SASL credentials that may be included in an
   * UNBOUNDID-TOTP SASL bind request.
   *
   * @return  The encoded SASL credentials that may be included in an
   *          UNBOUNDID-TOTP SASL bind request.
   *
   * @throws  LDAPException  If a problem is encountered while attempting to
   *                         obtain the encoded credentials.
   */
  protected abstract ASN1OctetString getSASLCredentials()
            throws LDAPException;



  /**
   * Encodes the provided information in a form suitable for inclusion in an
   * UNBOUNDID-TOTP SASL bind request.
   *
   * @param  authenticationID  The authentication identity for the bind request.
   *                           It must not be {@code null}, and must be in the
   *                           form "u:" followed by a username, or "dn:"
   *                           followed by a DN.
   * @param  authorizationID   The authorization identity for the bind request.
   *                           It may be {@code null} if the authorization
   *                           identity should be the same as the authentication
   *                           identity.  If an authorization identity is
   *                           specified, it must be in the form "u:" followed
   *                           by a username, or "dn:" followed by a DN.  The
   *                           value "dn:" may indicate an authorization
   *                           identity of the anonymous user.
   * @param  totpPassword      The TOTP password to include in the bind request.
   *                           It must not be {@code null}.
   * @param  staticPassword    The static password for the target user.  It may
   *                           be {@code null} if only the one-time password is
   *                           to be used for authentication (which may or may
   *                           not be allowed by the server).
   *
   * @return  The encoded SASL credentials.
   */
  public static ASN1OctetString encodeCredentials(final String authenticationID,
                                    final String authorizationID,
                                    final String totpPassword,
                                    final ASN1OctetString staticPassword)
  {
    Validator.ensureNotNull(authenticationID);
    Validator.ensureNotNull(totpPassword);

    final ArrayList<ASN1Element> elements = new ArrayList<>(4);
    elements.add(new ASN1OctetString(TYPE_AUTHENTICATION_ID, authenticationID));

    if (authorizationID != null)
    {
      elements.add(new ASN1OctetString(TYPE_AUTHORIZATION_ID, authorizationID));
    }

    elements.add(new ASN1OctetString(TYPE_TOTP_PASSWORD, totpPassword));

    if (staticPassword != null)
    {
      if (staticPassword.getType() == TYPE_STATIC_PASSWORD)
      {
        elements.add(staticPassword);
      }
      else
      {
        elements.add(new ASN1OctetString(TYPE_STATIC_PASSWORD,
             staticPassword.getValue()));
      }
    }

    return new ASN1OctetString(new ASN1Sequence(elements).encode());
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public final int getLastMessageID()
  {
    return messageID;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public final void toString(final StringBuilder buffer)
  {
    buffer.append("UnboundIDTOTPBindRequest(authID='");
    buffer.append(authenticationID);
    buffer.append("', ");

    if (authorizationID != null)
    {
      buffer.append("authzID='");
      buffer.append(authorizationID);
      buffer.append("', ");
    }

    buffer.append("includesStaticPassword=");
    buffer.append(staticPassword != null);


    final Control[] controls = getControls();
    if (controls.length > 0)
    {
      buffer.append(", controls={");
      for (int i=0; i < controls.length; i++)
      {
        if (i > 0)
        {
          buffer.append(", ");
        }

        buffer.append(controls[i]);
      }
      buffer.append('}');
    }

    buffer.append(')');
  }
}
