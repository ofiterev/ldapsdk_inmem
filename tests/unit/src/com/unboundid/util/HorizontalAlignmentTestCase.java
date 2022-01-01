/*
 * Copyright 2009-2022 Ping Identity Corporation
 * All Rights Reserved.
 */
/*
 * Copyright 2009-2022 Ping Identity Corporation
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
 * Copyright (C) 2009-2022 Ping Identity Corporation
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
package com.unboundid.util;



import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;



/**
 * This class provides test coverage for the {@code HorizontalAlignment} enum.
 */
public class HorizontalAlignmentTestCase
       extends UtilTestCase
{
  /**
   * Provides test coverage for the automatically-generated values and valueOf
   * methods.
   */
  @Test()
  public void testAutoGeneratedMethods()
  {
    HorizontalAlignment[] values = HorizontalAlignment.values();

    assertNotNull(values);
    assertFalse(values.length == 0);

    for (HorizontalAlignment f : values)
    {
      assertNotNull(HorizontalAlignment.valueOf(f.name()));
      assertEquals(HorizontalAlignment.valueOf(f.name()), f);
    }
  }



  /**
   * Tests the {@code format} method.
   *
   * @param  text            The text to be formatted.
   * @param  width           The width to use.
   * @param  expectedLeft    The expected result after formatting for an
   *                         alignment of "LEFT".
   * @param  expectedCenter  The expected result after formatting for an
   *                         alignment of "CENTER".
   * @param  expectedRight   The expected result after formatting for an
   *                         alignment of "RIGHT".
   */
  @Test(dataProvider="testFormatData")
  public void testFormat(String text, int width, String expectedLeft,
                         String expectedCenter, String expectedRight)
  {
    StringBuilder leftBuffer   = new StringBuilder();
    StringBuilder centerBuffer = new StringBuilder();
    StringBuilder rightBuffer  = new StringBuilder();

    HorizontalAlignment.LEFT.format(leftBuffer, text, width);
    HorizontalAlignment.CENTER.format(centerBuffer, text, width);
    HorizontalAlignment.RIGHT.format(rightBuffer, text, width);

    assertEquals(leftBuffer.toString(), expectedLeft);
    assertEquals(centerBuffer.toString(), expectedCenter);
    assertEquals(rightBuffer.toString(), expectedRight);
  }



  /**
   * Provides test data for use when testing the {@code format} method.
   *
   * @return  Test data for use when testing the {@code format} method.
   */
  @DataProvider(name="testFormatData")
  public Object[][] getTestFormatData()
  {
    return new Object[][]
    {
      new Object[] { "", 0, "", "", "" },
      new Object[] { " ", 0, "", "", "" },
      new Object[] { "", 1, " ", " ", " " },
      new Object[] { "a", 1, "a", "a", "a" },
      new Object[] { "a", 2, "a ", "a ", " a" },
      new Object[] { "a", 3, "a  ", " a ", "  a" },
      new Object[] { "aa", 1, "a", "a", "a" },
      new Object[] { "aa", 2, "aa", "aa", "aa" },
      new Object[] { "aa", 3, "aa ", "aa ", " aa" },
      new Object[] { "aaa", 1, "a", "a", "a" },
      new Object[] { "aaa", 2, "aa", "aa", "aa" },
      new Object[] { "aaa", 3, "aaa", "aaa", "aaa" },
    };
  }



  /**
   * Tests the {@code forName} method with automated tests based on the actual
   * name of the enum values.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testForNameAutomated()
         throws Exception
  {
    for (final HorizontalAlignment value : HorizontalAlignment.values())
    {
      for (final String name : getNames(value.name()))
      {
        assertNotNull(HorizontalAlignment.forName(name));
        assertEquals(HorizontalAlignment.forName(name), value);
      }
    }

    assertNull(HorizontalAlignment.forName("some undefined name"));
  }



  /**
   * Retrieves a set of names for testing the {@code forName} method based on
   * the provided set of names.
   *
   * @param  baseNames  The base set of names to use to generate the full set of
   *                    names.  It must not be {@code null} or empty.
   *
   * @return  The full set of names to use for testing.
   */
  private static Set<String> getNames(final String... baseNames)
  {
    final HashSet<String> nameSet = new HashSet<>(10);
    for (final String name : baseNames)
    {
      nameSet.add(name);
      nameSet.add(name.toLowerCase());
      nameSet.add(name.toUpperCase());

      final String nameWithDashesInsteadOfUnderscores = name.replace('_', '-');
      nameSet.add(nameWithDashesInsteadOfUnderscores);
      nameSet.add(nameWithDashesInsteadOfUnderscores.toLowerCase());
      nameSet.add(nameWithDashesInsteadOfUnderscores.toUpperCase());

      final String nameWithUnderscoresInsteadOfDashes = name.replace('-', '_');
      nameSet.add(nameWithUnderscoresInsteadOfDashes);
      nameSet.add(nameWithUnderscoresInsteadOfDashes.toLowerCase());
      nameSet.add(nameWithUnderscoresInsteadOfDashes.toUpperCase());

      final StringBuilder nameWithoutUnderscoresOrDashes = new StringBuilder();
      for (final char c : name.toCharArray())
      {
        if ((c != '-') && (c != '_'))
        {
          nameWithoutUnderscoresOrDashes.append(c);
        }
      }
      nameSet.add(nameWithoutUnderscoresOrDashes.toString());
      nameSet.add(nameWithoutUnderscoresOrDashes.toString().toLowerCase());
      nameSet.add(nameWithoutUnderscoresOrDashes.toString().toUpperCase());
    }

    return nameSet;
  }
}
