package com.course_sched;

import java.util.ArrayList;
import java.sql.*;
import java.util.Arrays;


class room implements Comparable<room>
{
    String room_no;
    int capacity;
    room(String room_no,int capacity)
    {
        this.room_no=room_no;
        this.capacity=capacity;
    }
    public int compareTo(room other)
    {
        return Integer.compare(this.capacity,other.capacity);
    }
}
class course
{
    String course_no;
    int enrol;
    ArrayList<String> lst;
    Boolean canSchedule;
    Boolean isScheduled;
}

class error
{
    course c;
    String err;
}

public class Model
{
    int TotRooms=4,TotTimes=7,TotCourses=30;
    static int MAX_COURSES=30;
    int NoOfCourses;
    int PgNP,UgP,UgNP;

    course SchCourses[]=new course[MAX_COURSES];
    room ClassroomDB[]=new room[TotRooms];
    //"cs101","cs102","cs110","cs120","cs220","cs412","cs430","cs612","cs630"
    String CourseDB[]=new String[TotCourses];
    //"MWF9","MWF10","MWF11","MWF2","TT9","TT10:30","TT2","TT3:30"
    String TimesslotDB[]=new String[TotTimes];

    int TimeTable[][]=new int[TotRooms][TotTimes];
    ArrayList<error> Error=new ArrayList<error>();

    public Model()
    {
        try {
            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/course_scheduling", "root", "root");
            Statement stmt = c.createStatement();
            ResultSet r = stmt.executeQuery("select * from roomsdb");
            int i = 0;
            while (r.next()) {
                ClassroomDB[i] = new room(r.getString(1), r.getInt(2));
                i++;
            }
            i = 0;
            r = stmt.executeQuery("select * from coursesdb");
            while (r.next()) {
                CourseDB[i] = r.getString(1);
                i++;
            }
            r = stmt.executeQuery("select * from timesdb");
            i = 0;
            while (r.next()) {
                TimesslotDB[i] = r.getString(1);
                i++;
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    private void sortCourses()
    {
        try {
            course pg_pref[] = new course[MAX_COURSES];
            course ug_pref[] = new course[MAX_COURSES];
            course pg_nopref[] = new course[MAX_COURSES];
            course ug_nopref[] = new course[MAX_COURSES];

            int pg_pref_i = 0;
            int ug_pref_i = 0;
            int pg_no_pref_i = 0;
            int ug_no_pref_i = 0;
            int j = 0, i = 0;
            for (i = 0; i < NoOfCourses; i++) {
                char temp = SchCourses[i].course_no.charAt(2);
                if (temp > '5') {
                    if (SchCourses[i].lst.isEmpty()) {
                        pg_nopref[pg_no_pref_i] = new course();
                        pg_nopref[pg_no_pref_i++] = SchCourses[i];
                    } else {
                        pg_pref[pg_pref_i] = new course();
                        pg_pref[pg_pref_i++] = SchCourses[i];
                    }
                }
                else {
                    if (SchCourses[i].lst.isEmpty()) {
                        ug_nopref[ug_no_pref_i] = new course();
                        ug_nopref[ug_no_pref_i++] = SchCourses[i];
                    } else {
                        ug_pref[ug_pref_i] = new course();
                        ug_pref[ug_pref_i++] = SchCourses[i];
                    }
                }
            }
            for (i = 0, j = 0; i < pg_pref_i; i++, j++)
                SchCourses[j] = pg_pref[i];

            UgP = j;
            for (i = 0; i < ug_pref_i; i++, j++)
                SchCourses[j] = ug_pref[i];

            PgNP = j;
            for (i = 0; i < pg_no_pref_i; i++, j++)
                SchCourses[j] = pg_nopref[i];

            UgNP = j;
            for (i = 0; i < ug_no_pref_i; i++, j++)
                SchCourses[j] = ug_nopref[i];
        }
        catch (Exception e1)
        {
            System.out.println(e1.getMessage());
        }
    }
    private int getRoom(int capacity)
    {
        for(int i=0;i<TotRooms;i++)
        {
            if(ClassroomDB[i].capacity>=capacity)
            {
                return i;
            }
        }
        return -1;
    }
    void schedule()
    {
        Arrays.sort(ClassroomDB);
        sortCourses();
        ArrayList<String> CourseDBA=new ArrayList<>(Arrays.asList(CourseDB));
        ArrayList<String> TimesslotDBA=new ArrayList<>(Arrays.asList(TimesslotDB));
        ArrayList<String> tempCourse=new ArrayList<String>();
        for (int i = 0; i < NoOfCourses; i++) {
                if(!tempCourse.contains(SchCourses[i].course_no))
                {
                    tempCourse.add(SchCourses[i].course_no);
                }
                else
                {
                    SchCourses[i].canSchedule=false;
                    error temp=new error();
                    temp.c=SchCourses[i];
                    temp.err="Duplicate Course";
                    Error.add(temp);
                }
                if(SchCourses[i].enrol<=2 || SchCourses[i].enrol>250)
                {
                    SchCourses[i].canSchedule=false;
                    error temp=new error();
                    temp.c=SchCourses[i];
                    temp.err="Invalid Enrollment";
                    Error.add(temp);
                }
                if(SchCourses[i].lst.size()>5)
                {
                    error temp=new error();
                    temp.c=SchCourses[i];
                    temp.err="More number of preferences";
                    Error.add(temp);
                }
                if(!CourseDBA.contains(SchCourses[i].course_no))
                {
                    SchCourses[i].canSchedule=false;
                    error temp=new error();
                    temp.c=SchCourses[i];
                    temp.err="Invalid Course Number";
                    Error.add(temp);
                }
        }

        for(int i=0;i<TotRooms;i++)
        {
            for(int j=0;j<TotTimes;j++)
            {
                TimeTable[i][j]=-1;
            }
        }

        for(int i=0;i<UgP;i++)
        {
            if(!SchCourses[i].canSchedule)
                continue;
            int room=getRoom(SchCourses[i].enrol);
            if(room==-1)
            {
                error temp=new error();
                temp.c=SchCourses[i];
                temp.err="No room available";
                Error.add(temp);
                continue;
            }
            for(String s:SchCourses[i].lst)
            {
                int index=TimesslotDBA.indexOf(s);
                if(index==-1){
                    error temp=new error();
                    temp.c=SchCourses[i];
                    temp.err="Invalid Time Preference "+s;
                    Error.add(temp);
                    continue;
                }
                if(TimeTable[room][index]==-1)
                {
                    TimeTable[room][index]=CourseDBA.indexOf(SchCourses[i].course_no);
                    break;
                }
                else
                {
                    error temp=new error();
                    temp.c=SchCourses[i];
                    temp.err="Conflict with "+CourseDB[TimeTable[room][index]];
                    Error.add(temp);
                }
            }
        }
        for(int i=UgP;i<PgNP;i++)
        {
            if(!SchCourses[i].canSchedule)
                continue;
            int room=getRoom(SchCourses[i].enrol);
            if(room==-1) {
                error temp=new error();
                temp.c=SchCourses[i];
                temp.err="No room available";
                Error.add(temp);
                continue;
            }
            for(String s:SchCourses[i].lst)
            {
                int index=TimesslotDBA.indexOf(s);
                if(index==-1) {
                    error temp=new error();
                    temp.c=SchCourses[i];
                    temp.err="Invalid Time Preference "+s;
                    Error.add(temp);
                    continue;
                }
                if(TimeTable[room][index]==-1)
                {
                    TimeTable[room][index]=CourseDBA.indexOf(SchCourses[i].course_no);
                    break;
                }
                else
                {
                    error temp=new error();
                    temp.c=SchCourses[i];
                    temp.err="Conflict with "+CourseDB[TimeTable[room][index]];
                    Error.add(temp);
                }
            }
        }
        for(int i=PgNP;i<UgNP;i++)
        {
            if(!SchCourses[i].canSchedule)
                continue;
            int room=getRoom(SchCourses[i].enrol);
            if(room==-1){
                error temp=new error();
                temp.c=SchCourses[i];
                temp.err="No room available";
                Error.add(temp);
                continue;
            }
            for(int j=0;j<TotTimes;j++)
            {
                if(TimeTable[room][j]==-1)
                {
                    TimeTable[room][j]=CourseDBA.indexOf(SchCourses[i].course_no);
                    break;
                }
                else
                {
                    error temp=new error();
                    temp.c=SchCourses[i];
                    temp.err="Conflict with "+CourseDB[TimeTable[room][j]];
                    Error.add(temp);
                }
            }
        }
        for(int i=UgNP;i<NoOfCourses;i++)
        {
            if(!SchCourses[i].canSchedule)
                continue;
            int room=getRoom(SchCourses[i].enrol);
            if(room==-1){
                error temp=new error();
                temp.c=SchCourses[i];
                temp.err="No room available";
                Error.add(temp);
                continue;
            }
            for(int j=0;j<TotTimes;j++)
            {
                if(TimeTable[room][j]==-1)
                {
                    TimeTable[room][j]=CourseDBA.indexOf(SchCourses[i].course_no);
                    break;
                }
                else
                {
                    error temp=new error();
                    temp.c=SchCourses[i];
                    temp.err="Conflict with "+CourseDB[TimeTable[room][j]];
                    Error.add(temp);
                }
            }
        }
    }


}
