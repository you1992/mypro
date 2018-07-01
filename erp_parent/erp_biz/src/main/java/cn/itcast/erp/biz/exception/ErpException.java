package cn.itcast.erp.biz.exception;

/**
 * 自定义异常：中止对已知业务的错误操作
 *
 */
public class ErpException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ErpException(String message){
		super(message);
	}

}
