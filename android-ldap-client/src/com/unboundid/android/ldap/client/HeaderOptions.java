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
package com.unboundid.android.ldap.client;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.unboundid.ldap.sdk.Entry;

import static com.unboundid.android.ldap.client.Logger.*;



/**
 * This class provides an Android activity that may be used to display a set
 * of options to perform on an entry after a user has long-clicked on the entry
 * header.  It includes the ability to view the LDIF representation of the entry
 * and copy the DN to the clipboard.
 */
public final class HeaderOptions
       extends Activity
       implements OnItemClickListener
{
  /**
   * The name of the field used to provide the entry.
   */
  public static final String BUNDLE_FIELD_ENTRY = "ENTRY";



  /**
   * The tag that will be used for log messages generated by this class.
   */
  private static final String LOG_TAG = "HeaderOptions";



  // The attribute value that was selected.
  private volatile Entry entry = null;



  /**
   * Performs all necessary processing when this activity is started or resumed.
   */
  @Override()
  protected void onResume()
  {
    logEnter(LOG_TAG, "onResume");

    super.onResume();

    setContentView(R.layout.layout_popup_menu);
    setTitle(R.string.activity_label);


    // Get the phone number for this
    final Intent intent = getIntent();
    final Bundle extras = intent.getExtras();

    entry = (Entry) extras.getSerializable(BUNDLE_FIELD_ENTRY);
    setTitle(getString(R.string.header_options_title, entry.getDN()));


    // Populate the list of options.
    final String[] options =
    {
      getString(R.string.header_options_option_view_ldif),
      getString(R.string.header_options_option_copy_dn),
      getString(R.string.header_options_option_copy_ldif)
    };

    final ListView optionList =
         (ListView) findViewById(R.id.layout_popup_menu_list_view);
    final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
         android.R.layout.simple_list_item_1, options);
    optionList.setAdapter(listAdapter);
    optionList.setOnItemClickListener(this);
  }



  /**
   * Takes any appropriate action after a list item was clicked.
   *
   * @param  parent    The list containing the item that was clicked.
   * @param  item      The item that was clicked.
   * @param  position  The position of the item that was clicked.
   * @param  id        The ID of the item that was clicked.
   */
  public void onItemClick(final AdapterView<?> parent, final View item,
                          final int position, final long id)
  {
    logEnter(LOG_TAG, "onItemClick", parent, item, position, id);

    // Figure out which item was clicked and take the appropriate action.
    switch (position)
    {
      case 0:
        doViewLDIF();
        break;
      case 1:
        doCopyDN();
        break;
      case 2:
        doCopyLDIF();
        break;
      default:
        break;
    }
    finish();
  }



  /**
   * Displays an LDIF representation of the entry.
   */
  private void doViewLDIF()
  {
    logEnter(LOG_TAG, "doViewLDIF");

    final Intent i = new Intent(this, ViewLDIF.class);
    i.putExtra(ViewLDIF.BUNDLE_FIELD_ENTRY, entry);
    startActivity(i);
  }



  /**
   * Copies the entry DN to the clipboard.
   */
  private void doCopyDN()
  {
    logEnter(LOG_TAG, "doCopyDN");

    final ClipboardManager clipboard =
         (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    clipboard.setText(entry.getDN());
  }



  /**
   * Copies the LDIF representation of the entry to the clipboard.
   */
  private void doCopyLDIF()
  {
    logEnter(LOG_TAG, "doCopyLDIF");

    final ClipboardManager clipboard =
         (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    clipboard.setText(entry.toLDIFString());
  }
}
