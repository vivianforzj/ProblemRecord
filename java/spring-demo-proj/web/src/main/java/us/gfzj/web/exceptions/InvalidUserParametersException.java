package us.gfzj.web.exceptions;

/**
 * @author zhoujing
 * Create on 19-2-12
 */
public class InvalidUserParametersException extends Exception {
    public InvalidUserParametersException(String msg) {
        super(msg);
    }

    public InvalidUserParametersException(String msg, Throwable e) {
        super(msg, e);
    }
}
