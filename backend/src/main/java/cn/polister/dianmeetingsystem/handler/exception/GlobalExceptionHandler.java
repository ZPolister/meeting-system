package cn.polister.dianmeetingsystem.handler.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.polister.dianmeetingsystem.entity.ResponseResult;
import cn.polister.dianmeetingsystem.enums.AppHttpCodeEnum;
import cn.polister.dianmeetingsystem.exception.SystemException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SystemException.class)
    public ResponseResult systemExceptionHandler(SystemException e) {
        //打印异常信息
        e.printStackTrace();
        //从异常对象中获取提示信息封装返回
        return ResponseResult.errorResult(e.getCode(), e.getMsg());
    }

    // 处理参数校验异常（@RequestBody）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return ResponseResult.errorResult(400, ex.getMessage());
    }

    // 处理参数校验异常（@RequestParam）
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseResult handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseResult.errorResult(
                400,
                ex.getConstraintViolations().iterator().next().getMessage()
        );
    }

//    @ExceptionHandler(HandlerMethodValidationException.class)
//    public ResponseResult handleConstraintViolation(HandlerMethodValidationException ex) {
//        return ResponseResult.errorResult(
//                400,
//                ex.getAllValidationResults().stream().findFirst().get()
//                        .getResolvableErrors().stream().findFirst().get().getDefaultMessage()
//        );
//    }

    @ExceptionHandler(Exception.class)
    public ResponseResult exceptionHandler(Exception e) {
        //打印异常信息
        log.error("出现了异常！", e);
        // e.printStackTrace();
        //从异常对象中获取提示信息封装返回
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public ResponseResult handleNotLoginException(NotLoginException e, HttpServletResponse response) {
        log.error(e.toString(), e);
        response.setStatus(401);
        return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
    }
}