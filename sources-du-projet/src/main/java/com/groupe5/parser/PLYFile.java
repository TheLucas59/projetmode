package com.groupe5.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.groupe5.geometry.Face;
import com.groupe5.geometry.Point;

public class PLYFile {
	public File file;
	public String header;
	public ArrayList<Face> faces;
	public ArrayList<Point> points;

	public PLYFile(File file){
		this.file = file;
		header="";
		faces=new ArrayList<Face>();
		points=new ArrayList<Point>();
	}
	
	public File getFile() {
		return this.file;
	}
	
	public String getFormat(){
		return "format";
	}
	
	public int getTotalPoints(){
		return points.size();
	}
	
	public int getTotalFaces(){
		return faces.size();
	}
	
	// returns false if parsing fails
	public boolean parse() {
		Parser p;
		try {
			p = new Parser(this.file);
		} catch (IOException e) {
			return false;
		}
		header = p.getHeader();
		points = p.getPoints();
		faces = p.getFaces(points);
		return true;
	}
	
	public String toString() {
		return this.file.getName();
	}
}
