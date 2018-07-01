package cn.itcast.erp.biz;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IReportBiz {

	/**
	 * 销售统计报表
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<Map<String,Object>> orderReport(Date startDate, Date endDate);
	
	/**
	 * 趋势报表
	 * @param year
	 * @return
	 */
	List<Map<String,Object>> trendReport(int year);
	
	/**
	 * 趋势报表
	 * @param year
	 * @return
	 */
	List<Map<String,Object>> tr2(int year);
}
