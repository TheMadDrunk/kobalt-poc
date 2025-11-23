# Kobalt POC: Kotlin/JavaFX + SolidJS Bridge

This Proof of Concept (POC) demonstrates a bidirectional communication bridge between a Kotlin/JavaFX backend and a SolidJS frontend running within a `WebView`.

## Architecture Overview

The project is divided into two main modules:
1.  **Backend (`back`)**: A Kotlin application using JavaFX to host a `WebView`.
2.  **Frontend (`frontend`)**: A SolidJS application built with Vite, running inside the `WebView`.

### Interaction Flow

The communication happens via a bridge injected into the JavaScript `window` object.

1.  **Kotlin -> JavaScript**:
    *   The backend calls `window.receiveMessage(jsonString)` in the `WebView`.
    *   The frontend registers this global function to handle incoming messages.

2.  **JavaScript -> Kotlin**:
    *   The backend injects a Java object (mapped to `window.app`) into the `WebView`.
    *   The frontend calls `window.app.sendMessage(jsonString)` to send data back to Kotlin.

## Components

### Backend (Kotlin)

Located in `back/src/main/kotlin/org/kobalt/`:

*   **`Main.kt`**: The entry point. It sets up the JavaFX `Stage`, creates the `WebView`, and loads the frontend. It initializes the `Bridge` and `MessageService` once the page is loaded.
*   **`Bridge.kt`**: Handles the low-level communication.
    *   `sendToJavaScript(message)`: Executes JavaScript in the `WebView`.
    *   `sendMessage(message)`: Called by JavaScript to log messages in the backend.
*   **`MessageService.kt`**: A service that simulates backend activity. It schedules random "PING" messages to be sent to the frontend.

### Frontend (SolidJS)

Located in `frontend/src/`:

*   **`bridge.ts`**: Encapsulates the bridge logic.
    *   `useBridge()`: A SolidJS hook that provides `sendMessage` and reactive state (`pings`, `lastMessage`).
    *   It registers `window.receiveMessage` to update the reactive state when data arrives from Kotlin.
*   **`App.tsx`**: The main UI component. It uses `useBridge` to display ping counts and send click events back to the backend.

## Getting Started

### Prerequisites
-   JDK 21
-   Node.js & npm

### Build and Run

1.  **Build the Frontend**:
    ```bash
    cd frontend
    npm install
    npm run build
    ```
    This compiles the SolidJS app and outputs the files to `../back/src/main/resources/web`, making them available to the backend.

2.  **Run the Backend**:
    ```bash
    cd back
    ./gradlew run
    ```
    This starts the JavaFX application. You should see the SolidJS app loaded in the window.

### Verification
-   **Backend -> Frontend**: Watch the "number of pings" counter in the UI increment as the backend sends random messages.
-   **Frontend -> Backend**: Click the "count is..." button and check the terminal running the backend. You should see logs like `LOG: (sent message) : ...`.
