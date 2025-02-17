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

package io.nats.client.support;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static io.nats.client.support.NatsConstants.EMPTY;
import static io.nats.client.support.NatsJetStreamConstants.MAX_PULL_SIZE;
import static io.nats.client.support.Validator.*;
import static io.nats.client.utils.ResourceUtils.dataAsLines;
import static io.nats.client.utils.TestBase.*;
import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTests {
    private static List<String> UTF_ONLY_STRINGS;

    @BeforeAll
    public static void beforeAll() {
        UTF_ONLY_STRINGS = dataAsLines("utf8-only-no-ws-test-strings.txt");
    }

    @Test
    public void testValidateSubject() {
        allowedRequired(Validator::validateSubject, Arrays.asList(PLAIN, HAS_PRINTABLE, HAS_DOT, HAS_STAR, HAS_GT, HAS_DOLLAR));
        notAllowedRequired(Validator::validateSubject, Arrays.asList(null, EMPTY, HAS_SPACE, HAS_LOW, HAS_127));
        notAllowedRequired(Validator::validateSubject, UTF_ONLY_STRINGS);
        allowedNotRequiredEmptyAsNull(Validator::validateSubject, Arrays.asList(null, EMPTY));

        notAllowedRequired(Validator::validateSubject, Arrays.asList(null, EMPTY, HAS_SPACE, HAS_LOW, HAS_127));
        allowedNotRequiredEmptyAsNull(Validator::validateSubject, Arrays.asList(null, EMPTY));
    }

    @Test
    public void testValidateReplyTo() {
        allowedRequired(Validator::validateReplyTo, Arrays.asList(PLAIN, HAS_PRINTABLE, HAS_DOT, HAS_DOLLAR));
        notAllowedRequired(Validator::validateReplyTo, Arrays.asList(null, EMPTY, HAS_SPACE, HAS_STAR, HAS_GT, HAS_LOW, HAS_127));
        notAllowedRequired(Validator::validateReplyTo, UTF_ONLY_STRINGS);
        allowedNotRequiredEmptyAsNull(Validator::validateReplyTo, Arrays.asList(null, EMPTY));
    }

    @Test
    public void testValidateQueueName() {
        // validateQueueName(String s, boolean required)
        allowedRequired(Validator::validateQueueName, Arrays.asList(PLAIN, HAS_PRINTABLE, HAS_DOLLAR));
        notAllowedRequired(Validator::validateQueueName, Arrays.asList(null, EMPTY, HAS_SPACE, HAS_DOT, HAS_STAR, HAS_GT, HAS_LOW, HAS_127));
        notAllowedRequired(Validator::validateQueueName, UTF_ONLY_STRINGS);
        allowedNotRequiredEmptyAsNull(Validator::validateQueueName, Arrays.asList(null, EMPTY));
    }

    @Test
    public void testValidateStreamName() {
        allowedRequired(Validator::validateStreamName, Arrays.asList(PLAIN, HAS_PRINTABLE, HAS_DOLLAR));
        notAllowedRequired(Validator::validateStreamName, Arrays.asList(null, EMPTY, HAS_SPACE, HAS_DOT, HAS_STAR, HAS_GT, HAS_LOW, HAS_127));
        notAllowedRequired(Validator::validateStreamName, UTF_ONLY_STRINGS);
        allowedNotRequiredEmptyAsNull(Validator::validateStreamName, Arrays.asList(null, EMPTY));
    }

    @Test
    public void testValidateDurable() {
        allowedRequired(Validator::validateDurable, Arrays.asList(PLAIN, HAS_PRINTABLE, HAS_DOLLAR));
        notAllowedRequired(Validator::validateDurable, Arrays.asList(null, EMPTY, HAS_SPACE, HAS_DOT, HAS_STAR, HAS_GT, HAS_LOW, HAS_127));
        notAllowedRequired(Validator::validateDurable, UTF_ONLY_STRINGS);
        allowedNotRequiredEmptyAsNull(Validator::validateDurable, Arrays.asList(null, EMPTY));
    }

    @Test
    public void testValidatePullBatchSize() {
        assertEquals(1, validatePullBatchSize(1));
        assertEquals(MAX_PULL_SIZE, validatePullBatchSize(MAX_PULL_SIZE));
        assertThrows(IllegalArgumentException.class, () -> validatePullBatchSize(0));
        assertThrows(IllegalArgumentException.class, () -> validatePullBatchSize(-1));
        assertThrows(IllegalArgumentException.class, () -> validatePullBatchSize(MAX_PULL_SIZE + 1));
    }

    @Test
    public void testValidateMaxConsumers() {
        assertEquals(1, validateMaxConsumers(1));
        assertEquals(-1, validateMaxConsumers(-1));
        assertThrows(IllegalArgumentException.class, () -> validateMaxConsumers(0));
        assertThrows(IllegalArgumentException.class, () -> validateMaxMessages(-2));
    }

    @Test
    public void testValidateMaxMessages() {
        assertEquals(1, validateMaxMessages(1));
        assertEquals(-1, validateMaxMessages(-1));
        assertThrows(IllegalArgumentException.class, () -> validateMaxMessages(0));
        assertThrows(IllegalArgumentException.class, () -> validateMaxMessages(-2));
    }

    @Test
    public void testValidateMaxBucketValues() {
        assertEquals(1, validateMaxBucketValues(1));
        assertEquals(-1, validateMaxBucketValues(-1));
        assertThrows(IllegalArgumentException.class, () -> validateMaxBucketValues(0));
        assertThrows(IllegalArgumentException.class, () -> validateMaxBucketValues(-2));
    }

    @Test
    public void testValidateMaxMessagesPerSubject() {
        assertEquals(1, validateMaxMessagesPerSubject(1));
        assertEquals(-1, validateMaxMessagesPerSubject(-1));

        assertEquals(0, validateMaxMessagesPerSubject(0));
        // TODO Waiting on a server change take that /\ out, put this \/ back in
//        assertThrows(IllegalArgumentException.class, () -> validateMaxMessagesPerSubject(0));

        assertThrows(IllegalArgumentException.class, () -> validateMaxMessagesPerSubject(-2));
    }

    @Test
    public void testValidateMaxValuesPerKey() {
        assertEquals(1, validateMaxValuesPerKey(1));
        assertEquals(-1, validateMaxValuesPerKey(-1));

        assertEquals(0, validateMaxValuesPerKey(0));
        // TODO Waiting on a server change take that /\ out, put this \/ back in
//        assertThrows(IllegalArgumentException.class, () -> validateMaxValuesPerKey(0));

        assertThrows(IllegalArgumentException.class, () -> validateMaxValuesPerKey(-2));
    }

    @Test
    public void testValidateMaxBytes() {
        assertEquals(1, validateMaxBytes(1));
        assertEquals(-1, validateMaxBytes(-1));
        assertThrows(IllegalArgumentException.class, () -> validateMaxBytes(0));
        assertThrows(IllegalArgumentException.class, () -> validateMaxMessages(-2));
    }

    @Test
    public void testValidateMaxBucketBytes() {
        assertEquals(1, validateMaxBucketBytes(1));
        assertEquals(-1, validateMaxBucketBytes(-1));
        assertThrows(IllegalArgumentException.class, () -> validateMaxBucketBytes(0));
        assertThrows(IllegalArgumentException.class, () -> validateMaxMessages(-2));
    }

    @Test
    public void testValidateMaxMessageSize() {
        assertEquals(1, validateMaxMessageSize(1));
        assertEquals(-1, validateMaxMessageSize(-1));
        assertThrows(IllegalArgumentException.class, () -> validateMaxMessageSize(0));
        assertThrows(IllegalArgumentException.class, () -> validateMaxMessages(-2));
    }

    @Test
    public void testValidateMaxValueSize() {
        assertEquals(1, validateMaxValueSize(1));
        assertEquals(-1, validateMaxValueSize(-1));
        assertThrows(IllegalArgumentException.class, () -> validateMaxValueSize(0));
        assertThrows(IllegalArgumentException.class, () -> validateMaxMessages(-2));
    }

    @Test
    public void testValidateNumberOfReplicas() {
        assertEquals(1, validateNumberOfReplicas(1));
        assertEquals(5, validateNumberOfReplicas(5));
        assertThrows(IllegalArgumentException.class, () -> validateNumberOfReplicas(-1));
        assertThrows(IllegalArgumentException.class, () -> validateNumberOfReplicas(0));
        assertThrows(IllegalArgumentException.class, () -> validateNumberOfReplicas(7));
    }

    @Test
    public void testValidateDurationRequired() {
        assertEquals(Duration.ofNanos(1), validateDurationRequired(Duration.ofNanos(1)));
        assertEquals(Duration.ofSeconds(1), validateDurationRequired(Duration.ofSeconds(1)));
        assertThrows(IllegalArgumentException.class, () -> validateDurationRequired(null));
        assertThrows(IllegalArgumentException.class, () -> validateDurationRequired(Duration.ofNanos(0)));
        assertThrows(IllegalArgumentException.class, () -> validateDurationRequired(Duration.ofSeconds(0)));
        assertThrows(IllegalArgumentException.class, () -> validateDurationRequired(Duration.ofNanos(-1)));
        assertThrows(IllegalArgumentException.class, () -> validateDurationRequired(Duration.ofSeconds(-1)));
    }

    @Test
    public void testValidateDurationNotRequiredGtOrEqZero() {
        Duration ifNull = Duration.ofMillis(999);
        assertEquals(ifNull, validateDurationNotRequiredGtOrEqZero(null, ifNull));
        assertEquals(Duration.ZERO, validateDurationNotRequiredGtOrEqZero(Duration.ZERO, ifNull));
        assertEquals(Duration.ofNanos(1), validateDurationNotRequiredGtOrEqZero(Duration.ofNanos(1), ifNull));
        assertThrows(IllegalArgumentException.class, () -> validateDurationNotRequiredGtOrEqZero(Duration.ofNanos(-1), ifNull));

        assertEquals(Duration.ZERO, validateDurationNotRequiredGtOrEqZero(0));
        assertEquals(Duration.ofMillis(1), validateDurationNotRequiredGtOrEqZero(1));
        assertEquals(Duration.ofSeconds(1), validateDurationNotRequiredGtOrEqZero(1000));
        assertThrows(IllegalArgumentException.class, () -> validateDurationNotRequiredGtOrEqZero(-1));
    }

    @Test
    public void testValidateGtEqZero() {
        assertEquals(0, validateGtEqZero(0, "test"));
        assertEquals(1, validateGtEqZero(1, "test"));
        assertThrows(IllegalArgumentException.class, () -> validateGtEqZero(-1, "test"));
    }

    @Test
    public void testEnsureDuration() {
        assertEquals(Duration.ofMillis(10), ensureNotNullAndNotLessThanMin(null, Duration.ofMillis(2), Duration.ofMillis(10)));
        assertEquals(Duration.ofMillis(10), ensureNotNullAndNotLessThanMin(Duration.ofMillis(1), Duration.ofMillis(2), Duration.ofMillis(10)));
        assertEquals(Duration.ofMillis(100), ensureNotNullAndNotLessThanMin(Duration.ofMillis(100), Duration.ofMillis(2), Duration.ofMillis(10)));
        assertEquals(Duration.ofMillis(10), ensureDurationNotLessThanMin(1, Duration.ofMillis(2), Duration.ofMillis(10)));
        assertEquals(Duration.ofMillis(100), ensureDurationNotLessThanMin(100, Duration.ofMillis(2), Duration.ofMillis(10)));
    }

    @Test
    public void testValidateJetStreamPrefix() {
        assertThrows(IllegalArgumentException.class, () -> validateJetStreamPrefix(HAS_STAR));
        assertThrows(IllegalArgumentException.class, () -> validateJetStreamPrefix(HAS_GT));
        assertThrows(IllegalArgumentException.class, () -> validateJetStreamPrefix(HAS_DOLLAR));
        assertThrows(IllegalArgumentException.class, () -> validateJetStreamPrefix(HAS_SPACE));
        assertThrows(IllegalArgumentException.class, () -> validateJetStreamPrefix(HAS_LOW));
    }

    @Test
    public void testValidateBucketNameRequired() {
        validateBucketNameRequired(PLAIN);
        validateBucketNameRequired(PLAIN.toUpperCase());
        validateBucketNameRequired(HAS_DASH);
        validateBucketNameRequired(HAS_UNDER);
        validateBucketNameRequired("numbers9ok");
        assertThrows(IllegalArgumentException.class, () -> validateBucketNameRequired(null));
        assertThrows(IllegalArgumentException.class, () -> validateBucketNameRequired(HAS_SPACE));
        assertThrows(IllegalArgumentException.class, () -> validateBucketNameRequired(HAS_DOT));
        assertThrows(IllegalArgumentException.class, () -> validateBucketNameRequired(HAS_STAR));
        assertThrows(IllegalArgumentException.class, () -> validateBucketNameRequired(HAS_GT));
        assertThrows(IllegalArgumentException.class, () -> validateBucketNameRequired(HAS_DOLLAR));
        assertThrows(IllegalArgumentException.class, () -> validateBucketNameRequired(HAS_LOW));
        assertThrows(IllegalArgumentException.class, () -> validateBucketNameRequired(HAS_127));
        assertThrows(IllegalArgumentException.class, () -> validateBucketNameRequired("tic`not`allowed"));
    }

    @Test
    public void testValidateMustMatchIfBothSupplied() {
        assertNull(validateMustMatchIfBothSupplied(null, null, "", ""));
        assertEquals("y", validateMustMatchIfBothSupplied(null, "y", "", ""));
        assertEquals("y", validateMustMatchIfBothSupplied("", "y", "", ""));
        assertEquals("x", validateMustMatchIfBothSupplied("x", null, "", ""));
        assertEquals("x", validateMustMatchIfBothSupplied("x", " ", "", ""));
        assertEquals("x", validateMustMatchIfBothSupplied("x", "x", "", ""));
        assertThrows(IllegalArgumentException.class, () -> validateMustMatchIfBothSupplied("x", "y", "", ""));
    }

    @Test
    public void testValidateMaxLength() {
        validateMaxLength("test", 5, true, "label");
        validateMaxLength(null, 5, false, "label");
        assertThrows(IllegalArgumentException.class, () -> validateMaxLength("test", 3, true, "label"));
        assertThrows(IllegalArgumentException.class, () -> validateMaxLength(null, 5, true, "label"));
    }

    @Test
    public void testNotNull() {
        Object o = new Object();
        validateNotNull(o, "fieldName");
        assertThrows(IllegalArgumentException.class, () -> validateNotNull(null, "fieldName"));
    }

    @Test
    public void testZeroOrLtMinus1() {
        assertTrue(zeroOrLtMinus1(0));
        assertTrue(zeroOrLtMinus1(-2));
        assertFalse(zeroOrLtMinus1(1));
        assertFalse(zeroOrLtMinus1(-1));
    }

    @Test
    public void testValidateGtZeroOrMinus1() {
        assertEquals(1, validateGtZeroOrMinus1(1, "test"));
        assertEquals(-1, validateGtZeroOrMinus1(-1, "test"));
        assertThrows(IllegalArgumentException.class, () -> validateGtZeroOrMinus1(0, "test"));
    }

    @Test
    public void testValidateNotNegative() {
        assertEquals(0, validateNotNegative(0, "test"));
        assertEquals(1, validateNotNegative(1, "test"));
        assertThrows(IllegalArgumentException.class, () -> validateNotNegative(-1, "test"));
    }

    @Test
    public void testEmptyAsNull() {
        assertEquals("test", emptyAsNull("test"));
        assertNull(emptyAsNull(null));
        assertNull(emptyAsNull(""));
        assertNull(emptyAsNull(" "));
        assertNull(emptyAsNull("\t"));
    }

    @Test
    public void testEmptyOrNullAs() {
        assertEquals("test", emptyOrNullAs("test", null));
        assertNull(emptyOrNullAs(null, null));
        assertNull(emptyOrNullAs("", null));
        assertNull(emptyOrNullAs(" ", null));
        assertNull(emptyOrNullAs("\t", null));

        assertEquals("test", emptyOrNullAs("test", "as"));
        assertEquals("as", emptyOrNullAs(null, "as"));
        assertEquals("as", emptyOrNullAs("", "as"));
        assertEquals("as", emptyOrNullAs(" ", "as"));
        assertEquals("as", emptyOrNullAs("\t", "as"));
    }

    interface StringTest { String validate(String s, boolean required); }

    private void allowedRequired(StringTest test, List<String> strings) {
        for (String s : strings) {
            assertEquals(s, test.validate(s, true), allowedMessage(s));
        }
    }

    private void allowedNotRequiredEmptyAsNull(StringTest test, List<String> strings) {
        for (String s : strings) {
            assertNull(test.validate(s, false), allowedMessage(s));
        }
    }

    private void notAllowedRequired(StringTest test, List<String> strings) {
        for (String s : strings) {
            assertThrows(IllegalArgumentException.class, () -> test.validate(s, true), notAllowedMessage(s));
        }
    }

    private String allowedMessage(String s) {
        return "Testing [" + s + "] as allowed.";
    }

    private String notAllowedMessage(String s) {
        return "Testing [" + s + "] as not allowed.";
    }
}