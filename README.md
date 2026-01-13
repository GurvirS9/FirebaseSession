# Basic Todo App using Firestore as Backend

## Firebase Setup:
### Step 1: Create a Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/).
2. Click **Add project** and give it a name (e.g., "todo").
3. Disable Google Analytics (optional, keeps it simple).
4. Click **Create project**.

### Step 2: Add Android App
1. In your new project, click the **Android icon** to add an app.
2. **Package Name**: `com.example.todofcrud` (Example, must match exactly as the project).
3. Click **Register app**.

### Step 3: Download Config File
1. Download the `google-services.json` file.
2. Move this file into the `app/` folder of your project:
   Example: `TodoFCRUD/app/google-services.json`

### Step 4: Enable Firestore
1. In Firebase Console, go to **Build** -> **Firestore Database**.
2. Click **Create database**.
3. Choose a location (e.g., `us-central1`).
4. **Security Rules**: Start in **Test Mode** (allows read/write for 30 days).
   - *Note: For a real app, you should configure proper security rules.*

### P.S.
If you see a `PERMISSION_DENIED` crash, go to the **Rules** tab in Firestore and replace everything with these disaster rules:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```
Then click **Publish**.

