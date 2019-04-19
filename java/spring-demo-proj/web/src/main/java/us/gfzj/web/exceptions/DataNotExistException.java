package us.gfzj.web.exceptions;

/**
 * @author Zhiqiang Lin
 * @Description
 * @create 2019/2/26.
 */
public class DataNotExistException extends ResourceNotFoundException {
    public DataNotExistException(String s) {
        super(s);
    }
}
