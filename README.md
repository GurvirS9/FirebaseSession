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

### Step 5: Enable Authentication
#### Step 1: Enable Authentication Providers
1. Go to Firebase Console -> **Build** -> **Authentication**.
2. Click **Get Started**.
3. **Email/Password**:
   - Select **Email/Password**.
   - Enable "Email/Password".
   - Click **Save**.
4. **Google**:
   - Click **Add new provider**.
   - Select **Google**.
   - Enable it.
   - Set the **Project support email**.
   - Click **Save**.

#### Step 2: Google Sign-In Requirements (IMPORTANT)
For Google Sign-In to work, you **MUST** add your SHA-1 fingerprint to Firebase.

1. **Generate SHA-1**:
   - Open the **Gradle** tab in Android Studio (right side).
   - Navigate to `TodoFCRUD` -> `Tasks` -> `android` -> `signingReport`.
   - Double-click `signingReport`.
   - OR Click Gradle (Elephant icon) on right panel, click Execute Gradle task (Program icon) and type `signingReport` and hit enter
   - Copy the `SHA1` from the console output (look for generic 'debug' key).

2. **Add to Firebase**:
   - Go to Firebase Console -> **Project Settings** (Gear icon).
   - Scroll down to **Your apps**.
   - Click **Add fingerprint**.
   - Paste the SHA-1 key.
   - Click **Save**.

3. **Update Configuration**:
   - **Re-download `google-services.json`**.
   - Replace the old file in `app/google-services.json`.
   - This updated file contains the `client_id` required for Google Sign-In.

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

If you see `Error 12500 / 10`: It means SHA-1 is missing or wrong.