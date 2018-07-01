package cn.itcast.erp.action;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import cn.itcast.erp.biz.ISupplierBiz;
import cn.itcast.erp.entity.Supplier;
import cn.itcast.erp.util.WebUtil;

/**
 * 供应商Action 
 * @author Administrator
 *
 */
public class SupplierAction extends BaseAction<Supplier> {

	private ISupplierBiz supplierBiz;

	public void setSupplierBiz(ISupplierBiz supplierBiz) {
		this.supplierBiz = supplierBiz;
		super.setBaseBiz(this.supplierBiz);
	}
	
	private String q;//下拉表格mode设置为remote时发送的参数

	public void setQ(String q) {
		this.q = q;
	}
	
	/**
	 * 模糊查询
	 */
	public void list(){
		if(!StringUtils.isEmpty(q)){
			if(null == getT1()){
				//构建查询
				setT1(new Supplier());
			}
			getT1().setName(q);
		}
		super.list();
	}
	
	/**
	 * 导出
	 */
	public void export(){
		String filename = "";
		if(Supplier.TYPE_SUPPLIER.equals(getT1().getType())){
			filename = "供应商.xls";
		}
		if(Supplier.TYPE_CUSTOMER.equals(getT1().getType())){
			filename = "客户.xls";
		}
		
		HttpServletResponse res = ServletActionContext.getResponse();
		try {
			//协议头的信息是使用iso-8859-1的编码,解决乱码问题
			filename = new String(filename.getBytes(),"ISO-8859-1");
			//告诉浏览器，响应的是一个附件，字节流来接收并且保存成文件		
			res.setHeader("Content-Disposition","attachment;filename=" + filename);
			supplierBiz.export(getT1(), res.getOutputStream());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private File file;//页面form表单里的type=file元素name必须与这个一致
	private String fileFileName;//上传的文件名
	private String fileContentType;//文件类型

	public void setFile(File file) {
		this.file = file;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}
	
	/**
	 * 导入数据
	 */
	public void doImport(){
		//类型
		if(!"application/vnd.ms-excel".equals(fileContentType)){
			//不是excel文件
			if(!fileFileName.endsWith(".xls")){
				WebUtil.ajaxReturn(false, "文件格式不正确");
				return;
			}
		}
		
		try {
			supplierBiz.doImport(new FileInputStream(file));
			WebUtil.ajaxReturn(true, "导入成功");
		} catch (IOException e) {
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "导入失败");
		}
	}
	

}
