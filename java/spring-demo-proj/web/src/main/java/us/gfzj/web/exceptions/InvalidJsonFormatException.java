package us.gfzj.web.exceptions;

/**
 * @author zhoujing
 * Create on 19-2-12
 */
public class InvalidJsonFormatException extends InvalidUserParametersException{

    public InvalidJsonFormatException(String msg) {
        super(msg);
    }

    public InvalidJsonFormatException(String msg, Throwable e) {
        super(msg, e);
    }
}
