package com.course_sched;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;



public class View extends Application
{
    static Stage stage;
    static Scene scene;
    AnchorPane root1,root2;

    private double sceneWidth = 600;
    private double sceneHeight = 400;

    private int n =8;
    private int m =5;

    double gridWidth = sceneWidth / n;
    double gridHeight = sceneHeight / m;

    static int lg=0;
    static int a=32,b=224,c=377;

    TextField course_text[] = new TextField[Model.MAX_COURSES];
    TextField enrol_text[] = new TextField[Model.MAX_COURSES];
    TextField pref_text[] = new TextField[Model.MAX_COURSES];

    public void startView()
    {
        Application.launch();
    }

    public void start(Stage p1)
    {
        try {
            stage = new Stage();
            root1 = FXMLLoader.load((getClass().getResource("f1.fxml")));
            Button btn = new Button("Add");
            btn.setLayoutX(660);
            btn.setLayoutY(655);
            root1.getChildren().add(btn);
            course_text[lg] = new TextField();
            course_text[lg].setLayoutX(a);
            course_text[lg].setLayoutY(71 + lg * 71 / 2);
            course_text[lg].setPrefWidth(80);

            enrol_text[lg] = new TextField();
            enrol_text[lg].setLayoutX(b);
            enrol_text[lg].setLayoutY(71 + lg * 71 / 2);
            enrol_text[lg].setPrefWidth(80);

            pref_text[lg] = new TextField();
            pref_text[lg].setLayoutX(c);
            pref_text[lg].setLayoutY(71 + lg * 71 / 2);
            pref_text[lg].setPrefWidth(190);

            root1.getChildren().add(course_text[lg]);
            root1.getChildren().add(enrol_text[lg]);
            root1.getChildren().add(pref_text[lg]);
            lg++;
            btn.setOnAction(e -> {
                course_text[lg] = new TextField();
                course_text[lg].setLayoutX(a);
                course_text[lg].setLayoutY(71 + (lg%15) * 71 / 2);
                course_text[lg].setPrefWidth(80);

                enrol_text[lg] = new TextField();
                enrol_text[lg].setLayoutX(b);
                enrol_text[lg].setLayoutY(71 + (lg%15) * 71 / 2);
                enrol_text[lg].setPrefWidth(80);

                pref_text[lg] = new TextField();
                pref_text[lg].setLayoutX(c);
                pref_text[lg].setLayoutY(71 + (lg%15) * 71 / 2);
                pref_text[lg].setPrefWidth(190);

                root1.getChildren().add(course_text[lg]);
                root1.getChildren().add(enrol_text[lg]);
                root1.getChildren().add(pref_text[lg]);
                lg++;
                if(lg==15)
                {
                    a+=600;
                    b+=600;
                    c+=600;
                }

            });
            stage.setTitle("Course Scheduling");
            scene = new Scene(root1, 1366,768);
            stage.setScene(scene);
            stage.show();
            Controller.view=this;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void changeView2()
    {
        try {
            stage.close();
            root2 = FXMLLoader.load((getClass().getResource("f2.fxml")));
            scene = new Scene(root2, 600, 400);
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    public void output(Model model)
    {
        stage.close();
        Group g1=new Group();
        Group g2=new Group();
        String t1[][]=new String[model.TotRooms+1][model.TotTimes+1];
        t1[0][0]=" ";
        System.out.print("\t\t");
        for(int i=0;i<model.TotTimes;i++) {
            System.out.print(model.TimesslotDB[i] + "\t");
            t1[0][i+1] = model.TimesslotDB[i];
        }
        System.out.println();
        for(int i=0;i<model.TotRooms;i++)
        {
            System.out.print(model.ClassroomDB[i].room_no+"\t");
            t1[i+1][0] = model.ClassroomDB[i].room_no;
            for(int j=0;j<model.TotTimes;j++)
            {
                if(model.TimeTable[i][j]==-1) {
                    System.out.print("\t\t");
                    t1[i+1][j+1] = " ";
                }
                else {
                    System.out.print(model.CourseDB[model.TimeTable[i][j]]);
                    t1[i+1][j+1] =model.CourseDB[model.TimeTable[i][j]];
                }

            }
            System.out.println();

        }
        for(int i=0;i<model.TotRooms+1;i++)
        {
            for(int j=0;j<model.TotTimes+1;j++)
            {
                Table node = new Table( t1[i][j], j * gridWidth, i * gridHeight, gridWidth, gridHeight);
                g1.getChildren().add( node);
            }
        }
        HBox h=new HBox(30);
        h.getChildren().addAll(new Label("Course_No"),new Label("Enrollment"),new Label("Preferences"),new Label("Error or Conflict"));
        g2.getChildren().add(h);

        int y=2;
        for(error err:model.Error)
        {
            HBox hbox=ErrorList(err,y);
            y++;
            g2.getChildren().add(hbox);
        }

        TabPane tabPane=new TabPane();
        Tab tab1=new Tab("Schedule",g1);
        Tab tab2=new Tab("Errors",g2);
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        scene=new Scene(tabPane,600,430);
        stage.setScene(scene);
        stage.show();
    }

    public static HBox ErrorList(error Error, int y)
    {
        Label l1=new Label(Error.c.course_no);
        Label l2=new Label(""+Error.c.enrol);
        Label l3=new Label(Error.c.lst.toString());
        Label l4=new Label(Error.err);

        HBox hbox=new HBox(30);
        hbox.setLayoutY(y*20);
        hbox.getChildren().addAll(l1,l2,l3,l4);
        return hbox;
    }
}

class Table extends StackPane {

    public Table( String name, double x, double y, double width, double height) {

        Rectangle rectangle = new Rectangle( width, height);
        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(Color.LIGHTGREEN);

        Label label = new Label( name);

        setTranslateX( x);
        setTranslateY( y);

        getChildren().addAll( rectangle, label);

    }
}



