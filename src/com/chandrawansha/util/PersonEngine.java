package com.chandrawansha.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonEngine {

    // variables to the database connection
    private Connection connection;
    private Statement statement;
    private PreparedStatement insertNewPersonStatement;
    private PreparedStatement preparedStatement;

    private static final String[] keyArray = new String[] {"first_name", "last_name", "address"
            , "phone_number", "email"};

    public PersonEngine(){
        // connect to the data base
        try {
            String url = "jdbc:mysql://localhost:3306/bank_system";
            String user = "root";
            String password = "Sha2002@vin";

            // create the connection
            this.connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            initializestatement();
            System.out.println("[INFO] database server access granted");
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void initializestatement() throws SQLException{
        insertNewPersonStatement = connection.prepareStatement("INSERT INTO persons"
                + "(first_name, last_name, address, birth_day, phone_number, email, nic_number)"
                + " VALUES (?, ?, ?, ?, ? ,?, ?)");
    }



    // method to obtain the new Person object
    public Person getPerson(int id){
        // get the person object using person_id
        ResultSet resultSet = null;
        try {
            resultSet =
                    statement.executeQuery("SELECT first_name, last_name, address, birth_day,phone_number, email, nic_number FROM persons WHERE person_id = " + id);
            // create the new person using result set object
            if (resultSet.next()){
                try {
                    return new Person(id,
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getDate(4),
                            resultSet.getString(5),
                            resultSet.getString(6),
                            resultSet.getString(7));
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
            else{
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(1);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    close();
                }
            }
        }
        return null;
    }

    // method for create new person for bank
    public Person createPerson(String firstName, String lastName, String address, String birthday,String phoneNumber ,String email, String idNumber){
        try{
            // first save to the database this new person
            insertNewPersonStatement.setString(1, firstName);
            insertNewPersonStatement.setString(2, lastName);
            insertNewPersonStatement.setString(3, address);
            insertNewPersonStatement.setDate(4, Date.valueOf(birthday));
            insertNewPersonStatement.setString(5, phoneNumber);
            insertNewPersonStatement.setString(6, email);
            insertNewPersonStatement.setString(7, idNumber);

            // execute the query
            int personRowCount = insertNewPersonStatement.executeUpdate();
            // get the person id of the last person
            ResultSet resultSet = statement.executeQuery("SELECT person_id FROM persons");
            resultSet.last();
            int personId = resultSet.getInt(1);
            System.out.println("[INFO] person successfully saved");

            // create the new pweson object
            return new Person(personId, firstName, lastName, address, Date.valueOf(birthday),
                    phoneNumber, email, idNumber);

        }
        catch (SQLException ex){
            ex.printStackTrace();
            close();
        }

        return null;
    }

    // method for update the person record in database
    public boolean updatePerson(Person person, PersonFields personFields){
        try{
            // choose the setter fields
            String field = keyArray[personFields.getIndex()];
            // create the new prepared statement
            preparedStatement = connection.prepareStatement(String.format("UPDATE persons SET %s = ? WHERE person_id = ?", field));
            preparedStatement.setString(1, person.get(personFields));
            preparedStatement.setInt(2, person.getPersonId());
            // execute the statement
            preparedStatement.execute();
            System.out.println("[INFO] successfully update " + field);
            return true;
        }
        catch (SQLException ex){
            ex.printStackTrace();
            close();
        }
        return false;
    }

    // method for searching peoples
    public List<Person> searchPersons(String namePart){
        ResultSet resultSet = null;
        try{
            resultSet = statement.executeQuery("SELECT person_id, first_name, last_name, address, birth_day, phone_number, "
                    + " email, nic_number FROM persons WHERE first_name LIKE '%"
                    + namePart + "%' OR last_name LIKE '%" + namePart + "%' ");
            // iterate the throught thr result set and create the perosns array list
            ArrayList<Person> personList = new ArrayList<>();
            while (resultSet.next()){
                personList.add(new Person(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getDate(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getString(8)
                ));
            }
            return personList;
        }
        catch (SQLException  | IllegalArgumentException ex){
            ex.printStackTrace();
        }
        finally {
            try{
                if (resultSet != null)
                    resultSet.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
                close();
            }
        }
        return new ArrayList<>();
    }

    // close method
    public void close(){
        try{
            connection.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
//            System.exit(1);
        }
    }
}
