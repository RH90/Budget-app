package com.example.rilind.budget1;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import junit.runner.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Rilind on 2017-04-07.
 */
    public class SQL {

    Intent intent = new Intent("event");
    Statement stmt = null;
    ResultSet rs = null;
    Connection con = null;
    // connect to database
    public Connection connect(String ip) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
            }
            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://rh9011.hopto.org:3306/mydb", "RH9011", "RH9011");
                return con;
            } catch (Exception ex) {
                System.out.println("Connection fail");
                return null;
            }

    }
    // input data to database
    public void input(String ip,Context context) throws SQLException, ClassNotFoundException, IOException {
        String color="0";
        try {
            int index =index(ip,context);
            System.out.println(index);
            File file = new File(context.getFilesDir(),"hello.txt");
            if(!file.exists())
                file.createNewFile();
            Scanner sc = new Scanner(context.openFileInput(file.getName()));
            con = connect(ip);
            stmt = con.createStatement();

            String query="";
            while(sc.hasNextLine()){
                String[] parts =sc.nextLine().split("¤¤");
                String s=parts[0];
                int i= Integer.parseInt(s);
                if(index<i||index==0){
                   query = "INSERT INTO Budget (id,Item,Moms,Price,Comment,Date,IN_UT,used) VALUES ("+parts[0]+",'" + parts[1] + "', " +parts[2] + "," + parts[3]  + ",'" + parts[4] + "','"+parts[5]+"','"+parts[6]+"','"+parts[7]+"');";
                   stmt.addBatch(query);
                }
            }
            sc.close();
            System.out.println(query);

            stmt.executeBatch();
            if(query.equalsIgnoreCase(""))
                color="2";
            else
                color="1";


          //
           //



        } catch (Exception ex) {
            System.out.println("Login fail1");
        } finally {
            try {

                intent.putExtra("message", color);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                Logger lgr = Logger.getLogger(Version.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
    public int index(String ip,Context context){
        int index =0;
        try {
            con = connect(ip);

            stmt = con.createStatement();
            String query = "select * from budget";
            rs = stmt.executeQuery(query);
            ResultSetMetaData columns = rs.getMetaData();

            rs.afterLast();
            if(rs.previous())
                index=Integer.parseInt(rs.getString(1));



        } catch (Exception ex) {
            intent.putExtra("message", "0");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            System.out.println("Login fail");
        } finally {
            try {

                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }


            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(Version.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        return index;
    }
    // read data from database
    public void read(String ip, Context context) {
        try {
            con = connect(ip);

            stmt = con.createStatement();
            String query = "select * from budget";
            rs = stmt.executeQuery(query);
            ResultSetMetaData columns = rs.getMetaData();
            String s;
            s = String.format("%-10s|%4s|%5s|%-13s|%-10s\n----------------------------------------------\n", columns.getColumnName(2), columns.getColumnName(3), columns.getColumnName(4), columns.getColumnName(5), columns.getColumnName(6));


            //}

            /*
            int a = 0, b = 0, c = 0, d = 0;
            while (rs.next()) {
                if (rs.getString(1).length() > a) {
                    a = rs.getString(1).length();
                }
                if (rs.getString(2).length() > b) {
                    b = rs.getString(2).length();
                }
                if (rs.getString(3).length() > c) {
                    c = rs.getString(3).length();
                }
                if (rs.getString(4).length() > d) {
                    d = rs.getString(4).length();
                }
            }
            */
            rs.afterLast();
            while (rs.previous()) {//get first result
                if(rs.getString(7).endsWith("UT"))
                s = s + String.format("%-10s|%4s|%5s|%-13s|%-10s\n", rs.getString(2), rs.getString(3), "-"+rs.getString(4), rs.getString(5), rs.getString(6));
                else
                    s = s + String.format("%-10s|%4s|%5s|%-13s|%-10s\n", rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
            }
            // send string to textfield
            intent.putExtra("message", s);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        } catch (Exception ex) {
            System.out.println("Login fail");
        } finally {
            try {

                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }


            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(Version.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }


    }

    public void results(String ip, Context context,String from,String to){
        try {
            con = connect(ip);
            stmt = con.createStatement();
            String query = "SELECT * FROM budget WHERE (Date BETWEEN '"+from+"' AND '"+to+"')";
            rs = stmt.executeQuery(query);
            String s="";
            double moms_in=0;
            double moms_ut = 0;
            double solt_med_moms=0;
            double kopt_med_moms=0;
            while (rs.next()) {//get first result
                if(rs.getString(7).endsWith("UT")) {

                    moms_ut += Double.parseDouble(rs.getString(3)) * Double.parseDouble(rs.getString(4));
                    kopt_med_moms+=Double.parseDouble(rs.getString(4))+moms_ut;
                }else {
                    if(rs.getString(8).equalsIgnoreCase("no"))
                        moms_in += Double.parseDouble(rs.getString(3)) * Double.parseDouble(rs.getString(4));
                    else
                        moms_in += Double.parseDouble(rs.getString(3)) * (Double.parseDouble(rs.getString(4))*0.8);
                    solt_med_moms+=Double.parseDouble(rs.getString(4)) +moms_in;
                }
            }
            double moms_total = moms_in-moms_ut;
            double vinst= solt_med_moms-kopt_med_moms;
            s=String.format("%.2f-%.2f-%.2f-%.2f",solt_med_moms,kopt_med_moms,moms_total,vinst);
            intent.putExtra("message", s);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        } catch (SQLException ex) {
            System.out.println("Login fail");
        } finally {
            try {

                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(Version.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

    }


}
