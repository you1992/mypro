package cn.itcast.erp.biz.impl;
import java.util.List;

import cn.itcast.erp.biz.IDepBiz;
import cn.itcast.erp.biz.exception.ErpException;
import cn.itcast.erp.dao.IDepDao;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.entity.Dep;
import cn.itcast.erp.entity.Emp;
/**
 * 部门业务逻辑类
 * @author Administrator
 *
 */
public class DepBiz extends BaseBiz<Dep> implements IDepBiz {

	private IDepDao depDao;
	private IEmpDao empDao;
	
	public void setDepDao(IDepDao depDao) {
		this.depDao = depDao;
		super.setBaseDao(this.depDao);
	}
	
	@Override
	public void delete(Long uuid) {
		
		//根据部门编号查询员工列表
		Emp emp = new Emp();
		//构建员工所属部门
		emp.setDep(new Dep());
		//指定部门的编号
		emp.getDep().setUuid(uuid);
		List<Emp> list = empDao.getList(emp, null, null);
		if(null != list && list.size() > 0){
			//部门下有员工
			throw new ErpException("该部门下有员工，不能删除");
		}
		super.delete(uuid);

	}

	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}
	
}
