package com.youlite.jxc.common.transport;


public interface IObjectTransportService extends ITransportService {
	void createReceiver(String subject, IObjectListener listener) throws Exception;
	void createSubscriber(String subject, IObjectListener listener) throws Exception;
	void removeSubscriber(String subject, IObjectListener listener) throws Exception;
	void sendMessage(String subject, Object obj) throws Exception;
	void publishMessage(String subject, Object obj) throws Exception;

	IObjectSender createObjectSender(String subject) throws Exception;
	IObjectSender createObjectPublisher(String subject) throws Exception;

}
