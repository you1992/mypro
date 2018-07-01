package cn.itcast.erp.filter;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
/**
 * 自定义过滤器
 *
 */
public class MyAuthorizationFilter extends PermissionsAuthorizationFilter {

	@Override
	public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws IOException {
		// 主题
		Subject subject = getSubject(request, response);
		// 该url标定的权限信息
        String[] perms = (String[]) mappedValue;
        //没有标定权限时，让其通过
        if(null == perms){
        	return true;
        }
        //有标定权限时，只要有一个通过，则放行
        for (String perm : perms) {
        	//授权的入口
			if(subject.isPermitted(perm)){
				return true;
			}
		}
        //一个也没有通过，则拦截
        return false;
	}
}
