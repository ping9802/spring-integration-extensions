/* Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.smpp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.smpp.core.SmppConstants;
import org.springframework.integration.smpp.session.ExtendedSmppSession;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Simple tests for the gateway which differs from the outbound adapter only in that it supports
 * sending the message ID back
 *
 *
 * @author Josh Long
 * @since 1.0
 */
@ContextConfiguration("classpath:TestSmppOutboundGateway-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSmppOutboundGateway {


	private MessagingTemplate messagingTemplate = new MessagingTemplate();
	private Log log = LogFactory.getLog(getClass());

	@Value("#{session}")
	private ExtendedSmppSession smppSession;

	@Value("#{outboundSms}")
	private MessageChannel messageChannel;

	private String smsMessageToSend = "jSMPP is truly a convenient, and powerful API for SMPP " +
			"on the Java and Spring Integration platforms (sent " + System.currentTimeMillis() + ")";

	@Test
	public void testSendingAndReceivingASmppMessageUsingRawApi() throws Throwable {

		Message<String> smsMsg = MessageBuilder.withPayload(this.smsMessageToSend)
				.setHeader(SmppConstants.SRC_ADDR, "1616")
				.setHeader(SmppConstants.DST_ADDR, "628176504657")
				.setHeader(SmppConstants.REGISTERED_DELIVERY_MODE, SMSCDeliveryReceipt.SUCCESS)
				.build();

		Message<?> response = this.messagingTemplate.sendAndReceive(this.messageChannel,smsMsg);

		Assert.assertNotNull(response);
		Assert.assertTrue(response.getPayload() instanceof String);
		log.info("received the SMS Message ID: " + response.getPayload());
	}
}
