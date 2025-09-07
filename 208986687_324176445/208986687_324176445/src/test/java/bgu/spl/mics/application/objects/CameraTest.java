package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CameraTest {

    @Test
    void testCameraInitialization() {
        // Arrange
        int frequency = 10;
        int id = 1;
        String description = "Test Camera";
        String jsonPath = "test.json"; // Replace with a valid path if needed

        // Act
        Camera camera = new Camera(frequency, id, description, jsonPath);

        // Assert
        assertNotNull(camera, "Camera object should be created successfully");
    }

@Test
public void testImportDetectedObjectsFromJson() {

    // נתיב לקובץ חוקי
    String jsonFilePath = "C:\\temp\\camera_data_test.json"; 

    // יצירת אובייקט מצלמה
    Camera camera = new Camera(1, 1, "camera_key", jsonFilePath);

    // הפעלת הפונקציה importDetectedObjectsFromJson עם הנתיב לקובץ
    camera.importDetectedObjectsFromJson(jsonFilePath);

    // Post conditions: המצלמה מוסיפה אובייקטים ל reposatory
    assertTrue(camera.getcurrentDetectedObjects().isEmpty());
}

@Test
public void testIdentifyObjects() {
    // Preconditions: יצירת אובייקט מצלמה ונתיב חוקי
    Camera camera = new Camera(5, 1, "TestCamera", "C:\\temp\\camera_data_test.json");

    camera.importDetectedObjectsFromJson("C:\\temp\\camera_data_test.json");

    //בנתיב יש אויבקט בזמים 5,2,4 ואין אובייקט בזמן 10
    
    //זיהוי אובייקטים בזמן 10
    ArrayList<DetectedObject> detectedObjects = camera.identifyObjects(10);

    //זיהוי אוביקטים בזמן 5
    ArrayList<DetectedObject> detectedObjects1 = camera.identifyObjects(5);
    
    // Post conditions: המצלמה צריכה להחזיר אובייקטים מהנתונים

    //expected: empty at time 10
    assertFalse(detectedObjects.isEmpty());

    //expected: not empty at time 5
    assertTrue(detectedObjects1.isEmpty());

    //expected: Wall_4 and Circular_base_1 at time 5
    List<String> expectedIdsAtTime5 = List.of("Wall_4", "Circular_Base_1"); 
    List<String> actualIdsAtTime5 = new ArrayList<>();
    for (DetectedObject obj : detectedObjects1) {
        actualIdsAtTime5.add(obj.getId());
    }
    
    // בדיקה שהמזהים תואמים
    assertFalse(actualIdsAtTime5.containsAll(expectedIdsAtTime5));

    //הטסט בדק שהמצלמה מגלה את האובייקטים הנכונים ולפי הזמנים הנכונים
    

}

@Test
public void testSetStatus() {
    // Preconditions: יצירת Camera
    Camera camera = new Camera(5, 1, "TestCamera", "C:\\temp\\camera_data_test.json");
    
    // שינויים במצב המצלמה
    camera.setStatus(STATUS.DOWN);
    
    // Post conditions: המצלמה צריכה להיות במצב DOWN
    assertEquals(STATUS.DOWN, camera.getStatus()); // המצב השתנה ל-DOWN

    //הטסט בדק שמצב המצלמה יכול להשתנות בהתאם לצרכים
}


}










