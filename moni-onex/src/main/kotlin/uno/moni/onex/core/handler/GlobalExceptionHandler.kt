package uno.moni.onex.core.handler

import cn.dev33.satoken.exception.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import uno.moni.onex.core.pojo.vo.Response


@RestControllerAdvice
class GlobalExceptionHandler {

    // 拦截：未登录异常
    @ExceptionHandler(NotLoginException::class)
    fun handlerException(e: NotLoginException): Response<Unit> {
        // 打印堆栈，以供调试
        e.printStackTrace()

        // 返回给前端
        return e.message?.let { Response.fail(it) } ?: Response.fail()
    }

    // 拦截：缺少权限异常
    @ExceptionHandler(NotPermissionException::class)
    fun handlerException(e: NotPermissionException): Response<Unit> {
        e.printStackTrace()
        return Response.fail("缺少权限：${e.permission}")
    }

    // 拦截：缺少角色异常
    @ExceptionHandler(NotRoleException::class)
    fun handlerException(e: NotRoleException): Response<Unit> {
        e.printStackTrace()
        return Response.fail("缺少角色：${e.role}")
    }

    // 拦截：二级认证校验失败异常
    @ExceptionHandler(NotSafeException::class)
    fun handlerException(e: NotSafeException): Response<Unit> {
        e.printStackTrace()
        return Response.fail("二级认证校验失败：${e.service}")
    }

    // 拦截：服务封禁异常
    @ExceptionHandler(DisableServiceException::class)
    fun handlerException(e: DisableServiceException): Response<Unit> {
        e.printStackTrace()
        return Response.fail("当前账号 ${e.service} 服务已被封禁 (level=${e.level})：${e.disableTime}秒后解封")
    }

    // 拦截：Http Basic 校验失败异常
    @ExceptionHandler(NotHttpBasicAuthException::class)
    fun handlerException(e: NotHttpBasicAuthException): Response<Unit> {
        e.printStackTrace()
        return e.message?.let { Response.fail(it) } ?: Response.fail()
    }

    // 拦截：其它所有异常
    @ExceptionHandler(Exception::class)
    fun handlerException(e: Exception): Response<Unit> {
        e.printStackTrace()
        return Response.fail(e.message ?: "服务器错误")
    }
}