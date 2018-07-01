package cn.itcast.erp.biz.impl;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.itcast.erp.biz.IStoredetailBiz;
import cn.itcast.erp.biz.exception.ErpException;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IStoreDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.util.MailUtil;
/**
 * 仓库库存业务逻辑类
 * @author Administrator
 *
 */
public class StoredetailBiz extends BaseBiz<Storedetail> implements IStoredetailBiz {

	private IStoredetailDao storedetailDao;
	private IGoodsDao goodsDao;
	private IStoreDao storeDao;
	private MailUtil mailUtil;
	private String subject; 
	private String to; 
	private String content;
	
	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
		super.setBaseDao(this.storedetailDao);
	}
	
	@Override
	public List<Storedetail> getListByPage(Storedetail t1, Storedetail t2, Object param, int firstResult,
			int maxResults) {
		List<Storedetail> list = super.getListByPage(t1, t2, param, firstResult, maxResults);
		for (Storedetail sd : list) {
			sd.setGoodsName(goodsDao.get(sd.getGoodsuuid()).getName());
			sd.setStoreName(storeDao.get(sd.getStoreuuid()).getName());
		}
		return list;
	}

	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
	}

	@Override
	public List<Storealert> getStorealertList() {
		return storedetailDao.getStorealertList();
	}

	@Override
	public void sendStorealertMail() throws Exception {
		List<Storealert> list = storedetailDao.getStorealertList();
		if(null == list || list.size() == 0){
			throw new ErpException("没有商品预警");
		}
		//有商品要预警
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String _subject = subject.replace("[time]", sdf.format(new Date()));
		String _content = content.replace("[count]", list.size() + "");
		mailUtil.sendMail(_subject, to, _content);
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
