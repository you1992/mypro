package cn.itcast.erp.action;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.util.WebUtil;

/**
 * 登入出action
 *
 */
public class LoginAction {

	private String username;//登陆用户名
	private String pwd; //登陆密码
	
	/**
	 * 登陆
	 *
	 */
	public void login(){
		try {
			// 主题,封装了登陆用户的操作(登陆，退出，权限验证)
			Subject subject = SecurityUtils.getSubject();
			// 令牌 凭证
			UsernamePasswordToken upt = new UsernamePasswordToken(username,pwd);
			// 登陆
			subject.login(upt);
			WebUtil.ajaxReturn(true, "登陆成功");
		} catch (Exception e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "登陆失败");
		}
	}
	/*
	public void login(){
		Emp emp;
		try {
			emp = empBiz.findByUsernameAndPwd(username, pwd);
			if(null != emp){
				//登陆成功
				ServletActionContext.getRequest().getSession().setAttribute("loginUser", emp);
				WebUtil.ajaxReturn(true, "登陆成功");
			}else{
				WebUtil.ajaxReturn(false, "用户名或密码错误");
			}
		} catch (Exception e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "登陆失败");
		}
	}*/
	
	/**
	 * 显示登陆用户名
	 */
	public void showName(){
		Emp emp = WebUtil.getLoginUser();
		if(null == emp){
			WebUtil.ajaxReturn(false, "您还没有登陆!");
		}else{
			//返回登陆的用户名
			WebUtil.ajaxReturn(true, emp.getName());
		}
	}
	
	/**
	 * 退出登陆
	 */
	public void loginOut(){
		//ServletActionContext.getRequest().getSession().removeAttribute("loginUser");
		SecurityUtils.getSubject().logout();
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
}
