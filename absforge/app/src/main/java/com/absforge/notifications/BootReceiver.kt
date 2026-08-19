package com.absforge.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.absforge.AbsForgeApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val app = context.applicationContext as? AbsForgeApplication ?: return
            
            CoroutineScope(Dispatchers.IO).launch {
                // Restore reminder logic
                ReminderScheduler.scheduleReminder(context, 8, 0) // Default 8 AM
            }
        }
    }
}
