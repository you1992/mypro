package cn.itcast.erp.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.util.MailUtil;

/**
 * 后台发邮件任务
 *
 */
public class MailJob {

	private IStoredetailDao storedetailDao;
	
	private MailUtil mailUtil;
	
	private String subject;
	
	private String to;
	
	private String content;
	
	public void doJob(){
		List<Storealert> list = storedetailDao.getStorealertList();
		if(null != list && list.size() > 0){//有商品要预警
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			String _subject = subject.replace("[time]", sdf.format(new Date()));
			String _content = content.replace("[count]", list.size() + "");
			try {
				mailUtil.sendMail(_subject, to, _content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
	}

	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
