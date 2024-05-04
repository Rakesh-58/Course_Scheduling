package com.course_sched;

public class Main
{
    public static void main(String[] args) {
        try {
            View view = new View();
            Controller.model= new Model();
            Controller.view=view;
            view.startView();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
