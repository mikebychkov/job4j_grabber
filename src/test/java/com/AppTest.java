package com;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class AppTest {

    @Test
    public void test() {
        assertThat(1, is(1));
    }

    @Test
    public void test1() {
        App app = new App();
        assertThat(app.foo(17), is(17 << 5));
    }
}