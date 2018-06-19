// Copyright 2015-2018 The NATS Authors
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.nats.client.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class NatsMessageTests {
    @Test
    public void testSizeOnProtocolMessage() {
        NatsMessage msg = new NatsMessage("PING");

        assertEquals("Size is set, with CRLF", msg.getProtocolBytes().length + 2, msg.getSize());
        assertEquals("Size is correct", "PING".getBytes(StandardCharsets.UTF_8).length + 2, msg.getSize());
    }
    
    @Test
    public void testSizeOnPublishMessage() {
        byte[] body = new byte[10];
        String subject = "subj";
        String replyTo = "reply";
        String protocol = "PUB "+subject+" "+replyTo+" "+body.length;

        NatsMessage msg = new NatsMessage(subject, replyTo, body);

        assertEquals("Size is set, with CRLF", msg.getProtocolBytes().length + body.length + 4, msg.getSize());
        assertEquals("Size is correct", protocol.getBytes(StandardCharsets.UTF_8).length + body.length + 4, msg.getSize());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testBigProtocolLineWithBody() {
        byte[] body = new byte[10];
        String subject = "subject";
        String replyTo = "reply";

        while (subject.length() <= NatsConnection.MAX_PROTOCOL_LINE) {
            subject = subject + subject;
        }

        new NatsMessage(subject, replyTo, body);
        assertFalse(true);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testBigProtocolLineWithoutBody() {
        String protocol = "subject";

        while (protocol.length() <= NatsConnection.MAX_PROTOCOL_LINE) {
            protocol = protocol + protocol;
        }

        new NatsMessage(protocol);
        assertFalse(true);
    }
}