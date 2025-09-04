package uno.moni.onex.core.pojo.vo

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
        private const val SUCCESS_CODE: Int = 2000
        private const val SUCCESS_MESSAGE = "success"

        private const val FAIL_CODE: Int = 4500
        private const val FAIL_MESSAGE = "failed"

        fun success(): Response<Unit> {
            return Response(SUCCESS_CODE, SUCCESS_MESSAGE, null)
        }

        fun success(code: Int): Response<Unit> {
            return Response(code, SUCCESS_MESSAGE, null)
        }
        fun success(message: String): Response<Unit> {
            return Response(SUCCESS_CODE, message, null)
        }
        fun success(code: Int, message: String): Response<Unit> {
            return Response(code, message, null)
        }
        fun <T> success(code: Int, message: String, data: T): Response<T> {
            return Response(code, message, data)
        }


        fun fail(): Response<Unit> {
            return Response(FAIL_CODE, FAIL_MESSAGE, null)
        }
        fun fail(code: Int): Response<Unit> {
            return Response(code, FAIL_MESSAGE, null)
        }
        fun fail(message: String): Response<Unit> {
            return Response(FAIL_CODE, message, null)
        }
        fun fail(code: Int, message: String): Response<Unit> {
            return Response(code, message, null)
        }
        fun <T> fail(code: Int, message: String, data: T): Response<T> {
            return Response(code, message, data)
        }
    }
}