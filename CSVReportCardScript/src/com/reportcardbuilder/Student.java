package com.reportcardbuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

//Student data objects

public class Student {
	private int id;
	private String name;
	private HashMap<Integer, Integer> testing;
	private HashMap<Integer, Integer> courseAvg;
	
	// constructors
	Student(int id, String name){
		this.id = id;
		this.name = name;
		this.testing = new HashMap<Integer, Integer>();
		this.courseAvg = new HashMap<Integer, Integer>();
	}
	Student(){
		this.testing = new HashMap<Integer, Integer>();
		this.courseAvg = new HashMap<Integer, Integer>();
	}
	
	// getters and setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public HashMap<Integer, Integer> getTesting() {
		return testing;
	}
	public void setTesting(HashMap<Integer, Integer> testing) {
		this.testing = testing;
	}
	public HashMap<Integer, Integer> getCourseAvg() {
		return courseAvg;
	}
	public void setCourseAvg(HashMap<Integer, Integer> courseAvg) {
		this.courseAvg = courseAvg;
	}
	
	// Used for testing purposes
	public void viewMaps() {
		for (int i: courseAvg.keySet()) {
			System.out.println(courseAvg.get(i));
		}
	}
	
	// Used for testing purposes
	public void viewTesting() {
		for (int i: testing.keySet()) {
			System.out.println(testing.get(i));
		}
	}
	
	// Used to calculate the student average over all courses
	public double totalAvg() {
		double totalAvg = 0;
		for (int i: courseAvg.keySet()) {
			totalAvg = totalAvg + courseAvg.get(i);
		}
		totalAvg = totalAvg / 100 / courseAvg.size();
		return totalAvg;
	}
	
	// Used this method to make a list of the courses a student has so that it can be sorted
	public List<Integer> courseList(){
		List<Integer> courseList = new ArrayList<Integer>();
		for (int i: courseAvg.keySet()) {
			courseList.add(i);
		}
		return courseList;
	}
	
	// Method to write to the report card text file
	public void printReportCard(FileWriter fr, HashMap<Integer, Object> courses) throws IOException {
		// Used to format grades to two decimal places
		DecimalFormat df = new DecimalFormat("#0.00");
		
		// Appending in format specified
		fr.append("Student Id: ");
		fr.append(String.valueOf(this.id));
		fr.append(", name: ");
		fr.append(this.name);
		fr.append("\nTotal Average:\t ");
		
		double ta = (this.totalAvg());
		fr.append(String.valueOf(df.format(ta)));
		fr.append("%\n");
		
		List<Integer> courseList = (this.courseList());
		Collections.sort(courseList);
		
		for (int i: courseList) {
			fr.append("\tCourse: ");
			fr.append(((Course)courses.get(i)).getCourseName());
			fr.append(", Teacher: ");
			fr.append(((Course)courses.get(i)).getTeacher());
			fr.append("\n\tFinal Grade:\t");
		
			double ca = (this.courseAvg.get(i));
			fr.append(String.valueOf(df.format(ca/100)));
			fr.append("%\n\n");
		}
		
		fr.append("\n");
	}
}