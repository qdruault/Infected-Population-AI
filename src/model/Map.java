package model;

import java.util.ArrayList;

import res.values.Constants;

public class Map {
	private ArrayList<Obstacle> obstacles;
	
	public Map(){
		obstacles = new ArrayList<Obstacle>();
		loadObstacle2();
	}

	public void loadObstacle1(){
		
		int halfSize = Constants.GRID_SIZE/2;
		int quarterSize = Constants.GRID_SIZE/4;
		
		for (int i = (halfSize-quarterSize); i < (halfSize+quarterSize); i++){
			for (int j = (halfSize-quarterSize); j < (halfSize+quarterSize); j++){
				Obstacle o = new Obstacle (i,j);
				obstacles.add(o);
			}
		}
	}
	
	public void loadObstacle2(){
		
		int halfSize = Constants.GRID_SIZE/2;
		int quarterSize = Constants.GRID_SIZE/4;
		int eighthSize = Constants.GRID_SIZE/8;
		
		for (int i = (halfSize-quarterSize); i < (halfSize-eighthSize); i++){
			for (int j = (halfSize-quarterSize); j < (halfSize-eighthSize); j++){
				Obstacle o = new Obstacle (i,j);
				obstacles.add(o);
			}
		}
		
		for (int i = (halfSize+eighthSize); i < (halfSize+quarterSize); i++){
			for (int j = (halfSize+eighthSize); j < (halfSize+quarterSize); j++){
				Obstacle o = new Obstacle (i,j);
				obstacles.add(o);
			}
		}
		
		for (int i = (eighthSize); i < (halfSize-eighthSize); i++){
			for (int j = (halfSize+eighthSize); j < (Constants.GRID_SIZE-1-eighthSize); j++){
				Obstacle o = new Obstacle (i,j);
				obstacles.add(o);
			}
		}
		
		for (int i = (halfSize+eighthSize); i < (Constants.GRID_SIZE-1-eighthSize); i++){
			for (int j = (eighthSize); j < (halfSize-eighthSize); j++){
				Obstacle o = new Obstacle (i,j);
				obstacles.add(o);
			}
		}
	}
	
	public boolean isLocationFree(int x, int y){
		boolean res = true;
		
		for (int i = 0; i < obstacles.size(); i++){
			if (obstacles.get(i).getX() == x && obstacles.get(i).getY() == y){
				res = false;
			}
		}
		
		return res;
	}

	public ArrayList<Obstacle> getObstacles() {
		return obstacles;
	}

	public void setObstacles(ArrayList<Obstacle> obstacles) {
		this.obstacles = obstacles;
	}
	
	
}
