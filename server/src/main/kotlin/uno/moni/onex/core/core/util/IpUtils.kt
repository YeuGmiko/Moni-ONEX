package uno.moni.onex.core.core.util

import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Enumeration

class IpUtils {
    companion object {
        private const val IP_V4 = "127.0.0.1"
        private const val IP_V6 = "0:0:0:0:0:0:0:1"
        private const val UNKNOWN = "unknown"

        /**
         * 用于IP定位转换
         */
        private const val REGION: String = "内网IP|内网IP"

        fun getHostIp(): String {
            try {
                val allNetInterfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
                while(allNetInterfaces.hasMoreElements()) {
                    val netInterface = allNetInterfaces.nextElement()
                    val addresses = netInterface.inetAddresses
                    while(addresses.hasMoreElements()) {
                        val ip = addresses.nextElement()
                        if (ip !is Inet4Address && !ip.isLoopbackAddress && !ip.hostAddress.contains(":"))
                            return ip.hostAddress
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return IP_V4
        }
    }
}