/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ffly.demo;

import xu.ye.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.pim.vcard.ContactStruct;
import android.pim.vcard.EntryHandler;
import android.pim.vcard.VCardConfig;
import android.util.Log;

public class ProgressShower implements EntryHandler {
    public static final String LOG_TAG = "vcard.ProgressShower"; 

    private final Context mContext;
    private final Handler mHandler;
    private final ProgressDialog mProgressDialog;
    private final String mProgressMessage;

    private long mTime;
    
    private class ShowProgressRunnable implements Runnable {
        private ContactStruct mContact;
        
        public ShowProgressRunnable(ContactStruct contact) {
            mContact = contact;
        }
        
        public void run() {
            mProgressDialog.setMessage( mProgressMessage + "\n" + 
                    mContact.getDisplayName());
            mProgressDialog.incrementProgressBy(1);
        }
    }
    
    public ProgressShower(ProgressDialog progressDialog,
            String progressMessage,
            Context context,
            Handler handler) {
        mContext = context;
        mHandler = handler;
        mProgressDialog = progressDialog;
        mProgressMessage = progressMessage;
    }

    public void onParsingStart() {
    }

    public void onEntryCreated(ContactStruct contactStruct) {
        long start = System.currentTimeMillis();
        
        if (!contactStruct.isIgnorable()) {
            if (mProgressDialog != null && mProgressMessage != null) {
                if (mHandler != null) {
                    mHandler.post(new ShowProgressRunnable(contactStruct));
                } else {
                    mProgressDialog.setMessage(mContext.getString(R.string.progress_shower_message,
                            mProgressMessage, 
                            contactStruct.getDisplayName()));
                }
            }
        }
        
        mTime += System.currentTimeMillis() - start;
    }

    public void onParsingEnd() {
        if (VCardConfig.showPerformanceLog()) {
            Log.d(LOG_TAG,
                    String.format("Time to progress a dialog: %d ms", mTime));
        }
    }
}
