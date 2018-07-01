package cn.itcast.erp.util;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * 邮件工具类
 *
 */
public class MailUtil {

	private JavaMailSender sender;
	
	private String from;//发件人
	
	public void sendMail(String subject,String to, String content) throws Exception{
		//创建邮件
		MimeMessage message = sender.createMimeMessage();
		//邮件工具类
		MimeMessageHelper helper = new MimeMessageHelper(message);
		//标题
		helper.setSubject(subject);
		//发件人
		helper.setFrom(from);
		//收件人
		helper.setTo(to);
		//内容
		helper.setText(content);
		//发送邮件
		sender.send(message);
	}

	public void setSender(JavaMailSender sender) {
		this.sender = sender;
	}

	public void setFrom(String from) {
		this.from = from;
	}
}
