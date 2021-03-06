package com.groupe5.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;

import com.groupe5.geometry.Face;
import com.groupe5.geometry.Point;
/**
 * Parse un fichier PLY
 * @author demayl
 *
 */
public class Parser {
	String header;
	String faces;
	String points;

	List<Integer> idPoints;
	
	/**
	 * Crée un objet de Parser avec le fichier passé en paramètre et le parse
	 * @param file Fichier a parser
	 * @throws IOException Erreur de lecture
	 */
	public Parser(File file) throws IOException{
		parse(file);
	}
	
	/**
	 * Parse toute les catégories du fichier
	 * @param file Fichier a parse
	 * @throws IOException Erreur de lecture
	 */
	public void parse(File file) throws IOException {
		String everything = readFile(file);
		Scanner s = new Scanner(everything);
		StringBuilder currentStack = new StringBuilder();
		String line;
		Integer vertexCount = null;
		Integer faceCount = null;
		// parse header
		while(s.hasNext()){
			line = s.nextLine();
			currentStack.append(line).append("\n");
			if(line.startsWith("element vertex")) {
				vertexCount = Integer.parseInt(line.split(" ")[2]);				
			}
			if(line.startsWith("element face")) {
				faceCount = Integer.parseInt(line.split(" ")[2]);
			}
			if(line.startsWith("end_header")) {
				// we are at the end of the header; set it and break
				header = currentStack.toString();
				break;
			}
		}
		currentStack = new StringBuilder();
		
		if(vertexCount == null || faceCount == null) {
			Logger.getAnonymousLogger().severe("ERROR PARSING THE MODEL : Incorrect header");
			s.close();
			return;
		}
		
		// parse points
		for(int i = 0; i < vertexCount; i++){
			currentStack.append(s.nextLine()).append("\n");
		}
		points = currentStack.toString();
		currentStack = new StringBuilder();
		
		// parse faces
		for(int i = 0; i < faceCount; i++){
			currentStack.append(s.nextLine()).append("\n");
		}
		faces = currentStack.toString();
		s.close();
	}

	/**
	 * Renvoie le header
	 * @return String header
	 */
	public String getHeader() {
		return header;
	}
	
	/**
	 * Parse les points et retourne une liste de points
	 * @return ArrayList des points
	 */
	public List<Point> getPoints() {
		if(this.points == null) {
			return null;
		}
		ArrayList<Point> r = new ArrayList<>();
		Scanner s = new Scanner(this.points);
		int currentLine = -1;
		while(s.hasNext()){
			currentLine++;
			String line = s.nextLine();
			String[] split = line.split(" ");
			if(split.length == 3) {
				r.add(new Point(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]), currentLine));
			}
		}
		s.close();
		return r;
	}

	/**
	 * Parse les points et retourne une liste de faces
	 * @param points Liste des points
	 * @return ArrayList des faces
	 * @throws NumberFormatException Face non reconnue
	 * @throws NoSuchElementException Scanner qui demande une ressource invalide
	 */
	public List<Face> getFaces(List<Point> points) throws NumberFormatException, NoSuchElementException {
		if(points == null || this.faces == null) {
			return null;
		}
		ArrayList<Face> f = new ArrayList<>();


		Scanner s = new Scanner(this.faces);
		while(s.hasNext()){
			try {
				idPoints = new ArrayList<>();
				String line = s.nextLine();
				String[] split = line.split(" ");
				int faceSize = Integer.parseInt(split[0]);
				for(int i = 0; i <= faceSize; i++) {
					int idx = Integer.parseInt(split[i]);
					idPoints.add(idx);
				}
				f.add(new Face(idPoints));
			} catch(NumberFormatException | NoSuchElementException e) {
				s.close();
				throw e;
			}
		}
		s.close();
		return f;
	}
	
	/**
	 * Lis le fichier passé en paramètre et renvoie l'entièreté du fichier dans un String
	 * @param file Fichier a parser
	 * @return contenu du fichier dans un String
	 * @throws IOException Erreur de lecture
	 */
	public String readFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line).append("\n");
		        line = br.readLine();
		    }
		    return sb.toString();
		} finally {
		    br.close();
		}
	}
}
