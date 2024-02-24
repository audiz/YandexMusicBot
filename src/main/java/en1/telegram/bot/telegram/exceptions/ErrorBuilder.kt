package en1.telegram.bot.telegram.exceptions

import java.io.Serializable

class ErrorBuilder private constructor() : Serializable {
    var errorKind: ErrorKind? = null
    var exception: Exception? = null
    var code: Int? = null
    var respText: String? = null
    var description: String? = null
    var because: String? = null
    var solution: String? = null

    fun withException(exception: Exception?): ErrorBuilder {
        this.exception = exception
        return this
    }

    /**
     * Http or whatever
     */
    fun withCode(code: Int): ErrorBuilder {
        this.code = code
        return this
    }

    /**
     * Http or whatever
     */
    fun withCodeResp(code: Int, text: String?): ErrorBuilder {
        this.code = code
        respText = text
        return this
    }

    fun because(because: String?): ErrorBuilder {
        this.because = because
        return this
    }

    fun solution(solution: String?): ErrorBuilder {
        this.solution = solution
        return this
    }

    fun withDescription(description: String?): ErrorBuilder {
        this.description = description
        return this
    }

    /**
     * SYSTEM-COMPONENT-CODE: Msg
     */
    val simpleErrorMsg: String
        get() {
            val stringBuilder = StringBuilder()
            stringBuilder.append(errorKind!!.description)
            if (code != null) {
                stringBuilder.append("-").append(code)
            }
            if (description != null) {
                stringBuilder.append(": ").append(description)
            }
            return stringBuilder.toString()
        }

    /**
     * Return error code or provided code
     */
    fun getCodeOr(or: Int): Int {
        return if (code != null) code!! else or
    }

    override fun toString(): String {
        return "ErrorBuilder{" +
                "errorKind=" + errorKind +
                ", httpCode=" + code +
                ", description='" + description + '\'' +
                ", because='" + because + '\'' +
                ", solution='" + solution + '\'' +
                '}'
    }

    companion object {
        private const val serialVersionUID = 1L
        fun newBuilder(errorKind: ErrorKind?): ErrorBuilder {
            val errorBuilder = ErrorBuilder()
            errorBuilder.errorKind = errorKind
            return errorBuilder
        }
    }
}