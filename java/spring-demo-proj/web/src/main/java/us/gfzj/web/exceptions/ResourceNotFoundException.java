package us.gfzj.web.exceptions;

/**
 * @author zhoujing
 * Create on 19-2-22
 */
public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String msg) {
        super(msg);
    }

    public ResourceNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
