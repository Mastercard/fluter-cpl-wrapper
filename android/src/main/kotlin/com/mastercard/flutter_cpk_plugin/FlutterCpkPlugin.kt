package com.mastercard.flutter_cpk_plugin

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry


/**
 * Suggestion
 * Secondary activity
 * Start the activity from the plugin
 * Listen for a result within the plugin file
*/

/** FlutterCpkPlugin */
class FlutterCpkPlugin: FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel: MethodChannel
  private lateinit var context: Context
  private lateinit var activity: Activity
  private var isCpkConnected = false

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_cpk_plugin")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    var intent = Intent(context, CpkActivity::class.java)
    activity.startActivityForResult(intent, 100)

    // Work on sending the application GUID to the CPK

    when (call.method) {
      "getPlatformVersion" -> result.success(getPlatformVersion())
      "getCpkConnectionStatus" -> result.success(
        getCpkConnectionStatus(
          call.argument<String>("appGuid")!!,
        )
    )
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivity() {
    TODO("Not yet implemented")
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    isCpkConnected = if(resultCode == Activity.RESULT_OK){
      var success: Boolean? = data?.getBooleanExtra("success", true)
      success ?: true
    } else {
      var errorCode: Int? = data?.getIntExtra("errorCode", 0)
      var errorMessage: String? = data?.getStringExtra("errorMessage")
      var success: Boolean? = data?.getBooleanExtra("success", false)
      success ?: false
    }

    return isCpkConnected;
  }
}