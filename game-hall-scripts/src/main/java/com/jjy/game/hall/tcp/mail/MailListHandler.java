package com.jjy.game.hall.tcp.mail;

import java.util.Date;
import java.util.List;

import com.jjy.game.hall.manager.MailManager;
import com.jjy.game.message.Mid.MID;
import com.jjy.game.message.hall.HallChatMessage.MailListRequest;
import com.jjy.game.message.hall.HallChatMessage.MailListResponse;
import com.jjy.game.model.mongo.hall.dao.MailDao;
import com.jjy.game.model.struct.Mail;
import com.jzy.game.engine.handler.HandlerEntity;
import com.jzy.game.engine.handler.TcpHandler;

/**
 * 请求邮件列表
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年9月21日 下午5:18:19
 */
@HandlerEntity(mid=MID.MailListReq_VALUE,msg=MailListRequest.class)
public class MailListHandler extends TcpHandler {
	
	@Override
	public void run() {
		List<Mail> publicMails = MailDao.getPublicMails();
		List<Mail> mails = MailDao.getMails(rid);
		if(mails!=null&&publicMails!=null) {
			mails.addAll(publicMails);
		}
		MailListResponse.Builder builder=MailListResponse.newBuilder();
		if(mails!=null) {
			Date now=new Date();
			mails.forEach(mail->{
				//过期邮件监测
				if(mail.getDeleteTime()!=null&&mail.getDeleteTime().after(now)) {
					MailDao.deleteMail(mail.getId());
				}else {
					builder.addMails(MailManager.getInstance().buildMailInfo(mail));
				}
			});
		}
		sendIdMsg(builder.build());
	}

}
