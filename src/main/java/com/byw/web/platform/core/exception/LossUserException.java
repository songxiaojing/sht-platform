package com.byw.web.platform.core.exception;

import com.byw.web.platform.core.exception.PlatformException;


/**
 * 
 * 用户丢失异常.
 * 
 * 用户丢失异常.
 * 
 * @title LossUserException
 * @package com.topsec.tss.core.web.exception
 * @author baiyanwei
 * @version
 * @date 2014-10-30
 * 
 */
public class LossUserException extends PlatformException {

    /**
     * Your field description in here.
     */
    private static final long serialVersionUID = 6180211011603248234L;

    public LossUserException(String exceptionMassage) {

        super(exceptionMassage);
    }

}
