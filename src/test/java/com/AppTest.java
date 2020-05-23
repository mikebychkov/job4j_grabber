package com;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class AppTest {

    @Test
    public void test() {
        assertThat(1, is(1));
    }
}