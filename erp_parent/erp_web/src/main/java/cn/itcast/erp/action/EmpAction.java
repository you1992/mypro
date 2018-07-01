package cn.itcast.erp.action;
import java.util.List;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Tree;
import cn.itcast.erp.util.WebUtil;

/**
 * 员工Action 
 * @author Administrator
 *
 */
public class EmpAction extends BaseAction<Emp> {

	private IEmpBiz empBiz;
	private String newPwd;//新密码
	private String oldPwd;//原密码
	private String ids;//角色编号，多个以逗号分割

	public void setEmpBiz(IEmpBiz empBiz) {
		this.empBiz = empBiz;
		super.setBaseBiz(this.empBiz);
	}
	
	/**
	 * 修改密码
	 */
	public void updatePwd(){
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "您还没有登陆");
			return;
		}
		try {
			empBiz.updatePwd(oldPwd, newPwd, loginUser.getUuid());
			WebUtil.ajaxReturn(true, "修改成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "修改失败");
		}
	}
	
	/**
	 * 管理员重置密码
	 */
	public void updatePwd_reset(){
		try {
			empBiz.updatePwd_reset(newPwd, getId());
			WebUtil.ajaxReturn(true, "重置密码成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "重置密码失败");
		}
	}
	
	/**
	 * 用户下的角色
	 */
	public void readEmpRoles(){
		List<Tree> list = empBiz.readEmpRoles(getId());
		WebUtil.write(list);
	}
	
	/**
	 * 更新用户角色
	 */
	public void updateEmpRoles(){
		try {
			empBiz.updateEmpRoles(getId(), ids);
			WebUtil.ajaxReturn(true, "更新成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "更新失败");
		}
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

}
