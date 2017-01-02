package org.apache.cordova.firebase;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigInfo;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;
import com.google.firebase.FirebaseApp;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class FirebaseDatabasePlugin extends CordovaPlugin {
    private final String TAG = "FirebaseDatabasePlugin";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static WeakReference<CallbackContext> callbackContext;

    @Override
    protected void pluginInitialize() {
        final Context context = this.cordova.getActivity().getApplicationContext();
        this.cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                if (!FirebaseApp.getApps(context).isEmpty()) {
                    Log.d(TAG, "Starting Firebase plugin");
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mAuth = FirebaseAuth.getInstance();
                }
            }
        });
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("updateChildren")) {
            this.updateChildren(callbackContext, args.getString(0), args.getJSONObject(1));
            return true;
        } else if (action.equals("setValueBoolean")) {
            this.setValue(callbackContext, args.getString(0), args.getBoolean(1));
            return true;
        } else if (action.equals("setValueNumber")) {
            this.setValue(callbackContext, args.getString(0), args.getDouble(1));
            return true;
        } else if (action.equals("setValueString")) {
            this.setValue(callbackContext, args.getString(0), args.getString(1));
            return true;
        } else if (action.equals("setValue")) {
            this.setValue(callbackContext, args.getString(0), args.getJSONObject(1));
            return true;
        } else if (action.equals("setDatabasePersistent")) {
            this.setPersistenceEnabled(callbackContext, args.getBoolean(0));
            return true;
        } else if (action.equals("signInWithEmailAndPassword")) {
            this.signInWithEmailAndPassword(callbackContext, args.getString(0), args.getString(1));
            return true;
        }
        return false;
    }

    private void updateChildren(final CallbackContext callbackContext, final String path, final JSONObject updates) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
              try {
                if (path != null) {
                  mDatabase.child(path).updateChildren(jsonObjectToMap(updates), getCompletionListener(callbackContext));
                } else {
                  mDatabase.updateChildren(jsonObjectToMap(updates), getCompletionListener(callbackContext));
                }
              } catch (JSONException e) {
                  callbackContext.error(e.getMessage());
              }
            }
        });
    }

    private void setValue(final CallbackContext callbackContext, final String path, final JSONObject updates) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
              try {
                mDatabase.child(path).setValue(jsonObjectToMap(updates), getCompletionListener(callbackContext));
              } catch (JSONException e) {
                  callbackContext.error(e.getMessage());
              }
            }
        });
    }

    private void setValue(final CallbackContext callbackContext, final String path, final String updates) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                mDatabase.child(path).setValue(updates, getCompletionListener(callbackContext));
            }
        });
    }

    private void setValue(final CallbackContext callbackContext, final String path, final double updates) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                mDatabase.child(path).setValue(updates, getCompletionListener(callbackContext));
            }
        });
    }

    private void setValue(final CallbackContext callbackContext, final String path, final boolean updates) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
              mDatabase.child(path).setValue(updates, getCompletionListener(callbackContext));
            }
        });
    }

    private void setPersistenceEnabled(final CallbackContext callbackContext, final boolean persistent) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                FirebaseDatabase.getInstance().setPersistenceEnabled(persistent);
            }
        });
    }

    private void signInWithEmailAndPassword(final CallbackContext callbackContext, final String email, final String password) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task == null || !task.isSuccessful()) {
                                callbackContext.error(task.getException().toString());
                            } else {
                                callbackContext.success("");
                            }
                        }
                    }
                );
            }
        });
    }

    private DatabaseReference.CompletionListener getCompletionListener(final CallbackContext callbackContext) {
        return new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    callbackContext.success("");
                } else {
                    callbackContext.error(databaseError.getMessage());
                }
            }
        };
    }

    private static Map<String, Object> jsonObjectToMap(JSONObject json) throws JSONException {
      Gson gson = new Gson();
      Map<String,Object> map = new HashMap<String,Object>();
      map = (Map<String,Object>) gson.fromJson(json.toString(), map.getClass());
      return map;
    }
}
