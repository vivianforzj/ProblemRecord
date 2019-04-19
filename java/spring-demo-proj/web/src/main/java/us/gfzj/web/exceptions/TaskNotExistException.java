package us.gfzj.web.exceptions;

/**
 * @author zhoujing
 * Create on 19-2-22
 */
public class TaskNotExistException extends ResourceNotFoundException {
    public TaskNotExistException(String msg) {
        super(msg);
    }

    public TaskNotExistException(String msg, Throwable e) {
        super(msg, e);
    }
}
