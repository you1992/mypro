package cn.itcast.erp.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.entity.Emp;

@SuppressWarnings("unchecked")
public class WebUtil {

	/**
	 * //{"name":"管理员组","tele":"000011","uuid":1} 
	 * @param jsonString JSON数据字符串
	 * @param prefix 要加上的前缀
	 * @return  {"t.name":"管理员组","t.tele":"000011","t.uuid":1} 
	 */
	public static String mapData(String jsonString, String prefix){
		Map<String, Object> map = JSON.parseObject(jsonString);
		
		//需要的{"t.birthday":"2018-01-02","t.dep.name":"总裁办","t.dep.tele":"111111","t.dep.uuid":2,"t.email":"asdfas"};
		//现在给的：{"t.address":"asdf","t.birthday":"2018-01-02","t.dep":{"name":"总裁办","tele":"111111","uuid":2}}
		//需要把部门拆散到外面的｛｝中
		
		//存储key加上前缀后的值
		Map<String, Object> dataMap = new HashMap<String, Object>();
		//给每key值加上前缀
		for(String key : map.keySet()){
			String newKey = prefix + "." + key;
			//"t.dep":{"name":"总裁办","tele":"111111","uuid":2}
			if(map.get(key) instanceof Map){
				
				Map<String,Object> innerMap = (Map<String,Object>)map.get(key);
				//"t.dep.name":"总裁办","t.dep.tele":"111111","t.dep.uuid":2}
				for(String innerKey : innerMap.keySet()){
					//t + "." + "dep" + "." + "name"
					String newKey2 = newKey + "." + innerKey;
					dataMap.put(newKey2, innerMap.get(innerKey));
				}
			}else{
				dataMap.put(newKey, map.get(key));
			}
		}
		return JSON.toJSONString(dataMap);
	}
	
	/**
	 * 返回前端操作结果
	 * @param success
	 * @param message
	 */
	public static void ajaxReturn(boolean success, String message){
		//返回前端的JSON数据
		Map<String, Object> rtn = new HashMap<String, Object>();
		rtn.put("success",success);
		rtn.put("message",message);
		write(JSON.toJSONString(rtn));
	}
	
	public static void write(Object obj){
		write(JSON.toJSONString(obj));
	}
	
	/**
	 * 输出字符串到前端
	 * @param jsonString
	 */
	public static void write(String jsonString){
		try {
			//响应对象
			HttpServletResponse response = ServletActionContext.getResponse();
			//设置编码
			response.setContentType("text/html;charset=utf-8"); 
			//输出给页面
			response.getWriter().write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Emp getLoginUser(){
		// 当事人
		Emp emp = (Emp)SecurityUtils.getSubject().getPrincipal();
		
		return emp;
	}
}
