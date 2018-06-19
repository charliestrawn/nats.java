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

import io.nats.client.Statistics;

import java.text.NumberFormat;
import java.util.LongSummaryStatistics;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

class NatsStatistics implements Statistics {
    private ReentrantLock lock;
    private LongSummaryStatistics accumulateStats;

    private AtomicLong flushCounter;
    private AtomicLong outstandingRequests;
    private AtomicLong requestsSent;
    private AtomicLong repliesReceived;
    private AtomicLong inMsgs;
    private AtomicLong outMsgs;
    private AtomicLong inBytes;
    private AtomicLong outBytes;

    public NatsStatistics() {
        this.lock = new ReentrantLock();
        this.accumulateStats = new LongSummaryStatistics();
        this.flushCounter = new AtomicLong();
        this.outstandingRequests = new AtomicLong();
        this.requestsSent = new AtomicLong();
        this.repliesReceived = new AtomicLong();
        this.inMsgs = new AtomicLong();
        this.outMsgs = new AtomicLong();
        this.inBytes = new AtomicLong();
        this.outBytes = new AtomicLong();
    }

    void incrementRequestsSent() {
        this.requestsSent.incrementAndGet();
    }

    void incrementRepliesReceived() {
        this.repliesReceived.incrementAndGet();
    }

    void incrementInMsgs() {
        this.inMsgs.incrementAndGet();
    }

    void incrementOutMsgs() {
        this.outMsgs.incrementAndGet();
    }

    void incrementInBytes(long bytes) {
        this.inBytes.addAndGet(bytes);
    }

    void incrementOutBytes(long bytes) {
        this.outBytes.addAndGet(bytes);
    }

    void incrementFlushCounter() {
        this.flushCounter.incrementAndGet();
    }

    void incrementOutstandingRequests() {
        this.outstandingRequests.incrementAndGet();
    }

    void decrementOutstandingRequests() {
        this.outstandingRequests.decrementAndGet();
    }

    void registerAccumulate(long msgCount) {
        lock.lock();
        try {
            accumulateStats.accept(msgCount);
        } finally {
            lock.unlock();
        }
    }

    public long getInMsgs() {
        return this.inMsgs.get();
    }

    public long getOutMsgs() {
        return this.outMsgs.get();
    }

    public long getInBytes() {
        return this.inBytes.get();
    }

    public long getOutBytes() {
        return this.outBytes.get();
    }

    public long getFlushCounter() {
        return flushCounter.get();
    }

    public long getOutstandingRequests() {
        return outstandingRequests.get();
    }

    void appendNumberStat(StringBuilder builder, String name, long value) {
        builder.append(name);
        builder.append(NumberFormat.getNumberInstance().format(value));
        builder.append("\n");
    }

    void appendNumberStat(StringBuilder builder, String name, double value) {
        builder.append(name);
        builder.append(NumberFormat.getNumberInstance().format(value));
        builder.append("\n");
    }

    public String buildHumanFriendlyString() {
        StringBuilder builder = new StringBuilder();

        lock.lock();
        try {
            builder.append("### Connection ###\n");
            appendNumberStat(builder, "Requests Sent:                   ", this.requestsSent.get());
            appendNumberStat(builder, "Replies Received:                ", this.repliesReceived.get());
            builder.append("\n");
            appendNumberStat(builder, "Successful Flush Calls:          ", this.flushCounter.get());
            appendNumberStat(builder, "Outstanding Request Futures:     ", this.outstandingRequests.get());
            builder.append("\n");
            builder.append("### Reader ###\n");
            appendNumberStat(builder, "Messages in:                     ", this.inMsgs.get());
            appendNumberStat(builder, "Bytes in:                        ", this.inBytes.get());
            builder.append("\n");
            builder.append("### Writer ###\n");
            appendNumberStat(builder, "Messages out:                    ", this.outMsgs.get());
            appendNumberStat(builder, "Bytes out:                       ", this.outBytes.get());
            builder.append("\n");
            appendNumberStat(builder, "Accumulation Calls:              ", accumulateStats.getCount());
            appendNumberStat(builder, "Average Messages Per Accumulate: ", accumulateStats.getAverage());
            appendNumberStat(builder, "Min Messages Per Accumulate:     ", accumulateStats.getMin());
            appendNumberStat(builder, "Max Messages Per Accumulate:     ", accumulateStats.getMax());
        } finally {
            lock.unlock();
        }

        return builder.toString();
    }
}