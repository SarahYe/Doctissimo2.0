
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;

import javafx.application.Application;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class IHMFX extends Application {

	public Image img;
	private ArrayList<Text> texts;
	private ArrayList<TextField> searchBars;
	private static Stage primaryStage;
	private ArrayList<ArrayList<String>> result;
	
	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) {
		result=new ArrayList<ArrayList<String>>();
		img=new Image("file:sprites/background.png");
		this.primaryStage=primaryStage;

		texts=new ArrayList<Text>();
		searchBars=new ArrayList<TextField>();
		Rectangle2D primaryScreenBounds = new Rectangle2D(1280, 720,1280,  720);
		Group root = new Group();
		final VBox results = new VBox();
		results.setFillWidth(false);
		results.setSpacing(Screen.getPrimary().getVisualBounds().getHeight() * 1/12);
		results.setBorder(new Border(new BorderStroke[0]));
		Scene scene = new Scene(root, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight(),Color.ANTIQUEWHITE);

		Button button = new Button();
		final TextField question = new TextField();

		final Text disease = new Text();
		final Text sideEffects= new Text();
		final Text medicine = new Text();
		final Text labelDisease = new Text();
		final Text labelSideEffects= new Text();
		final Text labelMedicine = new Text();


		double barX=primaryScreenBounds.getWidth()*70/100;
		double barY=primaryScreenBounds.getHeight()*5/100;
		final double buttonSizeX = primaryScreenBounds.getWidth() * 1 / 10;
		final double buttonSizeY = primaryScreenBounds.getHeight() * 1 / 15; 
		
		



		//Properties of the texts written

		disease.setText("");
		disease.setWrappingWidth(primaryScreenBounds.getWidth()/3-15);
		ScrollPane pane = new ScrollPane(disease);
		pane.setLayoutX(20);
		pane.setLayoutY(primaryScreenBounds.getHeight()*2/10);
		pane.setMaxHeight(450);
		pane.setMaxWidth(400);
		pane.setHbarPolicy(ScrollBarPolicy.NEVER);
        pane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		pane.setContent(disease);
		
		sideEffects.setText("");
		sideEffects.setWrappingWidth(primaryScreenBounds.getWidth()/3-15);
		ScrollPane pane3 = new ScrollPane(sideEffects);
		pane3.setLayoutX(2*primaryScreenBounds.getWidth()/3+20);
		pane3.setLayoutY(primaryScreenBounds.getHeight()*2/10);
		pane3.setMaxHeight(450);
		pane3.setMaxWidth(400);
		pane3.setHbarPolicy(ScrollBarPolicy.NEVER);
        pane3.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		pane3.setContent(sideEffects);
		
		medicine.setText("");
		medicine.setWrappingWidth(primaryScreenBounds.getWidth()/3-15);
		ScrollPane pane2 = new ScrollPane(medicine);
		pane2.setLayoutX(20+primaryScreenBounds.getWidth()/3);
		pane2.setLayoutY(primaryScreenBounds.getHeight()*2/10);
		pane2.setMaxHeight(450);
		pane2.setMaxWidth(400);
		pane2.setHbarPolicy(ScrollBarPolicy.NEVER);
        pane2.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		pane2.setContent(medicine);
		
		
		labelDisease.setText("Possible disease");
		labelDisease.setFont(Font.font("Muli",FontWeight.BOLD , 25));
		labelDisease.setLayoutX(primaryScreenBounds.getWidth()/6-sideEffects.getWrappingWidth()/4);
		labelDisease.setLayoutY(40+primaryScreenBounds.getHeight()/10);
		
		
		labelSideEffects.setText("Medicine to cure");
		labelSideEffects.setFont(Font.font("Muli",FontWeight.BOLD , 25));
		labelSideEffects.setLayoutX(primaryScreenBounds.getWidth()/2-sideEffects.getWrappingWidth()/4);
		labelSideEffects.setLayoutY(primaryScreenBounds.getHeight()/10+40);
		
		labelMedicine.setText("Side Effects");
		labelMedicine.setFont(Font.font("Muli",FontWeight.BOLD , 25));
		labelMedicine.setLayoutX(5*primaryScreenBounds.getWidth()/6-sideEffects.getWrappingWidth()/5);
		labelMedicine.setLayoutY(primaryScreenBounds.getHeight()/10+40);
		
		//Properties of searchbars and button
		question.setLayoutX(0);
		question.setText("Enter a symptom");
		question.setLayoutY(primaryScreenBounds.getHeight()-barY);
		question.setPrefSize(primaryScreenBounds.getWidth()-buttonSizeX, barY - 15);

		button.setLayoutX(primaryScreenBounds.getWidth()-buttonSizeX);
		button.setLayoutY(primaryScreenBounds.getHeight()-barY);
		button.setPrefSize(buttonSizeX, barY-15);
		button.setText("Search");
		
		button.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				System.out.println("button used");
				disease.setText("");
				medicine.setText("");
				sideEffects.setText("");
				try {
					result=Main.doc(question.getText());
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				String txt="";
				for(int i=0;i<result.get(0).size();i++){
					txt+= ""+result.get(0).get(i)+"\n**********************************************************************************\n";
				}
				disease.setText(txt);
				txt="";
				for(int i=0;i<result.get(1).size();i++){
					txt+= ""+result.get(1).get(i)+"\n**********************************************************************************\n";
				}
				medicine.setText(txt);
				txt="";
				for(int i=0;i<result.get(2).size();i++){
					txt+= ""+result.get(2).get(i)+"\n**********************************************************************************\n";
				}
				sideEffects.setText(txt);
			}
		});


		ImageView image=new ImageView();
		image.setImage(img);
		image.setLayoutX(0);
		image.setLayoutY(0);
		
		
		searchBars.add(question);

		texts.add(medicine);
		texts.add(sideEffects);
		texts.add(disease);
		texts.add(labelMedicine);
		texts.add(labelSideEffects);
		texts.add(labelDisease);
		
		root.getChildren().add(image);
		root.getChildren().add(pane);
		root.getChildren().add(pane2);
		root.getChildren().add(pane3);
		root.getChildren().add(button);
		root.getChildren().addAll(searchBars);
		root.getChildren().addAll(texts);
		Group contentGroup = new Group();
		//root.getChildren().add(image);
		contentGroup.getChildren().add(results);


		primaryStage.setScene(scene);
		//primaryStage.setMaximized(true);
		primaryStage.show();
	}

}