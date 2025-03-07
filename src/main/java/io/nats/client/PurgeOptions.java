// Copyright 2020 The NATS Authors
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

package io.nats.client;

import io.nats.client.support.JsonSerializable;
import io.nats.client.support.JsonUtils;

import static io.nats.client.support.ApiConstants.*;
import static io.nats.client.support.JsonUtils.beginJson;
import static io.nats.client.support.JsonUtils.endJson;
import static io.nats.client.support.Validator.validateSubject;

/**
 * The PurgeOptions class specifies the options for purging a stream
 */
public class PurgeOptions implements JsonSerializable {

    protected final String subject;
    protected final long seq;
    protected final long keep;

    private PurgeOptions(String subject, long seq, long keep) {
        this.subject = subject;
        this.seq = seq;
        this.keep = keep;
    }

    @Override
    public String toJson() {
        StringBuilder sb = beginJson();
        JsonUtils.addField(sb, FILTER, subject);
        JsonUtils.addField(sb, SEQ, seq);
        JsonUtils.addField(sb, KEEP, keep);
        return endJson(sb).toString();
    }

    /**
     * Get the subject for the Purge Options
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Get the upper bound sequence for the Purge Options
     * @return the upper bound sequence
     */
    public long getSeq() {
        return seq;
    }

    /**
     * Get the max number of messages to keep for the Purge Options
     * @return the max number of messages to keep
     */
    public long getKeep() {
        return keep;
    }

    public static PurgeOptions.Builder builder() {
        return new Builder();
    }

    public static PurgeOptions.Builder subject() {
        return new Builder();
    }

    public static class Builder {
        private String subject;
        private long seq;
        private long keep;

        /**
         * set the subject to filter the purge. Wildcards allowed.
         * @param subject the subject
         * @return the builder
         */
        public Builder subject(final String subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Set upper-bound sequence for messages to be deleted
         * @param seq the upper-bound sequence
         * @return the builder
         */
        public Builder seq(final long seq) {
            this.seq = seq;
            return this;
        }

        /**
         * set the max number of messages to keep
         * @param keep the max number of messages to keep
         * @return the builder
         */
        public Builder keep(final long keep) {
            this.keep = keep;
            return this;
        }

        public PurgeOptions build() {
            validateSubject(subject, false);

            // TODO move this validation to Validator
            if (seq > 0 && keep > 0) {
                throw new IllegalArgumentException("seq and keep are mutually exclusive.");
            }

            return new PurgeOptions(subject, seq, keep);
        }
    }
}
