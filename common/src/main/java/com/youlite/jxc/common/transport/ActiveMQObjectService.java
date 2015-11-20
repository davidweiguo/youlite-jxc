/*******************************************************************************
 * Copyright (c) 2011-2012 Cyan Spring Limited
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms specified by license file attached.
 * 
 * Software distributed under the License is released on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License.
 ******************************************************************************/
package com.youlite.jxc.common.transport;

import java.util.ArrayList;
import java.util.HashMap;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ActiveMQObjectService extends ActiveMQService implements
		IObjectTransportService {
	static Logger log = LoggerFactory.getLogger(ActiveMQObjectService.class);
	private XStream xstream = new XStream(new DomDriver());

	private HashMap<String, ArrayList<IObjectListener>> objSubscribers = new HashMap<String, ArrayList<IObjectListener>>();
	private HashMap<IObjectListener, MessageConsumer> objConsumers = new HashMap<IObjectListener, MessageConsumer>();

	class ObjectListenerAdaptor implements MessageListener {

		private IObjectListener listener;

		public ObjectListenerAdaptor(IObjectListener listener) {
			this.listener = listener;
		}

		public void onMessage(Message message) {
			try {
				if (message instanceof TextMessage) {
					String str = ((TextMessage) message).getText();
					log.debug("ActiveMQObjectService Received message: " + str);
					Object obj = xstream.fromXML(str);
					listener.onMessage(obj);
				} else if (message instanceof BytesMessage) {
					BytesMessage bms = (BytesMessage) message;
					int length = (int) bms.getBodyLength();
					if (length == 0) {
						log.warn("ActiveMQObjectService message length is 0, message=["
								+ message.toString() + "]");
						return;
					}
					byte[] bytes = new byte[length];
					bms.readBytes(bytes);
					listener.onMessage(new String(bytes));
					log.debug("ActiveMQObjectService Received message: "
							+ new String(bytes));
				} else {
					log.error("ActiveMQObjectService Unexpected text message: "
							+ message);
				}
			} catch (JMSException e) {
				log.error(e.getMessage(), e);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

	}

	class ObjectSender implements IObjectSender {
		private MessageProducer producer;

		public ObjectSender(MessageProducer producer) {
			this.producer = producer;
		}

		public void sendMessage(Object obj) throws Exception {
			String message = xstream.toXML(obj);
			log.debug("Sending message: \n" + message);
			TextMessage txt = session.createTextMessage(message);
			producer.send(txt);
		}

	}

	public void createReceiver(String subject, IObjectListener listener)
			throws Exception {
		// only one listener per subject allowed for point to point connection
		MessageConsumer consumer = receivers.get(subject);
		if (null == consumer) {
			Destination dest = session.createQueue(subject);
			consumer = session.createConsumer(dest);
			receivers.put(subject, consumer);
		}
		if (null == listener)
			consumer.setMessageListener(null);
		else
			consumer.setMessageListener(new ObjectListenerAdaptor(listener));
	}

	public void createSubscriber(String subject, IObjectListener listener)
			throws Exception {
		// many listeners per subject allowed for publish/subscribe
		ArrayList<IObjectListener> listeners = objSubscribers.get(subject);
		if (null == listeners) {
			listeners = new ArrayList<IObjectListener>();
			objSubscribers.put(subject, listeners);
		}

		if (!listeners.contains(listener)) {
			Destination dest = session.createTopic(subject);
			MessageConsumer consumer = session.createConsumer(dest);
			consumer.setMessageListener(new ObjectListenerAdaptor(listener));
			listeners.add(listener);
			objConsumers.put(listener, consumer);
		}
	}

	public void removeSubscriber(String subject, IObjectListener listener)
			throws Exception {
		ArrayList<IObjectListener> listeners = objSubscribers.get(subject);
		if (null == listeners)
			return;

		if (listeners.contains(listener)) {
			MessageConsumer consumer = objConsumers.get(listener);
			if (null == consumer)
				return;
			consumer.setMessageListener(null);
			objConsumers.remove(listener);
			listeners.remove(listener);
		}

	}

	public void sendMessage(String subject, Object obj) throws Exception {
		createObjectSender(subject).sendMessage(obj);

	}

	public void publishMessage(String subject, Object obj) throws Exception {
		createObjectPublisher(subject).sendMessage(obj);

	}

	public IObjectSender createObjectSender(String subject) throws Exception {
		MessageProducer producer = senders.get(subject);
		if (null == producer) {
			Destination dest = session.createQueue(subject);
			producer = session.createProducer(dest);
			producer.setDeliveryMode(persistent);
			senders.put(subject, producer);
		}

		return new ObjectSender(producer);
	}

	public IObjectSender createObjectPublisher(String subject) throws Exception {
		MessageProducer producer = publishers.get(subject);
		if (null == producer) {
			Destination dest = session.createTopic(subject);
			producer = session.createProducer(dest);
			producer.setDeliveryMode(persistent);
			publishers.put(subject, producer);
		}

		return new ObjectSender(producer);
	}

}
