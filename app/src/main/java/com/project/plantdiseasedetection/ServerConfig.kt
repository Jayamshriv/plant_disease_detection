package com.project.plantdiseasedetection



object ServerConfig {
    private const val PREFS_NAME = "server_config"
    private const val KEY_SERVER_IP = "server_ip"
    private const val KEY_SERVER_PORT = "server_port"
    private const val DEFAULT_IP = "192.168.0.106"
    private const val DEFAULT_PORT = "5000"

    fun getServerUrl(context: android.content.Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        val ip = prefs.getString(KEY_SERVER_IP, DEFAULT_IP) ?: DEFAULT_IP
        val port = prefs.getString(KEY_SERVER_PORT, DEFAULT_PORT) ?: DEFAULT_PORT
        return "http://$ip:$port/"
    }

    fun saveServerConfig(context: android.content.Context, ip: String, port: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_SERVER_IP, ip)
            .putString(KEY_SERVER_PORT, port)
            .apply()
    }

    fun getServerIp(context: android.content.Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        return prefs.getString(KEY_SERVER_IP, DEFAULT_IP) ?: DEFAULT_IP
    }

    fun getServerPort(context: android.content.Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        return prefs.getString(KEY_SERVER_PORT, DEFAULT_PORT) ?: DEFAULT_PORT
    }
}

