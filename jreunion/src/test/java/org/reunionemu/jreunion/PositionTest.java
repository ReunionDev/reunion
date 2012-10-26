package org.reunionemu.jreunion;

import static org.junit.Assert.*;

import org.junit.Test;
import org.reunionemu.jreunion.events.Event;
import org.reunionemu.jreunion.game.Position;
import org.reunionemu.jreunion.server.*;

public class PositionTest {

	Map map1 = new MockMap(1);
	Map map2 = new MockMap(2);
	LocalMap localMap1 = new LocalMap(1);
	LocalMap localMap2 = new LocalMap(2);

	@Test
	public void testPosition() {
		Position p = new Position();
		assertEquals(0, p.getX());
		assertEquals(0, p.getY());
		assertEquals(0, p.getZ());
		assertEquals(0, p.getRotation(), 0);
		assertNull(p.getMap());
	}

	@Test
	public void testPositionIntIntIntMapDouble() {
		int x = 1, y = 2, z = 3;
		double rotation = Math.PI;

		Position p = new Position(x, y, z, map1, rotation);
		assertEquals(x, p.getX());
		assertEquals(y, p.getY());
		assertEquals(z, p.getZ());
		assertEquals(rotation, p.getRotation(), 0);
		assertSame(map1, p.getMap());
	}

	@Test
	public void testClone() {

		Position p1 = new Position(1, 2, 3, map1, Math.PI);
		Position p2 = p1.clone();
		assertEquals(p1, p2);
		assertNotSame(p1, p2);
		assertSame(p1.getMap(), p2.getMap());
	}

	@Test(expected = RuntimeException.class)
	public void testDistanceOnDifferentMaps() {

		Position p1 = new Position(0, 0, 0, map1, 0);
		Position p2 = new Position(0, 0, 0, map2, 0);
		p1.distance(p2);
	}

	@Test(expected = RuntimeException.class)
	public void testDistanceOnDifferentMapsNull1() {

		Position p1 = new Position(0, 0, 0, map1, 0);
		Position p2 = new Position(0, 0, 0, null, 0);
		p1.distance(p2);
	}

	@Test(expected = RuntimeException.class)
	public void testDistanceOnDifferentMapsNull2() {

		Position p1 = new Position(0, 0, 0, null, 0);
		Position p2 = new Position(0, 0, 0, map1, 0);
		p1.distance(p2);
	}

	@Test
	public void testDistance() {

		Position p1 = new Position(0, 0, 0, map1, 0);
		assertEquals(10, p1.distance(p1.setX(10)), 0);
		assertEquals(10, p1.distance(p1.setX(-10)), 0);
		assertEquals(Math.sqrt((10 * 10) + (10 * 10)),
				p1.distance(p1.setX(10).setY(10)), 0);
		assertEquals(Math.sqrt((10 * 10) + (10 * 10)),
				p1.distance(p1.setX(-10).setY(-10)), 0);
		assertEquals(Math.sqrt((10 * 10) + (10 * 10) + (10 * 10)),
				p1.distance(p1.setX(10).setY(10).setZ(10)), 0);
		assertEquals(Math.sqrt((10 * 10) + (10 * 10) + (10 * 10)),
				p1.distance(p1.setX(10).setY(-10).setZ(10)), 0);
	}

	@Test
	public void testWithin() {
		Position p1 = new Position(0, 0, 0, map1, 0);
		assertTrue(p1.within(p1, 0.1));
		assertFalse(p1.within(p1.setX(10).setY(10), 1));
		assertTrue("Inclusivity check fails", p1.within(p1, 0));
		assertTrue(p1.within(p1.setX(10), 10));
		assertTrue(p1.within(p1.setX(-10), 10));

		assertTrue(p1.within(p1.setX(10).setY(10), 15));
		assertFalse(p1.within(p1.setX(10).setY(10), 14));

		assertTrue(p1.within(p1.setX(10).setY(10).setZ(10), 18));
		assertFalse(p1.within(p1.setX(10).setY(10).setZ(10), 17));

	}


	@Test
	public void testGetX() {
		int x = 1, y = 2, z = 3;
		double rotation = Math.PI;
		Position p = new Position(x, y, z, map1, rotation);
		assertEquals(x, p.getX());
	}

	@Test
	public void testSetX() {
		int x = 1, y = 2, z = 3;
		double rotation = Math.PI;
		Position p1 = new Position(x, y, z, map1, rotation);
		assertEquals(x, p1.getX());
		Position p2 = p1.setX(0);
		assertEquals(x, p1.getX());
		assertEquals(0, p2.getX());
	}

	@Test
	public void testGetY() {
		int x = 1, y = 2, z = 3;
		double rotation = Math.PI;
		Position p = new Position(x, y, z, map1, rotation);
		assertEquals(y, p.getY());
	}

	@Test
	public void testSetY() {
		int x = 1, y = 2, z = 3;
		double rotation = Math.PI;
		Position p1 = new Position(x, y, z, map1, rotation);
		assertEquals(y, p1.getY());
		Position p2 = p1.setY(0);
		assertEquals(y, p1.getY());
		assertEquals(0, p2.getY());
	}

	@Test
	public void testGetZ() {
		int x = 1, y = 2, z = 3;
		double rotation = Math.PI;
		Position p = new Position(x, y, z, map1, rotation);
		assertEquals(z, p.getZ());
	}

	@Test
	public void testSetZ() {
		int x = 1, y = 2, z = 3;
		double rotation = Math.PI;
		Position p1 = new Position(x, y, z, map1, rotation);
		assertEquals(z, p1.getZ());
		Position p2 = p1.setZ(0);
		assertEquals(z, p1.getZ());
		assertEquals(0, p2.getZ());
	}

	@Test(expected=Exception.class)
	public void testGetLocalMapFailing() {
		int x = 1, y = 2, z = 3;
		double rotation = Math.PI;
		Position p1 = new Position(x, y, z, map1, rotation);
		p1.getLocalMap();
	}
	public void testGetLocalMap() {
		int x = 1, y = 2, z = 3;
		double rotation = Math.PI;
		Position p1 = new Position(x, y, z, localMap1, rotation);
		LocalMap map = p1.getLocalMap();
		assertNotNull(map);
		assertSame(localMap1, map);
	}

	@Test
	public void testGetMap() {
		int x = 1, y = 2, z = 3;
		double rotation = Math.PI;
		Position p = new Position(x, y, z, map1, rotation);
		assertSame(map1, p.getMap());
	}

	@Test
	public void testSetMap() {
		int x = 1, y = 2, z = 3;
		double rotation = Math.PI;
		Position p1 = new Position(x, y, z, map1, rotation);
		assertSame(map1, p1.getMap());
		Position p2 = p1.setMap(map2);
		assertSame(map1, p1.getMap());
		assertSame(map2, p2.getMap());
		assertNull(p1.setMap(null).getMap());

	}

	@Test
	public void testGetRotation() {
		int x = 1, y = 2, z = 3;
		double rotation = Math.PI;
		Position p = new Position(x, y, z, map1, rotation);
		assertEquals(rotation, p.getRotation(), 0);
	}

	@Test
	public void testSetRotation() {
		int x = 1, y = 2, z = 3;
		double rotation = Math.PI;
		Position p1 = new Position(x, y, z, map1, rotation);
		assertEquals(rotation, p1.getRotation(),0);
		Position p2 = p1.setRotation(0);
		assertEquals(rotation, p1.getRotation(),0);
		assertEquals(0, p2.getRotation(),0);
	}

	@Test
	public void testHashCode() {
		int x = 1, y = 2, z = 3;
		double rotation = Math.PI;
		Position p1 = new Position(x, y, z, map1, rotation);
		Position p2 = new Position(x, y, z, map1, rotation);

		assertEquals(p1.hashCode(), p2.hashCode());
		assertNotEquals(0, p1.hashCode());
		assertNotEquals(p1.hashCode(), p2.setMap(null).hashCode());
		assertEquals(p1.setMap(null).hashCode(), p2.setMap(null).hashCode());
		assertNotEquals(p1.hashCode(), p1.setX(0).hashCode());
		assertNotEquals(p1.hashCode(), p1.setY(0).hashCode());
		assertNotEquals(p1.hashCode(), p1.setZ(0).hashCode());
		assertNotEquals(p1.hashCode(), p1.setRotation(0).hashCode());
	}

	@Test
	public void testEquals() {

		int x = 1, y = 2, z = 3;
		double rotation = Math.PI;

		Position p1 = new Position(x, y, z, map1, rotation);
		Position p2 = new Position(x, y, z, map1, rotation);
		assertEquals(p1, p2);
		assertNotEquals(p1, p2.setMap(null));
		assertEquals(p1.setMap(null), p2.setMap(null));
		assertNotEquals(p1, p1.setX(0));
		assertNotEquals(p1, p1.setY(0));
		assertNotEquals(p1, p1.setZ(0));
		assertNotEquals(p1, p1.setRotation(0));

	}
	private static class MockMap extends Map {

		public MockMap(int id) {
			
			super(id);
		}

		@Override
		public void handleEvent(Event event) {

		}

	}

	private static class MockLocalMap extends LocalMap {

		public MockLocalMap(int id) {
			super(id);
		}

		@Override
		public void handleEvent(Event event) {

		}

	}

}
