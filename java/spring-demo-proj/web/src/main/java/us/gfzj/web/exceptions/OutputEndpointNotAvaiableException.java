package us.gfzj.web.exceptions;

/**
 * @author zhoujing
 * Create on 19-2-3
 */
public class OutputEndpointNotAvaiableException extends InvalidUserParametersException {
    public OutputEndpointNotAvaiableException(String msg, Throwable e) {
        super(msg, e);
    }

    public OutputEndpointNotAvaiableException(String msg) {
        super(msg);
    }
}
