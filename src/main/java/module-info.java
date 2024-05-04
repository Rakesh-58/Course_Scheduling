module com.course_sched {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.course_sched to javafx.fxml;
    exports com.course_sched;
}