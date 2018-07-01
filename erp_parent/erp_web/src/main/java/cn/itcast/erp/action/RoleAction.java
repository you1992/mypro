package cn.itcast.erp.action;
import java.util.List;

import cn.itcast.erp.biz.IRoleBiz;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;
import cn.itcast.erp.util.WebUtil;

/**
 * 角色Action 
 * @author Administrator
 *
 */
public class RoleAction extends BaseAction<Role> {

	private IRoleBiz roleBiz;
	private String ids;//前端传过来的菜单编号，多个以逗号分割

	public void setRoleBiz(IRoleBiz roleBiz) {
		this.roleBiz = roleBiz;
		super.setBaseBiz(this.roleBiz);
	}
	
	/**
	 * 显示角色权限
	 */
	public void readRoleMenus(){
		List<Tree> list = roleBiz.readRoleMenus(getId());
		WebUtil.write(list);
	}
	
	/**
	 * 设置角色权限
	 */
	public void updateRoleMenus(){
		System.out.println(String.format("roleuuid:%d ids:%s", getId(),ids));
		try {
			roleBiz.updateRoleMenus(getId(), ids);
			WebUtil.ajaxReturn(true, "更新成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "更新失败");
		}
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

}
