package us.gfzj.web.exceptions;

/**
 * @author zhoujing
 * Create on 19-2-3
 */
public class InvalidTimeRangeException extends InvalidUserParametersException {

    public InvalidTimeRangeException(String msg, Throwable e) {
        super(msg, e);
    }

    public InvalidTimeRangeException(String msg) {
        super(msg);
    }
}
