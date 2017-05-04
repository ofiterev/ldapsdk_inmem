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
package com.unboundid.ldap.matchingrules;



import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.ThreadSafetyLevel;

import static com.unboundid.ldap.matchingrules.MatchingRuleMessages.*;
import static com.unboundid.util.StaticUtils.*;



/**
 * This class provides an implementation of a matching rule that performs
 * equality and ordering comparisons against values that should be integers.
 * Substring matching is not supported.
 */
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class IntegerMatchingRule
       extends MatchingRule
{
  /**
   * The singleton instance that will be returned from the {@code getInstance}
   * method.
   */
  private static final IntegerMatchingRule INSTANCE =
       new IntegerMatchingRule();



  /**
   * The name for the integerMatch equality matching rule.
   */
  public static final String EQUALITY_RULE_NAME = "integerMatch";



  /**
   * The name for the integerMatch equality matching rule, formatted in all
   * lowercase characters.
   */
  static final String LOWER_EQUALITY_RULE_NAME =
       toLowerCase(EQUALITY_RULE_NAME);



  /**
   * The OID for the integerMatch equality matching rule.
   */
  public static final String EQUALITY_RULE_OID = "2.5.13.14";



  /**
   * The name for the integerOrderingMatch ordering matching rule.
   */
  public static final String ORDERING_RULE_NAME = "integerOrderingMatch";



  /**
   * The name for the integerOrderingMatch ordering matching rule, formatted
   * in all lowercase characters.
   */
  static final String LOWER_ORDERING_RULE_NAME =
       toLowerCase(ORDERING_RULE_NAME);



  /**
   * The OID for the integerOrderingMatch ordering matching rule.
   */
  public static final String ORDERING_RULE_OID = "2.5.13.15";



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = -9056942146971528818L;



  /**
   * Creates a new instance of this integer matching rule.
   */
  public IntegerMatchingRule()
  {
    // No implementation is required.
  }



  /**
   * Retrieves a singleton instance of this matching rule.
   *
   * @return  A singleton instance of this matching rule.
   */
  public static IntegerMatchingRule getInstance()
  {
    return INSTANCE;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getEqualityMatchingRuleName()
  {
    return EQUALITY_RULE_NAME;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getEqualityMatchingRuleOID()
  {
    return EQUALITY_RULE_OID;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getOrderingMatchingRuleName()
  {
    return ORDERING_RULE_NAME;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getOrderingMatchingRuleOID()
  {
    return ORDERING_RULE_OID;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getSubstringMatchingRuleName()
  {
    return null;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getSubstringMatchingRuleOID()
  {
    return null;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public boolean valuesMatch(final ASN1OctetString value1,
                             final ASN1OctetString value2)
         throws LDAPException
  {
    return normalize(value1).equals(normalize(value2));
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public boolean matchesSubstring(final ASN1OctetString value,
                                  final ASN1OctetString subInitial,
                                  final ASN1OctetString[] subAny,
                                  final ASN1OctetString subFinal)
         throws LDAPException
  {
    throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING,
                            ERR_INTEGER_SUBSTRING_MATCHING_NOT_SUPPORTED.get());
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public int compareValues(final ASN1OctetString value1,
                           final ASN1OctetString value2)
         throws LDAPException
  {
    final byte[] norm1Bytes = normalize(value1).getValue();
    final byte[] norm2Bytes = normalize(value2).getValue();

    if (norm1Bytes[0] == '-')
    {
      if (norm2Bytes[0] == '-')
      {
        // Both values are negative.  The smaller negative is the larger value.
        if (norm1Bytes.length < norm2Bytes.length)
        {
          return 1;
        }
        else if (norm1Bytes.length > norm2Bytes.length)
        {
          return -1;
        }
        else
        {
          for (int i=1; i < norm1Bytes.length; i++)
          {
            final int difference = norm2Bytes[i] - norm1Bytes[i];
            if (difference != 0)
            {
              return difference;
            }
          }

          return 0;
        }
      }
      else
      {
        // The first is negative and the second is positive.
        return -1;
      }
    }
    else
    {
      if (norm2Bytes[0] == '-')
      {
        // The first is positive and the second is negative.
        return 1;
      }
      else
      {
        // Both values are positive.
        if (norm1Bytes.length < norm2Bytes.length)
        {
          return -1;
        }
        else if (norm1Bytes.length > norm2Bytes.length)
        {
          return 1;
        }
        else
        {
          for (int i=0; i < norm1Bytes.length; i++)
          {
            final int difference = norm1Bytes[i] - norm2Bytes[i];
            if (difference != 0)
            {
              return difference;
            }
          }

          return 0;
        }
      }
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public ASN1OctetString normalize(final ASN1OctetString value)
         throws LDAPException
  {
    // It is likely that the provided value is already acceptable, so we should
    // try to validate it without any unnecessary allocation.
    final byte[] valueBytes = value.getValue();
    if (valueBytes.length == 0)
    {
      throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX,
                              ERR_INTEGER_ZERO_LENGTH_NOT_ALLOWED.get());
    }

    if ((valueBytes[0] == ' ') || (valueBytes[valueBytes.length-1] == ' '))
    {
      // There is either a leading or trailing space, which needs to be
      // stripped out so we'll have to allocate memory for this.
      final String valueStr = value.stringValue().trim();
      if (valueStr.length() == 0)
      {
        throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX,
                                ERR_INTEGER_ZERO_LENGTH_NOT_ALLOWED.get());
      }

      for (int i=0; i < valueStr.length(); i++)
      {
        switch (valueStr.charAt(i))
        {
          case '-':
            // This is only acceptable as the first character, and only if it is
            // followed by one or more other characters.
            if ((i != 0) || (valueStr.length() == 1))
            {
              throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX,
                                      ERR_INTEGER_INVALID_CHARACTER.get());
            }
            break;

          case '0':
            // This is acceptable anywhere except the as first character unless
            // it is the only character, or as the second character if the first
            // character is a dash.
            if (((i == 0) && (valueStr.length() > 1)) ||
                ((i == 1) && (valueStr.charAt(0) == '-')))
            {
              throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX,
                                      ERR_INTEGER_INVALID_LEADING_ZERO.get());
            }
            break;

          case '1':
          case '2':
          case '3':
          case '4':
          case '5':
          case '6':
          case '7':
          case '8':
          case '9':
            // These are always acceptable.
            break;

          default:
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX,
                                    ERR_INTEGER_INVALID_CHARACTER.get(i));
        }
      }

      return new ASN1OctetString(valueStr);
    }


    // Perform the validation against the contents of the byte array.
    for (int i=0; i < valueBytes.length; i++)
    {
      switch (valueBytes[i])
      {
        case '-':
          // This is only acceptable as the first character, and only if it is
          // followed by one or more other characters.
          if ((i != 0) || (valueBytes.length == 1))
          {
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX,
                                    ERR_INTEGER_INVALID_CHARACTER.get());
          }
          break;

        case '0':
          // This is acceptable anywhere except the as first character unless
          // it is the only character, or as the second character if the first
          // character is a dash.
          if (((i == 0) && (valueBytes.length > 1)) ||
              ((i == 1) && (valueBytes[0] == '-')))
          {
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX,
                                    ERR_INTEGER_INVALID_LEADING_ZERO.get());
          }
          break;

        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
          // These are always acceptable.
          break;

        default:
          throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX,
                                  ERR_INTEGER_INVALID_CHARACTER.get(i));
      }
    }

    return value;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public ASN1OctetString normalizeSubstring(final ASN1OctetString value,
                                            final byte substringType)
         throws LDAPException
  {
    throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING,
                            ERR_INTEGER_SUBSTRING_MATCHING_NOT_SUPPORTED.get());
  }
}
