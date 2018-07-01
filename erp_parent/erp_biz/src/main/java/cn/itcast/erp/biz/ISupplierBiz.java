package cn.itcast.erp.biz;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.itcast.erp.entity.Supplier;
/**
 * 供应商业务逻辑层接口
 * @author Administrator
 *
 */
public interface ISupplierBiz extends IBaseBiz<Supplier>{

	/**
	 * 导出
	 * @param t1
	 * @param os
	 * @throws Exception
	 */
	void export(Supplier t1, OutputStream os) throws Exception;
	
	/**
	 * 导入
	 */
	void doImport(InputStream is) throws IOException;
}

