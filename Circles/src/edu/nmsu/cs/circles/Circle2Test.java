package edu.nmsu.cs.circles;

/***
 * Example JUnit testing class for Circle1 (and Circle)
 *
 * - must have your classpath set to include the JUnit jarfiles - to run the test do: java
 * org.junit.runner.JUnitCore Circle1Test - note that the commented out main is another way to run
 * tests - note that normally you would not have print statements in a JUnit testing class; they are
 * here just so you see what is happening. You should not have them in your test cases.
 ***/

import org.junit.*;

public class Circle2Test
{
	// Data you need for each test case
	private Circle2 circle2;

	//
	// Stuff you want to do before each test case
	//
	@Before
	public void setup()
	{
		System.out.println("\nTest starting...");
		circle2 = new Circle2(1, 2, 3);
	}

	//
	// Stuff you want to do after each test case
	//
	@After
	public void teardown()
	{
		System.out.println("\nTest finished.");
	}

	/**
	****************************************
	***** TESTING CIRCLE2 INTERSECTION *****
	****************************************
	*/

	//Circles do not intersect
	@Test
	public void Intersect_false(){ 
		System.out.println( "Running Test: Intersect_false" ); 

		//Far away: 
		Circle2 primaryCircle = new Circle2( 0, 100, 5 ); 
		Circle2 secondaryCircle = new Circle2( 0, 0, 2 ); 
		Assert.assertFalse( primaryCircle.intersects( secondaryCircle ) ); 
		Assert.assertFalse( secondaryCircle.intersects( primaryCircle ) ); 

		//Really Close: 
		primaryCircle = new Circle2( 0, 15.00001, 5 ); 
		secondaryCircle = new Circle2( 0, 0, 10 ); 
		Assert.assertFalse( primaryCircle.intersects( secondaryCircle ) ); 
		Assert.assertFalse( secondaryCircle.intersects( primaryCircle ) ); 

		//Really Close - Diagonal: 
		primaryCircle = new Circle2( 0, 0, 1 ); 
		secondaryCircle = new Circle2( 2, 1, 1 ); 
		Assert.assertFalse( primaryCircle.intersects( secondaryCircle ) ); 
		Assert.assertFalse( secondaryCircle.intersects( primaryCircle ) ); 
	} //end test

	//Circles intersect - Secondary circle shifted along x axis. 
	@Test
	public void Intersect_true_xshift(){ 
		System.out.println( "Intersect_true_xshift" ); 

		Circle2 primaryCircle = new Circle2( 0, 0, 3 ); 
		Circle2 secondaryCircle = new Circle2( 5, 0, 4 ); 
		Assert.assertTrue( primaryCircle.intersects( secondaryCircle ) ); 
		Assert.assertTrue( secondaryCircle.intersects( primaryCircle ) ); 
	} //end test

	//Circles intersect - Secondary circle shifted along y axis. 
	@Test
	public void Intersect_true_yshift(){ 
		System.out.println( "Intersect_true_yshift" ); 

		Circle2 primaryCircle = new Circle2( 0, 0, 3 ); 
		Circle2 secondaryCircle = new Circle2( 0, 5, 4 ); 
		Assert.assertTrue( primaryCircle.intersects( secondaryCircle ) ); 
		Assert.assertTrue( secondaryCircle.intersects( primaryCircle ) ); 
	}

	//Circles intersect - Secondary circle shifted both in x and y axis.
	@Test
	public void Intersect_true_diagnoalshift(){ 
		System.out.println( "Intersect_true_diagnoalshift" ); 

		Circle2 primaryCircle = new Circle2( 0, 0, 3 ); 
		Circle2 secondaryCircle = new Circle2( 4, 3, 3 ); 
		Assert.assertTrue( primaryCircle.intersects( secondaryCircle ) ); 
		Assert.assertTrue( secondaryCircle.intersects( primaryCircle ) ); 
	}

	//Circles intersect - Circles overlap perfectly (Same size and location)
	@Test
	public void Intersect_true_samesize_samelocation(){ 
		System.out.println( "Running Test: Intersect_true_samesize_samelocation" ); 

		Circle2 primaryCircle = new Circle2( 0, 0, 5 ); 
		Circle2 secondaryCircle = new Circle2( 0, 0, 5 ); 
		Assert.assertTrue( primaryCircle.intersects( secondaryCircle ) ); 
		Assert.assertTrue( secondaryCircle.intersects( primaryCircle ) ); 
	} //end test

	/**
	**************************************
	***** TESTING CIRCLE2 SIMPLEMOVE *****
	**************************************
	*/

	// Test a simple positive move
	@Test
	public void simpleMove()
	{
		Point p;
		System.out.println("Running test simpleMovePos.");
		p = circle2.moveBy(1, 1);
		Assert.assertTrue(p.x == 2 && p.y == 3);
	} //end test

	// Test a simple negative move
	@Test
	public void simpleMoveNeg()
	{
		Point p;
		System.out.println("Running test simpleMoveNeg.");
		p = circle2.moveBy(-1, -1);
		Assert.assertTrue(p.x == 0 && p.y == 1);
	} //end test

	/***
	 * NOT USED public static void main(String args[]) { try { org.junit.runner.JUnitCore.runClasses(
	 * java.lang.Class.forName("Circle1Test")); } catch (Exception e) { System.out.println("Exception:
	 * " + e); } }
	 ***/

}
