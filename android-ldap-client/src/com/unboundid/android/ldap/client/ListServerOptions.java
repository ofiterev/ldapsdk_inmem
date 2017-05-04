/*
 * Copyright 2009-2017 Ping Identity Corporation
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2009-2017 Ping Identity Corporation
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



import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static com.unboundid.android.ldap.client.Logger.*;
import static com.unboundid.util.StaticUtils.*;



/**
 * This class provides an Android activity that may be used to display a set
 * of options to perform on a server instance.
 */
public final class ListServerOptions
       extends Activity
       implements OnItemClickListener
{
  /**
   * The name of the field used to provide the server instance.
   */
  public static final String BUNDLE_FIELD_INSTANCE = "SERVER_OPTIONS_INSTANCE";



  /**
   * The tag that will be used for log messages generated by this class.
   */
  private static final String LOG_TAG = "ListServerOptions";



  // The instance that was selected.
  private volatile ServerInstance instance = null;



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


    // Get the instance on which to operate.
    final Intent intent = getIntent();
    final Bundle extras = intent.getExtras();

    instance = (ServerInstance) extras.getSerializable(BUNDLE_FIELD_INSTANCE);
    setTitle(getString(R.string.list_server_options_title, instance.getID()));


    // Populate the list of options.
    final String[] options =
    {
      getString(R.string.list_server_options_option_search),
      getString(R.string.list_server_options_option_edit),
      getString(R.string.list_server_options_option_remove)
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
        search();
        break;
      case 1:
        editServer();
        break;
      case 2:
        removeServer();
        break;
      default:
        break;
    }
    finish();
  }



  /**
   * Displays the form to search the selected server instance.
   */
  private void search()
  {
    logEnter(LOG_TAG, "search");

    final Intent i = new Intent(this, SearchServer.class);
    i.putExtra(SearchServer.BUNDLE_FIELD_INSTANCE, instance);
    startActivity(i);
  }



  /**
   * Displays the form to edit the specified server instance.
   */
  private void editServer()
  {
    logEnter(LOG_TAG, "editServer");

    final Intent i = new Intent(this, EditServer.class);
    i.putExtra(EditServer.BUNDLE_FIELD_INSTANCE, instance);
    startActivity(i);
  }



  /**
   * Deletes the selected server instance.
   */
  private void removeServer()
  {
    logEnter(LOG_TAG, "removeServer");

    final String id = instance.getID();

    try
    {
      final Map<String,ServerInstance> instances =
           ServerInstance.getInstances(this);
      final LinkedHashMap<String,ServerInstance> newInstances =
           new LinkedHashMap<String,ServerInstance>(instances);
      newInstances.remove(id);

      ServerInstance.saveInstances(this, newInstances);

      final Intent i = new Intent(this, PopUp.class);
      i.putExtra(PopUp.BUNDLE_FIELD_TITLE,
           getString(R.string.list_server_options_popup_title_removed));
      i.putExtra(PopUp.BUNDLE_FIELD_TEXT,
           getString(R.string.list_server_options_popup_text_removed, id));
      startActivity(i);
    }
    catch (final Exception e)
    {
      logException(LOG_TAG, "removeServer", e);

      final Intent i = new Intent(this, PopUp.class);
      i.putExtra(PopUp.BUNDLE_FIELD_TITLE,
           getString(R.string.list_server_options_popup_title_error));
      i.putExtra(PopUp.BUNDLE_FIELD_TEXT,
           getString(R.string.list_server_options_popup_text_error, id,
                getExceptionMessage(e)));
      startActivity(i);
    }
  }
}
