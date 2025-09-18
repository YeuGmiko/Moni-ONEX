package uno.moni.onex.core.enums

enum class ResponseCodeEnums(
    val code: Int,
    val msg: String
) {
    TOKEN_EXPIRE_TIME(4400, "用户凭证已过期，请重新登录"),
    USER_LOGOUT(2100, "退出登录成功")
}