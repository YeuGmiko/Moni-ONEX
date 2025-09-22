package uno.moni.onex.core.enums

enum class ResponseCodeEnums(
    val code: Int,
    val msg: String
) {
    /* ACCESS */
    USER_LOGOUT(2100, "退出登录成功"),
    TOKEN_EXPIRE_TIME(4500, "用户凭证已过期，请重新登录"),
    ACCOUNT_FORBID(4600, "该账户当前不可用"),
    /* REQUEST */
    SUCCESS_NO_CONTENT(2000, "请求成功"),
    SUCCESS(2200, "请求成功")
}