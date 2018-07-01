package cn.itcast.erp.biz;
import java.util.List;

import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Tree;
/**
 * 员工业务逻辑层接口
 * @author Administrator
 *
 */
public interface IEmpBiz extends IBaseBiz<Emp>{

	/**
	 * 登陆查询用户
	 * @param username
	 * @param pwd
	 * @return
	 */
	Emp findByUsernameAndPwd(String username, String pwd);
	
	/**
	 * 修改密码
	 * @param oldPwd
	 * @param newPwd
	 * @param uuid
	 */
	void updatePwd(String oldPwd, String newPwd, Long uuid);
	
	/**
	 * 管理员重置密码
	 * @param newPwd
	 * @param uuid
	 */
	void updatePwd_reset(String newPwd, Long uuid);
	
	/**
	 * 读取用户角色列表
	 * @param roleuuid
	 * @return
	 */
	List<Tree> readEmpRoles(Long uuid);
	
	/**
	 * 更新用户角色
	 * @param uuid
	 * @param ids
	 */
	void updateEmpRoles(Long uuid, String ids);
}

