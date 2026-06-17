package com.erp_be.config;

import com.erp_be.error.ProductExCommon;
import com.erp_be.error.ProductException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Log4j2
public class HandleExceptionResponse
        extends ResponseEntityExceptionHandler
{
    @ExceptionHandler({ProductException.class})
    public ResponseEntity<Object> exception(ProductException exception)
    {
        exception.printStackTrace();
        return new ResponseEntity(exception.getProductExCommon(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> exception(Exception exception)
    {
//        return new ResponseEntity(new ResponseDto(0, exception.getMessage()), HttpStatus.BAD_REQUEST);
        exception.printStackTrace();
        return new ResponseEntity(new ProductExCommon(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
