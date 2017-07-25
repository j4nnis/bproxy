package org.zaproxy.zap.control;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ZapReleaseUnitTest {

	private static final String DEV_BUILD = "Dev Build";

	@Test
	public void testDevBuildLaterThan1_4_1() {
		ZapRelease rel = new ZapRelease();
		rel.setVersion(DEV_BUILD);
		assertTrue(rel.isNewerThan("1.4.1"));
	}

	@Test
	public void test1_4_2LaterThan1_4_1() {
		ZapRelease rel = new ZapRelease();
		rel.setVersion("1.4.2");
		assertTrue(rel.isNewerThan("1.4.1"));
	}

	@Test
	public void test1_5_1LaterThan1_4_2() {
		ZapRelease rel = new ZapRelease();
		rel.setVersion("1.5.1");
		assertTrue(rel.isNewerThan("1.4.2"));
	}

	@Test
	public void test1_5_1LaterThan1_4_2_1() {
		ZapRelease rel = new ZapRelease();
		rel.setVersion("1.5.1");
		assertTrue(rel.isNewerThan("1.4.2.1"));
	}

	@Test
	public void testLots() {
		// Imported from old CheckForUpdates code
		assertFalse(new ZapRelease("1.3.4").isNewerThan("1.4"));
		assertFalse(new ZapRelease("1.3.4").isNewerThan("1.4"));
		assertFalse(new ZapRelease("1.3.4").isNewerThan("2.0"));
		assertFalse(new ZapRelease("1.4").isNewerThan("1.4.1"));
		assertFalse(new ZapRelease("1.4.1").isNewerThan("1.4.2"));
		assertFalse(new ZapRelease("1.4.2").isNewerThan("1.4.11"));
		// Dont support this right now
		//assertFalse(new ZapRelease("1.4.alpha.1").isNewerThan("1.4"));
		//assertFalse(new ZapRelease("1.4.alpha.1").isNewerThan("1.4.1"));
		assertFalse(new ZapRelease("1.4.beta.1").isNewerThan("1.5"));
		assertFalse(new ZapRelease("D-2012-08-01").isNewerThan("D-2012-08-02"));
		assertFalse(new ZapRelease("D-2012-01-01").isNewerThan("D-2013-10-10"));
		assertFalse(new ZapRelease("1.4").isNewerThan("1.4"));
		
		assertTrue(new ZapRelease("1.4").isNewerThan("1.3.4"));
		assertTrue(new ZapRelease("1.4.2").isNewerThan("1.4.1"));
		assertTrue(new ZapRelease("1.4.20").isNewerThan("1.4.11"));
		assertTrue(new ZapRelease("1.4.alpha.1").isNewerThan("1.3.4"));
		// Dont support this right now
		//assertTrue(new ZapRelease("1.4").isNewerThan("1.4.alpha.1"));
		assertTrue(new ZapRelease("Dev Build").isNewerThan("1.5"));
		assertTrue(new ZapRelease("D-2012-08-02").isNewerThan("D-2012-08-01"));
		assertTrue(new ZapRelease("D-2013-10-10").isNewerThan("D-2012-01-01"));
		assertTrue(new ZapRelease("D-2013-01-01").isNewerThan("D-2012-12-31"));
		assertTrue(new ZapRelease("D-2013-01-07").isNewerThan("D-2012-12-31"));
	}
}
