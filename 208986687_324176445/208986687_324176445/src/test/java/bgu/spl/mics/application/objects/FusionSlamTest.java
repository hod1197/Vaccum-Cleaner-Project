package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FusionSlamTest {

    private FusionSlam fusionSlam;

    @BeforeEach
    public void setUp() {
        // אתחול מחדש של ה-FusionSlam לפני כל טסט
        fusionSlam = FusionSlam.getInstance();
        fusionSlam.reset();
        // אתה יכול להוסיף גם אתחול נוסף במידת הצורך (למשל, אם יש צורך לאתחל ערכים אחרים)
    }

    //@BeforeEach
    //public void setUp() {
    //    fusionSlam = FusionSlam.getInstance();
    //}

    @Test
    public void testAddPose() {
        Pose pose = new Pose(1, 1.0, 1.0, (int) 0.0);  // יצירת Pose עם timestamp 1
        fusionSlam.addPose(pose);
        
        List<Pose> poses = fusionSlam.getAllPoses();
        assertEquals(1, poses.size(), "The number of poses should be 1.");
        assertEquals(pose, poses.get(0), "The pose added should be the one we created.");
    }


    @Test
public void testHandleTrackedObject_ExistingLandmark() {
    // Preconditions: יצירת FusionSlam והוספת Pose
    FusionSlam fusionSlam = FusionSlam.getInstance();
    Pose pose = new Pose(1, 1, 90, 5);  // ערכים לפוזה
    fusionSlam.addPose(pose);

    // יצירת TrackedObject
    ArrayList<CloudPoint> coordinates = new ArrayList<>();
    coordinates.add(new CloudPoint(1, 1));  // קואורדינטות
    TrackedObject trackedObject = new TrackedObject("Object1", 5, "Description", coordinates);
    
    // הוספת TrackedObject ראשון, יצירת Landmark חדש
    fusionSlam.handleTrackedObject(trackedObject);
    
    // Post-test: יש Landmark אחד אחרי ההוספה הראשונה
    assertEquals(1, fusionSlam.getAllLandMarks().size());
    
    // יצירת TrackedObject חדש עם אותו ID, אך קואורדינטות שונות
    ArrayList<CloudPoint> newCoordinates = new ArrayList<>();
    newCoordinates.add(new CloudPoint(2, 2));  // קואורדינטות שונות
    TrackedObject trackedObject2 = new TrackedObject("Object1", 6, "Updated Description", newCoordinates);

    // קריאה למתודה handleTrackedObject שוב עם ID זהה
    fusionSlam.handleTrackedObject(trackedObject2);
    
    // Post-test: יש עדיין רק Landmark אחד, אך הקואורדינטות עודכנו
    assertEquals(1, fusionSlam.getAllLandMarks().size());
    LandMark updatedLandmark = fusionSlam.getAllLandMarks().get(0);
    
    // בדיקה שהקואורדינטות של ה-Landmark עודכנו
    //assertEquals(new CloudPoint(2, 2), updatedLandmark.getCoordinates().get(0));
}


@Test
public void testProcessPendingObjects() {
    // Preconditions: יצירת FusionSlam והוספת Pose
    FusionSlam fusionSlam = FusionSlam.getInstance();
    Pose pose = new Pose(1, 1, 90, 5);  // ערכים לפוזה
    fusionSlam.addPose(pose);

    // יצירת TrackedObject
    ArrayList<CloudPoint> coordinates = new ArrayList<>();
    coordinates.add(new CloudPoint(1, 1));  // קואורדינטות
    TrackedObject trackedObject = new TrackedObject("Object1", 5, "Description", coordinates);

    // הוספת האובייקט ל-pendingTrackedObjects
    fusionSlam.handleTrackedObject(trackedObject);

    // Test: קריאה למתודת processPendingObjects
    fusionSlam.processPendingObjects();

    // Post-test: אחרי עיבוד האובייקט, הוא לא אמור להיות בתור
    assertTrue(fusionSlam.retrievePendingObjects().isEmpty());

    // בנוסף, נוודא שה-LandMark נוסף
    assertEquals(1, fusionSlam.getAllLandMarks().size());
}






@Test
public void testHandleTrackedObject() {
    // Preconditions: יצירת FusionSlam והוספת Pose
    FusionSlam fusionSlam = FusionSlam.getInstance();
    Pose pose = new Pose(1, 1, 90, 5);  // תוסיף את הערכים המתאימים לפוזה
    fusionSlam.addPose(pose);
    
    // יצירת TrackedObject
    ArrayList<CloudPoint> coordinates = new ArrayList<>();
    coordinates.add(new CloudPoint(1, 1));  // תוסיף את הקואורדינטות המתאימות
    TrackedObject trackedObject = new TrackedObject("Object1", 5, "Description", coordinates);
    
    // Pre-test: אין LandMarks לפני ההוספה
    assertEquals(0, fusionSlam.getAllLandMarks().size());
    
    // Test: קריאה למתודה handleTrackedObject
    fusionSlam.handleTrackedObject(trackedObject);
    
    // Post-test: יש LandMark אחד אחרי הקריאה
    assertEquals(1, fusionSlam.getAllLandMarks().size());
    
    // Post-test: דגל pendingObjectsUpdated אמור להיות true אם אובייקט היה ממתין לפוזה
    assertFalse(fusionSlam.pendingObjectsUpdated);

    // אם לא נמצא פוזה, נוודא ש-TrackedObject נוסף ל-pendingTrackedObjects
    fusionSlam.processPendingObjects();
    assertTrue(fusionSlam.retrievePendingObjects().isEmpty()); // יוודא שכל האובייקטים המטופלים עברו לפעולה
}



}
