package codesquad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);
    private static final String LOGIN_FAILED = "redirect:/user?login_failed";
    private static final String HOME = "redirect:/";

    @ExceptionHandler({UnAuthorizedException.class, UnAuthenticationException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String unauthorizedException() {
        return LOGIN_FAILED;
    }

    @ExceptionHandler(CannotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String canNotFoundException() {
        return HOME;
    }

    @ExceptionHandler({CannotDeleteException.class, CannotUpdateException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String canNotDeleteAndUpdateException() {
        return HOME;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleThrowable(Throwable e) {
        log.error(e.getMessage(), e);
        return HOME;
    }
}
