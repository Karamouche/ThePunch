package fr.karamouche.thepunch.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public enum Spawns {
	spawn1("ThePunch", 158, 157, 240),
	spawn2("ThePunch", 137, 157, 239),
	spawn3("ThePunch", 137, 157, 257),
	spawn4("ThePunch", 160, 157, 256),
	spawn5("ThePunch", 149, 157, 247);
	
	private String world;
	private final int X;
	private final int Y;
	private final int Z;
	
	Spawns(String world, int X, int Y, int Z) {
		this.world = world;
		this.X = X;
		this.Y = Y;
		this.Z = Z;
	}

	public String getWorld() {
		return world;
	}

	public int getX() {
		return X;
	}

	public int getY() {
		return Y;
	}

	public int getZ() {
		return Z;
	}
	
	public Location toLocation() {
		return new Location(Bukkit.getWorld(this.getWorld()), this.getX(), this.getY(), this.getZ());
	}
}
