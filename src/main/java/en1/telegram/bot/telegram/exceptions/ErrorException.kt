package en1.telegram.bot.telegram.exceptions

class ErrorException(val errorBuilder: ErrorBuilder) : Exception() {
    override fun toString(): String {
        return "EiimErrorException{" +
                "errorBuilder=" + errorBuilder +
                '}'
    }
}