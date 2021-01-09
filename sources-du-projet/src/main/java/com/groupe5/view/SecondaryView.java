package com.groupe5.view;

import java.util.List;
import java.util.logging.Logger;

import com.groupe5.calculation.Translation;
import com.groupe5.geometry.Face;
import com.groupe5.geometry.Modele3D;
import com.groupe5.geometry.Point;
import com.groupe5.observerpattern.Observed;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Class qui gère la fenêtre du Viewer 3D secondaire
 * @author pirca
 * @author plel
 * @author duhayona
 * @author demayl
 */
public class SecondaryView extends Viewer {
	
	@FXML Canvas canvas;
	@FXML Button buttonOpen;
	@FXML Button buttonClose;
	@FXML AnchorPane root;
	@FXML Text loadingString;
	@FXML MenuBar menuBar;
	@FXML Region regionZoom;
	@FXML
	public Slider slideZoom;
	@FXML Text zoomText;
	
	private GraphicsContext gc;
	private Modele3D modele;
	private static SecondaryView instance;
	private Translation center;
	private double oldMousePosX;
	private double oldMousePosY;
	public double oldZoom = 1;
	
	public boolean showLines;
	public boolean showFaces;
	private boolean rotAuto;
	
	public void initialize(){
		// System.out.println("init viewer");
		instance = this;
		
		gc = canvas.getGraphicsContext2D();
		showLines = true;
		showFaces = false;
		
		showFile(PrimaryView.getModele());
	}

	public void showFile(Modele3D modele3d) {
		
		instance = this;
		
		clearScreen();

		canvas.setWidth(ShowScene.getViewer().getWidth());
		canvas.setHeight(ShowScene.getViewer().getHeight()-37);
		
//		ShowScene.getViewer().setTitle("3D Viewer - "+modele3d.getName());
		
		center = new Translation(new Point(((float) canvas.getWidth()/2), ((float) canvas.getHeight()/2), 1, 1));
		
		//ACTIVATION ZOOM AUTO AVEC SLIDER

		slideZoom.setOnMouseDragged(e -> {
			modele.zoom();
			oldZoom = slideZoom.getValue();
		});

		slideZoom.setOnMouseReleased(e -> {
			modele.zoom();
			oldZoom = slideZoom.getValue();
		});

		regionZoom.setOnScroll(scroll -> {
			if(scroll.getDeltaY() > 0) {
				if(slideZoom.getValue() > 2)
					slideZoom.setValue(slideZoom.getValue()*1.05);
				else slideZoom.setValue(slideZoom.getValue() + 0.1);
				modele.zoom();
				oldZoom = slideZoom.getValue();
			}
			
			if(scroll.getDeltaY() < 0) {
				if(slideZoom.getValue() > 2)
					slideZoom.setValue(slideZoom.getValue()*0.95);
				else slideZoom.setValue(slideZoom.getValue() - 0.1);
				modele.zoom();
				oldZoom = slideZoom.getValue();
			}
		});

		regionZoom.setOnMousePressed(e -> {
				setOldAngles(e);
		});
		
		regionZoom.setOnMouseDragged(e -> {
			if(e.getButton().equals(MouseButton.PRIMARY)) {
				modele.rotate(e, oldMousePosX, oldMousePosY);
				setOldAngles(e);
			}
			else if(e.getButton().equals(MouseButton.SECONDARY)) {
				modele.translation(e, oldMousePosX, oldMousePosY);
				setOldAngles(e);
			}
		});
		

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
//				rotAuto = false;
//				Parser p = null;
//				try {
//					p = new Parser(fileToShow);
//				}
//				catch (IOException e) {
//					e.printStackTrace();
//				}
//				
//				ArrayList<Point> points = p.getPoints();
//				objectCenter = setObjectCenter(points);
//				
//				
//				for(Point pt : points) {
//					pt.setX(pt.getX()-objectCenter.getX());
//					pt.setY(pt.getY()-objectCenter.getY());
//					pt.setZ(pt.getZ()-objectCenter.getZ());
//				}
//				
//				ArrayList<Face> faces = new ArrayList<>();
//				try {
//					faces = p.getFaces(points);
//				} catch (Exception e) {
//					FileChooser.showAlert("Exception", "Error Message", e.getMessage());
//				}
//				
//				slideZoom.setValue(1);
//				oldZoom = 1;
//				modele = PrimaryView.getModele();
//				modele.newView(instance);
//				
//				boolean onCenter = false;
//				double vZoom = 10050;
//				while(!onCenter) {
//					onCenter = true;
//					
//					if(vZoom > 50) vZoom -= 50;
//					else if(vZoom > 10) vZoom -= 10;
//					else vZoom -= 0.1;
//					
//					Matrix tmp = modele.getPoints();
//					
//					Homothety h2 = new Homothety(vZoom);		
//					Matrix removeCenter = new Matrix(getCenter().multiply(h2.multiply(tmp)));
//					
//					for(double d : removeCenter.getLineX()) {
//						if(d < 17 || d > canvas.getWidth()) {
//							onCenter = false;
//						}
//					}
//					
//					for(double d : removeCenter.getLineY()) {
//						if(d < 17 || d > canvas.getHeight()) {
//							onCenter = false;
//						}
//					}
//				}
//				
//				slideZoom.setMax(vZoom);
//				slideZoom.setValue(vZoom);
//				
//				RotationZ r = new RotationZ(180);
//				modele.getPoints().setMatrix(r.multiply(modele.getPoints()));
//				modele.getPoints().setMatrix(center.multiply(modele.getPoints()));
//				modele.zoom();
//				oldZoom = slideZoom.getValue();
				
				if(modele == null)
					modele = PrimaryView.getModele();
				else
					modele = modele3d;
				modele.newView(instance);
				clearScreen();
				modele.notifyObservers();
			}
		});
		
		thread.setDaemon(true);
		thread.start();
	}
	
	private void setOldAngles(MouseEvent e) {
		oldMousePosX = e.getSceneX();
		oldMousePosY = e.getSceneY();
	}

	@Override
	public Translation getCenter() {
		return center;
	}

	@Override
	public void clearScreen() {
		gc.setFill(Color.rgb(153, 170, 181));
		gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
	}
	
	@Override
	public Text getZoomText() {
		return zoomText;
	}
	
	public void updateShowLines() {
		showLines = !showLines;
		drawObject(modele.getFaces(), showLines, showFaces);
	}
	
	public void updateShowFaces() {
		showFaces = !showFaces;
		drawObject(modele.getFaces(), showLines, showFaces);
	}
	
	public void rotAuto() {
		rotAuto = !rotAuto;
		String action;
		
		if(rotAuto) action = "run";
		else action = "stop";
		
		modele.rotationAuto(modele, action);
	}
	
	public void drawObject(List<Face> faces, boolean showLines, boolean showFaces) {	
		clearScreen();
		
		modele.sortFaces();
		gc.setStroke(Color.GRAY);
		gc.setLineWidth(0.5);
		gc.setFill(Color.LIGHTGRAY);
		
		for(Face f : faces) {
			double[] pointsX = getCoordsX(f);
			double[] pointsY = getCoordsY(f);
			if(showFaces) {
				float eclairage = modele.eclairageFace(f);
				gc.fillPolygon(pointsX, pointsY, f.getNbPoints()-1);
				
				if(eclairage != -1) {
					gc.setFill(Color.rgb(Math.round(255*eclairage), Math.round(255*eclairage), Math.round(255*eclairage)));
				}
			}
			if(showLines)
				gc.strokePolygon(pointsX, pointsY, f.getNbPoints()-1);
		}
	}
	
	private double[] getCoordsX(Face f) {
		double[] result = new double[f.getNbPoints()-1];
		int j = 0;
		for(Integer i : f.getPoints()) {
			if(j != 0) {
				result[j-1] = modele.getPoints().getColumn(i)[0];
			}
			j++;
		}
		return result;
	}

	private double[] getCoordsY(Face f) {
		double[] result = new double[f.getNbPoints()];
		int j = 0;
		for(Integer i : f.getPoints()) {
			if(j != 0) {
				result[j-1] = modele.getPoints().getColumn(i)[1];
			}
			j++;
		}
		return result;
	}
	
	public double getOldZoom() {
		return oldZoom;
	}
	
	public Slider getSlideZoom() {
		return slideZoom;
	}

	@Override
	public void update(Observed observed) {
		drawObject(modele.getFaces(), showLines, showFaces);
	}

	@Override
	public void update(Observed observed, Object data) {
		if(((String) data).equals("file")) {
			Logger.getAnonymousLogger().info("update data 2");
			showFile(PrimaryView.getModele());	
		}
	}
}
