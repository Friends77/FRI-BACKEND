package com.friends.board

import com.friends.common.exception.ErrorCode

abstract class BoardException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class BoardNotFoundException : BoardException(ErrorCode.NOT_FOUND_BOARD)

class InvalidBoardAccessException : BoardException(ErrorCode.INVALID_BOARD_ACCESS)
