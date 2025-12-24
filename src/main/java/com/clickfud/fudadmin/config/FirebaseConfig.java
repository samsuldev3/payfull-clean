import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore firebaseFirestore() throws Exception {

     InputStream serviceAccount;

String firebaseJson = System.getenv("FIREBASE_SERVICE_ACCOUNT");

if (firebaseJson != null && !firebaseJson.isEmpty()) {
    serviceAccount = new ByteArrayInputStream(
            firebaseJson.getBytes(StandardCharsets.UTF_8)
    );
} else {
    serviceAccount = getClass()
            .getClassLoader()
            .getResourceAsStream("firebase-service-account.json");
}

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        return FirestoreClient.getFirestore();
    }
}
