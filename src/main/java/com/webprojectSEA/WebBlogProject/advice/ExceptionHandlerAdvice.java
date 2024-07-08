package com.webprojectSEA.WebBlogProject.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

        @ExceptionHandler(Exception.class)
        public String handleException(Exception ex, Model model) {
            model.addAttribute("errorMessage", "Beklenmeyen bir hata oluştu: " + ex.getMessage());
            return "error";
        }

        @ExceptionHandler(RuntimeException.class)
        public String handleRuntimeException(RuntimeException ex, Model model) {
            model.addAttribute("errorMessage", "Bir çalışma zamanı hatası oluştu: " + ex.getMessage());
            return "error";
        }


    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String exceptionAccess(final Exception ex, final Model model) {

        logger.error("Kullanıcı yetkisi olmayan veya var olmayan bir adrese erişim yapmaya çalıştı.", ex);

        String erroMessage = "Yetkisiz erişim" + ex.getMessage();
        model.addAttribute("errorMessage", erroMessage);
        return "/error"; // yönlenecek sayfa adı
    }

}
