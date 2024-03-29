package view;

import controller.CourseController;
import controller.RegistrationSystem;
import controller.StudentController;
import controller.TeacherController;
import entities.Course;
import entities.Student;
import entities.Teacher;
import repository.CourseJdbcRepository;
import repository.StudentJdbcRepository;
import repository.TeacherJdbcRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TeacherView {
    private static Teacher teacher;

    public RegistrationSystem initializeRegistrationSystem() {
        CourseJdbcRepository courseRepository = new CourseJdbcRepository();
        CourseController courseController = new CourseController(courseRepository);

        StudentJdbcRepository studentRepository = new StudentJdbcRepository();
        StudentController studentController = new StudentController(studentRepository);

        TeacherJdbcRepository teacherRepository = new TeacherJdbcRepository();
        TeacherController teacherController = new TeacherController(teacherRepository);

        return new RegistrationSystem(studentController, courseController, teacherController);
    }


    @FXML
    Label message;

    @FXML
    Label message2;

    @FXML
    Label notFoundText;

    @FXML
    Button teacherLogIn;

    @FXML
    TextField idTeacher;

    @FXML
    Button refresh;

    @FXML
    Button showStudents;

    @FXML
    Label course;

    @FXML
    ListView<Student> listView;

    @FXML
    TextField courseName;

    /**
     * log in for teachers
     * @throws IOException if an I/O exception has occurred
     */
    public void loginTeacher() throws IOException {
        int id = Integer.parseInt(idTeacher.getText());
        RegistrationSystem registrationSystem = initializeRegistrationSystem();
        try {
            teacher = registrationSystem.getTeacherController().findByID(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (teacher == null) {
            notFoundText.setText("Teacher not found");
            return;
        }

        HelloController main = new HelloController();
        main.changeSceneTeacher("teacher-after-login.fxml");
    }

    /**
     * shows all students enrolled to a certain course of the logged in teacher
     */
    public void showEnrolledStudents() {
        message2.setVisible(false);
        String courseTitle = courseName.getText();
        RegistrationSystem registrationSystem = initializeRegistrationSystem();
        Course foundCourse = teacher.getCourses().stream()
                .filter(myCourse -> myCourse.getName().equals(courseTitle))
                .findFirst()
                .orElse(null);

        if (foundCourse == null) {
            message2.setText("This is not your course!");
            message2.setVisible(true);
            return;
        }

        try {
            List<Student> students = registrationSystem.retrieveStudentsEnrolledForACourse(courseTitle);
            ObservableList<Student> observableList = FXCollections.observableList(students);
            listView.setItems(observableList);
            refresh.setVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * refreshes page to show the updated list of enrolled students
     */
    public void refreshPage() {
        showEnrolledStudents();
        message2.setText("Page was refreshed");
        message2.setVisible(true);
    }
}
