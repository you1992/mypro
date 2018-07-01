package cn.itcast.erp.entity;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 角色实体类
 * 
 * @author Administrator *
 */
public class Role {
	private Long uuid;// 编号
	private String name;// 名称
	@JSONField(serialize=false)//角色管理页面不需要显示菜单权限信息
	private List<Menu> menus;// 该角色所拥有的权限
	@JSONField(serialize=false)//角色管理页面不需要显示用户信息
	private List<Emp> emps;//拥有这个角色的所有用户
	
	public Long getUuid() {
		return uuid;
	}

	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public List<Emp> getEmps() {
		return emps;
	}

	public void setEmps(List<Emp> emps) {
		this.emps = emps;
	}

}
