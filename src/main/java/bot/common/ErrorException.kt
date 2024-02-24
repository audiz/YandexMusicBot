package bot.common

class ErrorException(val errorBuilder: ErrorBuilder) : Exception() {
    override fun toString(): String {
        return "EiimErrorException{" +
                "errorBuilder=" + errorBuilder +
                '}'
    }
}