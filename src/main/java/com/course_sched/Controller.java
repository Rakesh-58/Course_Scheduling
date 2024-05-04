package com.course_sched;

import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class Controller
{
    static Model model;
    static View view;
    public void submit(ActionEvent e) {
        try {

            for (int i = 0; i < View.lg; i++) {
                model.SchCourses[i]=new course();
                model.SchCourses[i].course_no = view.course_text[i].getText();
                if(view.enrol_text[i].getText().isEmpty())
                    model.SchCourses[i].enrol=0;
                else
                    model.SchCourses[i].enrol = Integer.parseInt(view.enrol_text[i].getText());
                model.SchCourses[i].canSchedule=true;
                model.SchCourses[i].isScheduled=false;
                if(view.pref_text[i].getText().isEmpty()) {
                    model.SchCourses[i].lst = new ArrayList<String>();
                    continue;
                }
                String[] pref = view.pref_text[i].getText().split(",");
                model.SchCourses[i].lst = new ArrayList<String>(Arrays.asList(pref));
            }
            model.NoOfCourses=View.lg;
            /*for (int i = 0; i < model.NoOfCourses; i++) {
                System.out.println(model.SchCourses[i].course_no+" "+model.SchCourses[i].enrol);
                System.out.println(model.SchCourses[i].lst);
            }*/
            view.changeView2();

        }
        catch (Exception e1) {
            System.out.print(e1.getMessage()+"HI");
        }
    }
    public void schedule(ActionEvent e)
    {
        model.schedule();
        view.output(model);
    }

}
