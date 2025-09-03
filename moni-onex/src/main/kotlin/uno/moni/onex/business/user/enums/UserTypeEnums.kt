package uno.moni.onex.business.user.enums

enum class UserTypeEnums(
    val type: Int,
    val roleName: String,
) {
    SUPER_ADMIN(0, "SUPER_ADMIN"),
    ADMIN_USER(1, "ADMIN_USER"),
    COMMON_USER(2, "COMMON_USER"),
    ;

    companion object {
        fun getRoleName(roleType: Int): String {
            for (enums in entries) {
                if (enums.type == roleType) return enums.roleName
            }
            return ""
        }
    }
}