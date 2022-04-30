//Editied by Remington Crichton - April 29, 2022

package edu.nmsu.cs.circles;

public class Circle2 extends Circle
{

	public Circle2(double x, double y, double radius)
	{
		super(x, y, radius); //Corrected. Swapped x and y. 
	}

	public boolean intersects(Circle other)
	{
		double d;
		d = Math.sqrt(Math.pow(center.x - other.center.x, 2) +
				Math.pow(center.y - other.center.y, 2));
		if (d < radius + other.radius) //Corrected to include other.radius
			return true;
		else
			return false;
	}

}