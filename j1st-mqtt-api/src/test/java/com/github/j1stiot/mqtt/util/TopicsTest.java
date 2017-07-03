package com.github.j1stiot.mqtt.util;

import org.junit.Test;

import java.util.Arrays;

/**
 * MQTT Topic Utils Test
 */
public class TopicsTest {

    @Test(expected = IllegalArgumentException.class)
    public void sanitizeTopicFilterFail00() {
        Topics.sanitizeTopicFilter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sanitizeTopicFilterFail01() {
        Topics.sanitizeTopicFilter("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void sanitizeTopicFilterFail02() {
        Topics.sanitizeTopicFilter("abc/def/g+/h");
    }

    @Test(expected = IllegalArgumentException.class)
    public void sanitizeTopicFilterFail03() {
        Topics.sanitizeTopicFilter("abc/def#/g/h");
    }

    @Test(expected = IllegalArgumentException.class)
    public void sanitizeTopicFilterFail05() {
        Topics.sanitizeTopicFilter("abc/def/g/#/h");
    }

    @Test(expected = IllegalArgumentException.class)
    public void sanitizeTopicFilterFail06() {
        Topics.sanitizeTopicFilter("abc/def/g/#/");
    }

    @Test
    public void sanitizeTopicFilterTest() {
        assert Arrays.equals(Topics.sanitizeTopicFilter("abc/+/g/h").toArray(), new String[]{"abc", "+", "g", "h", Topics.END});
        assert Arrays.equals(Topics.sanitizeTopicFilter("abc/def/g/#").toArray(), new String[]{"abc", "def", "g", "#", Topics.END});
        assert Arrays.equals(Topics.sanitizeTopicFilter("abc/def/#").toArray(), new String[]{"abc", "def", "#", Topics.END});
        assert Arrays.equals(Topics.sanitizeTopicFilter("+/+/g/#").toArray(), new String[]{"+", "+", "g", "#", Topics.END});
        assert Arrays.equals(Topics.sanitizeTopicFilter("/abc/def/g/#").toArray(), new String[]{Topics.EMPTY, "abc", "def", "g", "#", Topics.END});
        assert Arrays.equals(Topics.sanitizeTopicFilter("/abc//def/g/#").toArray(), new String[]{Topics.EMPTY, "abc", Topics.EMPTY, "def", "g", "#", Topics.END});
        assert Arrays.equals(Topics.sanitizeTopicFilter("/abc/+/g/h/").toArray(), new String[]{Topics.EMPTY, "abc", "+", "g", "h", Topics.EMPTY, Topics.END});
    }

    @Test(expected = IllegalArgumentException.class)
    public void sanitizeTopicNameFail00() {
        Topics.sanitizeTopicName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sanitizeTopicNameFail01() {
        Topics.sanitizeTopicName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void sanitizeTopicNameFail02() {
        Topics.sanitizeTopicName("abc/def/g/+/h");
    }

    @Test(expected = IllegalArgumentException.class)
    public void sanitizeTopicNameFail03() {
        Topics.sanitizeTopicName("abc/def/g/h/#");
    }

    @Test
    public void sanitizeTopicNameTest() {
        assert Arrays.equals(Topics.sanitizeTopicName("abc/def/g/h").toArray(), new String[]{"abc", "def", "g", "h", Topics.END});
        assert Arrays.equals(Topics.sanitizeTopicName("/abc/def/g/h").toArray(), new String[]{Topics.EMPTY, "abc", "def", "g", "h", Topics.END});
        assert Arrays.equals(Topics.sanitizeTopicName("/abc/def/g/h/").toArray(), new String[]{Topics.EMPTY, "abc", "def", "g", "h", Topics.EMPTY, Topics.END});
        assert Arrays.equals(Topics.sanitizeTopicName("/abc/def//g/h").toArray(), new String[]{Topics.EMPTY, "abc", "def", Topics.EMPTY, "g", "h", Topics.END});
    }

    @Test
    public void sanitizeTest() {
        assert Arrays.equals(Topics.sanitize("abc/+/g/h").toArray(), new String[]{"abc", "+", "g", "h", Topics.END});
        assert Arrays.equals(Topics.sanitize("abc/def/g/#").toArray(), new String[]{"abc", "def", "g", "#", Topics.END});
        assert Arrays.equals(Topics.sanitize("abc/def/#").toArray(), new String[]{"abc", "def", "#", Topics.END});
        assert Arrays.equals(Topics.sanitize("+/+/g/#").toArray(), new String[]{"+", "+", "g", "#", Topics.END});
        assert Arrays.equals(Topics.sanitize("/abc/def/g/#").toArray(), new String[]{Topics.EMPTY, "abc", "def", "g", "#", Topics.END});
        assert Arrays.equals(Topics.sanitize("/abc//def/g/#").toArray(), new String[]{Topics.EMPTY, "abc", Topics.EMPTY, "def", "g", "#", Topics.END});
        assert Arrays.equals(Topics.sanitize("/abc/+/g/h/").toArray(), new String[]{Topics.EMPTY, "abc", "+", "g", "h", Topics.EMPTY, Topics.END});

        assert Arrays.equals(Topics.sanitize("abc/def/g/h").toArray(), new String[]{"abc", "def", "g", "h", Topics.END});
        assert Arrays.equals(Topics.sanitize("/abc/def/g/h").toArray(), new String[]{Topics.EMPTY, "abc", "def", "g", "h", Topics.END});
        assert Arrays.equals(Topics.sanitize("/abc/def/g/h/").toArray(), new String[]{Topics.EMPTY, "abc", "def", "g", "h", Topics.EMPTY, Topics.END});
        assert Arrays.equals(Topics.sanitize("/abc/def//g/h").toArray(), new String[]{Topics.EMPTY, "abc", "def", Topics.EMPTY, "g", "h", Topics.END});
    }

    @Test
    public void isTopicFilterTest() {
        assert Topics.isTopicFilter(Arrays.asList("abc", "+", "g", "h", Topics.END));
        assert Topics.isTopicFilter(Arrays.asList("abc", "def", "g", "#", Topics.END));
        assert Topics.isTopicFilter(Arrays.asList("abc", "def", "#", Topics.END));
        assert Topics.isTopicFilter(Arrays.asList("+", "+", "g", "#", Topics.END));
        assert !Topics.isTopicFilter(Arrays.asList("abc", "def", "g", "h", Topics.END));
        assert !Topics.isTopicFilter(Arrays.asList(Topics.EMPTY, "abc", "def", "g", "h", Topics.END));
    }

    @Test
    public void antidoteTest() {
        assert Topics.antidote(Arrays.asList(new String[]{"abc", "+", "g", "h", Topics.END})).equals("abc/+/g/h");
        assert Topics.antidote(Arrays.asList(new String[]{"abc", "def", "g", "#", Topics.END})).equals("abc/def/g/#");
        assert Topics.antidote(Arrays.asList(new String[]{"abc", "def", "#", Topics.END})).equals("abc/def/#");
        assert Topics.antidote(Arrays.asList(new String[]{"+", "+", "g", "#", Topics.END})).equals("+/+/g/#");
        assert Topics.antidote(Arrays.asList(new String[]{Topics.EMPTY, "abc", "def", "g", "#", Topics.END})).equals("/abc/def/g/#");
        assert Topics.antidote(Arrays.asList(new String[]{Topics.EMPTY, "abc", Topics.EMPTY, "def", "g", "#", Topics.END})).equals("/abc//def/g/#");
        assert Topics.antidote(Arrays.asList(new String[]{Topics.EMPTY, "abc", "+", "g", "h", Topics.EMPTY, Topics.END})).equals("/abc/+/g/h/");

        assert Topics.antidote(Arrays.asList(new String[]{"abc", "def", "g", "h", Topics.END})).equals("abc/def/g/h");
        assert Topics.antidote(Arrays.asList(new String[]{Topics.EMPTY, "abc", "def", "g", "h", Topics.END})).equals("/abc/def/g/h");
        assert Topics.antidote(Arrays.asList(new String[]{Topics.EMPTY, "abc", "def", "g", "h", Topics.EMPTY, Topics.END})).equals("/abc/def/g/h/");
        assert Topics.antidote(Arrays.asList(new String[]{Topics.EMPTY, "abc", "def", Topics.EMPTY, "g", "h", Topics.END})).equals("/abc/def//g/h");
    }
}
