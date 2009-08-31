/*
 * Copyright 2009 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2009 UnboundID Corp.
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
package com.unboundid.ldap.sdk.migrate.jndi;



import java.util.Collection;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.BasicControl;
import javax.naming.ldap.ExtendedResponse;

import com.unboundid.asn1.ASN1Exception;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.util.NotMutable;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.ThreadSafetyLevel;

import static com.unboundid.util.StaticUtils.*;



/**
 * This utility class provides a set of methods that may be used to convert
 * between data structures in the Java Naming and Directory Interface (JNDI)
 * and the corresponding data structures in the UnboundID LDAP SDK for Java.
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JNDIConverter
{
  /**
   * Prevent this utility class from being instantiated.
   */
  private JNDIConverter()
  {
    // No implementation required.
  }



  /**
   * Converts the provided JNDI attribute to an LDAP SDK attribute.
   *
   * @param  a  The attribute to be converted.
   *
   * @return  The LDAP SDK attribute that corresponds to the provided JNDI
   *          attribute.
   *
   * @throws  NamingException  If a problem is encountered during the conversion
   *                           process.
   */
  public static Attribute convertAttribute(
                               final javax.naming.directory.Attribute a)
         throws NamingException
  {
    final String name = a.getID();
    final ASN1OctetString[] values = new ASN1OctetString[a.size()];

    for (int i=0; i < values.length; i++)
    {
      final Object value = a.get(i);
      if (value instanceof byte[])
      {
        values[i] = new ASN1OctetString((byte[]) value);
      }
      else
      {
        values[i] = new ASN1OctetString(String.valueOf(value));
      }
    }

    return new Attribute(name, values);
  }



  /**
   * Converts the provided LDAP SDK attribute to a JNDI attribute.
   *
   * @param  a  The attribute to be converted.
   *
   * @return  The JNDI attribute that corresponds to the provided LDAP SDK
   *          attribute.
   */
  public static BasicAttribute convertAttribute(final Attribute a)
  {
    final BasicAttribute attr = new BasicAttribute(a.getName());
    for (final ASN1OctetString v : a.getRawValues())
    {
      attr.add(v.getValue());
    }

    return attr;
  }



  /**
   * Converts the provided JNDI attributes to an array of LDAP SDK attributes.
   *
   * @param  a  The attributes to be converted.
   *
   * @return  The array of LDAP SDK attributes that corresponds to the
   *          provided JNDI attributes.
   *
   * @throws  NamingException  If a problem is encountered during the conversion
   *                           process.
   */
  public static Attribute[] convertAttributes(final Attributes a)
         throws NamingException
  {
    int i=0;
    final Attribute[] attributes = new Attribute[a.size()];
    final NamingEnumeration<? extends javax.naming.directory.Attribute> e =
         a.getAll();

    try
    {
      while (e.hasMoreElements())
      {
        attributes[i++] = convertAttribute(e.next());
      }
    }
    finally
    {
      e.close();
    }

    return attributes;
  }



  /**
   * Converts the provided array of LDAP SDK attributes to a set of JNDI
   * attributes.
   *
   * @param  a  The array of attributes to be converted.
   *
   * @return  The JNDI attributes that corresponds to the provided LDAP SDK
   *          attributes.
   */
  public static BasicAttributes convertAttributes(final Attribute... a)
  {
    final BasicAttributes attrs = new BasicAttributes();
    for (final Attribute attr : a)
    {
      attrs.put(convertAttribute(attr));
    }

    return attrs;
  }



  /**
   * Converts the provided collection of LDAP SDK attributes to a set of JNDI
   * attributes.
   *
   * @param  a  The collection of attributes to be converted.
   *
   * @return  The JNDI attributes that corresponds to the provided LDAP SDK
   *          attributes.
   */
  public static BasicAttributes convertAttributes(final Collection<Attribute> a)
  {
    final BasicAttributes attrs = new BasicAttributes();
    for (final Attribute attr : a)
    {
      attrs.put(convertAttribute(attr));
    }

    return attrs;
  }



  /**
   * Converts the provided JNDI control to an LDAP SDK control.
   *
   * @param  c  The control to be converted.
   *
   * @return  The LDAP SDK control that corresponds to the provided JNDI
   *          control.
   *
   * @throws  NamingException  If a problem is encountered during the conversion
   *                           process.
   */
  public static Control convertControl(final javax.naming.ldap.Control c)
         throws NamingException
  {
    final ASN1OctetString value;
    final byte[] valueBytes = c.getEncodedValue();
    if ((valueBytes == null) || (valueBytes.length == 0))
    {
      value = null;
    }
    else
    {
      try
      {
        value = ASN1OctetString.decodeAsOctetString(valueBytes);
      }
      catch (ASN1Exception ae)
      {
        throw new NamingException(getExceptionMessage(ae));
      }
    }

    return new Control(c.getID(), c.isCritical(), value);
  }



  /**
   * Converts the provided LDAP SDK control to a JNDI control.
   *
   * @param  c  The control to be converted.
   *
   * @return  The JNDI control that corresponds to the provided LDAP SDK
   *          control.
   */
  public static BasicControl convertControl(final Control c)
  {
    final ASN1OctetString value = c.getValue();
    if (value == null)
    {
      return new BasicControl(c.getOID(), c.isCritical(), null);
    }
    else
    {
      return new BasicControl(c.getOID(), c.isCritical(), value.encode());
    }
  }



  /**
   * Converts the provided array of JNDI controls to an array of LDAP SDK
   * controls.
   *
   * @param  c  The array of JNDI controls to be converted.
   *
   * @return  The array of LDAP SDK controls that corresponds to the provided
   *          array of JNDI controls.
   *
   * @throws  NamingException  If a problem is encountered during the conversion
   *                           process.
   */
  public static Control[] convertControls(final javax.naming.ldap.Control... c)
         throws NamingException
  {
    final Control[] controls = new Control[c.length];
    for (int i=0; i < controls.length; i++)
    {
      controls[i] = convertControl(c[i]);
    }

    return controls;
  }



  /**
   * Converts the provided array of LDAP SDK controls to an array of JNDI
   * controls.
   *
   * @param  c  The array of LDAP SDK controls to be converted.
   *
   * @return  The array of JNDI controls that corresponds to the provided array
   *          of LDAP SDK controls.
   */
  public static javax.naming.ldap.Control[] convertControls(final Control... c)
  {
    final javax.naming.ldap.Control[] controls =
         new javax.naming.ldap.Control[c.length];
    for (int i=0; i < controls.length; i++)
    {
      controls[i] = convertControl(c[i]);
    }

    return controls;
  }



  /**
   * Converts the provided JNDI extended request to an LDAP SDK extended
   * request.
   *
   * @param  r  The request to be converted.
   *
   * @return  The LDAP SDK extended request that corresponds to the provided
   *          JNDI extended request.
   *
   * @throws  NamingException  If a problem is encountered during the conversion
   *                           process.
   */
  public static ExtendedRequest convertExtendedRequest(
                                     final javax.naming.ldap.ExtendedRequest r)
         throws NamingException
  {
    return JNDIExtendedRequest.toSDKExtendedRequest(r);
  }



  /**
   * Converts the provided LDAP SDK extended request to a JNDI extended request.
   *
   * @param  r  The request to be converted.
   *
   * @return  The JNDI extended request that corresponds to the provided LDAP
   *          SDK extended request.
   */
  public static JNDIExtendedRequest convertExtendedRequest(
                                         final ExtendedRequest r)
  {
    return new JNDIExtendedRequest(r);
  }



  /**
   * Converts the provided JNDI extended response to an LDAP SDK extended
   * result.
   *
   * @param  r  The response to be converted.
   *
   * @return  The LDAP SDK extended result that corresponds to the provided
   *          JNDI extended response.
   *
   * @throws  NamingException  If a problem is encountered during the conversion
   *                           process.
   */
  public static ExtendedResult convertExtendedResponse(final ExtendedResponse r)
         throws NamingException
  {
    return JNDIExtendedResponse.toSDKExtendedResult(r);
  }



  /**
   * Converts the provided LDAP SDK extended result to a JNDI extended response.
   *
   * @param  r  The result to be converted.
   *
   * @return  The JNDI extended response that corresponds to the provided LDAP
   *          SDK extended result.
   */
  public static JNDIExtendedResponse convertExtendedResult(
                                          final ExtendedResult r)
  {
    return new JNDIExtendedResponse(r);
  }



  /**
   * Converts the provided JNDI modification item to an LDAP SDK modification.
   *
   * @param  m  The JNDI modification item to be converted.
   *
   * @return  The LDAP SDK modification that corresponds to the provided JNDI
   *          modification item.
   *
   * @throws  NamingException  If a problem is encountered during the conversion
   *                           process.
   */
  public static Modification convertModification(final ModificationItem m)
         throws NamingException
  {
    final ModificationType modType;
    switch (m.getModificationOp())
    {
      case DirContext.ADD_ATTRIBUTE:
        modType = ModificationType.ADD;
        break;
      case DirContext.REMOVE_ATTRIBUTE:
        modType = ModificationType.DELETE;
        break;
      case DirContext.REPLACE_ATTRIBUTE:
        modType = ModificationType.REPLACE;
        break;
      default:
        throw new NamingException("Unsupported modification type " + m);
    }

    final Attribute a = convertAttribute(m.getAttribute());

    return new Modification(modType, a.getName(), a.getRawValues());
  }



  /**
   * Converts the provided LDAP SDK modification to a JNDI modification item.
   *
   * @param  m  The LDAP SDK modification to be converted.
   *
   * @return  The JNDI modification item that corresponds to the provided LDAP
   *          SDK modification.
   *
   * @throws  NamingException  If a problem is encountered during the conversion
   *                           process.
   */
  public static ModificationItem convertModification(final Modification m)
         throws NamingException
  {
    final int modType;
    switch (m.getModificationType().intValue())
    {
      case ModificationType.ADD_INT_VALUE:
        modType = DirContext.ADD_ATTRIBUTE;
        break;
      case ModificationType.DELETE_INT_VALUE:
        modType = DirContext.REMOVE_ATTRIBUTE;
        break;
      case ModificationType.REPLACE_INT_VALUE:
        modType = DirContext.REPLACE_ATTRIBUTE;
        break;
      default:
        throw new NamingException("Unsupported modification type " + m);
    }

    return new ModificationItem(modType, convertAttribute(m.getAttribute()));
  }



  /**
   * Converts the provided array of JNDI modification items to an array of LDAP
   * SDK modifications.
   *
   * @param  m  The array of JNDI modification items to be converted.
   *
   * @return  The array of LDAP SDK modifications that corresponds to the
   *          provided array of JNDI modification items.
   *
   * @throws  NamingException  If a problem is encountered during the conversion
   *                           process.
   */
  public static Modification[] convertModifications(final ModificationItem... m)
         throws NamingException
  {
    final Modification[] mods = new Modification[m.length];
    for (int i=0; i < m.length; i++)
    {
      mods[i] = convertModification(m[i]);
    }

    return mods;
  }



  /**
   * Converts the provided array of LDAP SDK modifications to an array of JNDI
   * modification items.
   *
   * @param  m  The array of LDAP SDK modifications to be converted.
   *
   * @return  The array of JNDI modification items that corresponds to the
   *          provided array of LDAP SDK modifications.
   *
   * @throws  NamingException  If a problem is encountered during the conversion
   *                           process.
   */
  public static ModificationItem[] convertModifications(final Modification... m)
         throws NamingException
  {
    final ModificationItem[] mods = new ModificationItem[m.length];
    for (int i=0; i < m.length; i++)
    {
      mods[i] = convertModification(m[i]);
    }

    return mods;
  }



  /**
   * Converts the provided JNDI search result object to an LDAP SDK entry.
   *
   * @param  r  The JNDI search result object to be converted.
   *
   * @return  The LDAP SDK entry that corresponds to the provided JNDI search
   *          result.
   *
   * @throws  NamingException  If a problem is encountered during the conversion
   *                           process.
   */
  public static Entry convertSearchEntry(final SearchResult r)
         throws NamingException
  {
    return new Entry(r.getName(), convertAttributes(r.getAttributes()));
  }



  /**
   * Converts the provided LDAP SDK entry to a JNDI search result.
   *
   * @param  e  The entry to be converted to a JNDI search result.
   *
   * @return  The JNDI search result that corresponds to the provided LDAP SDK
   *          entry.
   */
  public static SearchResult convertSearchEntry(final Entry e)
  {
    final Collection<Attribute> attrs = e.getAttributes();
    final Attribute[] attributes = new Attribute[attrs.size()];
    attrs.toArray(attributes);

    return new SearchResult(e.getDN(), null, convertAttributes(attributes));
  }
}