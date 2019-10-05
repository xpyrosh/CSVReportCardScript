package com.reportcardbuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CSVReaderWriter {
	public static void main(String[] args) {
		System.out.println("Hatchway Assessment");
		
		// Populating HashMap of Courses with data from Courses CSV
		HashMap<Integer, Object> courses = readCSV("./courses.csv", 0);
		
		// Populating HashMap of Students
		HashMap<Integer, Object> students = readCSV("./students.csv", 1);
		
		//Populating HashMap of Tests
		HashMap<Integer, Object> tests = readCSV("./tests.csv", 2);
		
		// Reading Marks CSV into a List
		List<Mark> marks = readMarks();
		
		// Needed try catch block for this method since I threw a custom exception for incomplete courses
		try {
			assignTests(students, tests, marks);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// Writing the information to the report card text file
		writeReportCard(students, courses);
		
	}
	
	public static void assignTests(HashMap<Integer, Object> students, HashMap<Integer, Object> tests, List<Mark> marks) throws Exception {
		
		// Iterating through all the marks data to sort to the correct students
		for (Mark m: marks) {
			if(students.containsKey(m.getStudentId())) {
				
				// Creating temp value to append data to current user
				Student tempStudent = (Student) students.get(m.getStudentId());
				
				// Checking if the current mark data's test exists in the test object data
				if(tests.containsKey(m.getTestId())) {
					
					// Creating temp variables to work with to append correct data
					int testId = m.getTestId();
					int tempMark = m.getMark();
					Integer tempCourseID = ((Test) tests.get(testId)).getCourseId();
					
					// checking if the course already exists in the current temp students data 
					if(tempStudent.getTesting().containsKey(tempCourseID)) {
						
						// if the data exists appending the weight data to the student to be able to tell if the student
						// completed all tests for a specified course
						tempStudent.getTesting().put(tempCourseID, tempStudent.getTesting().get(tempCourseID) 
								+ ((Test) tests.get(testId)).getWeight());
						
						// appending the courses averages to the user data
						tempStudent.getCourseAvg().put(tempCourseID, tempStudent.getCourseAvg().get(tempCourseID) 
								+ tempMark * ((Test) tests.get(testId)).getWeight());
					}
					// if the course doesn't already exist in the students data then add it for the first time
					else {
						
						tempStudent.getTesting().put(tempCourseID, ((Test) tests.get(testId)).getWeight());
						
						tempStudent.getCourseAvg().put(tempCourseID, tempMark * ((Test) tests.get(testId)).getWeight());
					}
				}
			}
		}
		// here we go through the completed student data to find out if they failed to complete a course
		for(int i: students.keySet()) {
			// by checking the map of weights we can determine if they completed all tests since all total weights should be 100
			HashMap<Integer, Integer> courseWeights = ((Student) students.get(i)).getTesting();
			for(int x: courseWeights.keySet()) {
				if (courseWeights.get(x) != 100) {
					throw new Exception("Course Incomplete");
				}
			}
		}
	}
	
	public static void writeReportCard(HashMap<Integer, Object> students, HashMap<Integer, Object> courses) {
		// Method used to write the report card data to the reportcard.txt 
		FileWriter fr = null;
		try {
			fr = new FileWriter("./reportcard.txt");
			// this list is used to sort the students by student id
			List<Integer> conversion = new ArrayList<Integer>(students.keySet());
			Collections.sort(conversion);
			
			for(int i: conversion) {
				// method created in the student class to write the specified data to the file
				((Student)students.get(i)).printReportCard(fr, courses);
				fr.append("\n");
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			try {
				fr.flush();
				fr.close();
				System.out.println("Sucessful Write");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static List<Mark> readMarks(){
		// method used to read the marks csv and populate it into a list
		BufferedReader reader = null;
		List<Mark> marks = null;
		try {
			// open buffered reader and read line by line while lines exist
			marks = new ArrayList<Mark>();
			String line = "";
			reader = new BufferedReader(new FileReader("./marks.csv"));
			reader.readLine();
			
			while((line = reader.readLine()) != null){
				// spliting data based on commas
				String[] fields = line.split(",");
				
				// creating mark objects and appending the the marks list
				if(fields.length>0) {
					Mark mark = new Mark();
					mark.setTestId(Integer.parseInt(fields[0]));
					mark.setStudentId(Integer.parseInt(fields[1]));
					mark.setMark(Integer.parseInt(fields[2]));
					marks.add(mark);
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {
				reader.close();
				return marks;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return marks = null;
	}
	
	public static HashMap<Integer, Object> readCSV(String path, int fileId) {
		// Generic CSV reader that populates data into a Map
		// Creating empty reader and object map
		BufferedReader reader = null;
		
		HashMap<Integer, Object> hm = new HashMap<>();
		
		try {
			String line = "";
			FileReader fr = new FileReader(path);
			reader = new BufferedReader(fr);
			// Reading past the category title line
			reader.readLine();
			
			// Reading while not null
			while((line = reader.readLine()) != null) {
				String[] fields = line.split(",");
				
				// Populating the course list to return
				if (fields.length > 0) {
					
					// handling the different csv cases
					switch(fileId) {
						case 0:
							Course course = new Course();
							course.setId(Integer.parseInt(fields[0]));
							course.setCourseName(fields[1]);
							course.setTeacher(fields[2]);
							
							hm.put(course.getId(), course);
							break;
						case 1:
							Student student = new Student(Integer.parseInt(fields[0]), fields[1]);
							
							hm.put(student.getId(), student);
							break;
						case 2:
							Test test = new Test();
							test.setTestId(Integer.parseInt(fields[0]));
							test.setCourseId(Integer.parseInt(fields[1]));
							test.setWeight(Integer.parseInt(fields[2]));
							
							hm.put(test.getTestId(), test);
							break;
					}
				}
			}
			
			System.out.println("Sucessfully Read");
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Read Failed");
		}
		finally {
			try {
				reader.close();
				return hm;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return hm = null;
	}

}
