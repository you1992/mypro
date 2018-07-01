package cn.itcast.erp.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.itcast.erp.biz.IReportBiz;
import cn.itcast.erp.util.WebUtil;

/**
 * 报表action
 *
 */
public class ReportAction {

	private IReportBiz reportBiz;
	
	private Date startDate;
	private Date endDate;
	private int year;//年份
	
	/**
	 * 销售统计报表
	 */
	public void orderReport(){
		List<Map<String,Object>> list = reportBiz.orderReport(startDate, endDate);
		WebUtil.write(list);
	}
	
	/**
	 * 销售趋势报表
	 */
	public void trendReport(){
		List<Map<String, Object>> list = reportBiz.trendReport(year);
		WebUtil.write(list);
	}
	
	/**
	 * 销售趋势报表
	 */
	public void tr2(){
		List<Map<String, Object>> list = reportBiz.tr2(year);
		WebUtil.write(list);
	}

	public void setReportBiz(IReportBiz reportBiz) {
		this.reportBiz = reportBiz;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
