package uno.moni.onex.core.pojo.vo

import uno.moni.onex.core.enums.ResponseCodeEnums
import java.io.Serializable

class Response<T> (
    var code: Int,
    var msg: String,
    var data: T?
): Serializable {
    fun <T> data(data: T): Response<T> {
        return Response(this.code, this.msg, data)
    }
    companion object {
        private val SUCCESS_CODE = ResponseCodeEnums.SUCCESS_NO_CONTENT
        private const val SUCCESS_MESSAGE = "success"

        private val FAIL_CODE = ResponseCodeEnums.FAILED
        private const val FAIL_MESSAGE = "failed"

        fun success(): Response<Unit> {
            return Response(SUCCESS_CODE.code, SUCCESS_MESSAGE, null)
        }

        fun success(code: Int): Response<Unit> {
            return Response(code, SUCCESS_MESSAGE, null)
        }
        fun success(message: String): Response<Unit> {
            return Response(SUCCESS_CODE.code, message, null)
        }
        fun success(code: Int, message: String): Response<Unit> {
            return Response(code, message, null)
        }
        fun <T> success(code: Int, message: String, data: T): Response<T> {
            return Response(code, message, data)
        }


        fun fail(): Response<Unit> {
            return Response(FAIL_CODE.code, FAIL_MESSAGE, null)
        }
        fun fail(code: Int): Response<Unit> {
            return Response(code, FAIL_MESSAGE, null)
        }
        fun fail(message: String): Response<Unit> {
            return Response(FAIL_CODE.code, message, null)
        }
        fun fail(code: Int, message: String): Response<Unit> {
            return Response(code, message, null)
        }
        fun <T> fail(code: Int, message: String, data: T): Response<T> {
            return Response(code, message, data)
        }
    }
}