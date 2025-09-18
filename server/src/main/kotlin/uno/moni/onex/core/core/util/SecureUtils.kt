package uno.moni.onex.core.core.util

import cn.hutool.core.util.IdUtil
import cn.hutool.core.util.RandomUtil

class SecureUtils {
    companion object {
        fun generateId(): String {
            return IdUtil.getSnowflake().nextIdStr()
        }

        fun generateSalt(length: Int): String {
            return RandomUtil.randomString(length)
        }

        fun replaceAll(target: String, replaceText: String, content: String): String {
            return ""
        }
    }
}