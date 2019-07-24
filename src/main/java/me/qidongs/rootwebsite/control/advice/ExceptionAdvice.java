package me.qidongs.rootwebsite.control.advice;

import me.qidongs.rootwebsite.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void ExceptionHandle(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("Server Error!"+e.getMessage());
        for (StackTraceElement element:e.getStackTrace()){
            logger.error(element.toString());
        }

        // check asynchronous requests
        String xrequestedwith= request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xrequestedwith)){
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"server error"));

        }
        else{
            response.sendRedirect(request.getContextPath()+"/error");
        }
    }
}
